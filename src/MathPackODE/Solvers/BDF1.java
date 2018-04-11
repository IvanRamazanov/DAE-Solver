package MathPackODE.Solvers;

import MathPack.MatrixEqu;
import MathPack.StringGraph;
import MathPack.WorkSpace;
import MathPackODE.Solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BDF1 extends Solver {
    double[] temp,F;
    double[][] dF;
    double tolerance=1e-6;

    @Override
    public void evalNextStep() {
        copyArray(Xvector,temp); //save X_(n-1)
        newtonF(); // eval F(X)

        boolean isErrnous=false;
        while(MatrixEqu.norm(F)>tolerance){
            isErrnous=true;
            double[] f0=Arrays.copyOf(F,F.length);

            estimJ(dF,null,f0,Xvector,null);

            MatrixEqu.solveLU(dF,f0); // f0 = deltaX

            for(int i=0;i<diffRank;i++){
                Xvector.get(i).set(Xvector.get(i).getValue()-f0[i]);
            }

            evalSysState();
            newtonF(); // eval F(X)
        }
        if(!isErrnous)
            evalSysState();
        time+=dt;
    }

    @Override
    protected void selfInit(){
        temp=new double[diffRank];
        copyArray(Xvector,temp);

        F=new double[diffRank];
        dF=new double[diffRank][diffRank];
    }

    void newtonF(){

        for(int i=0;i<diffRank;i++){
            F[i]=temp[i]-Xvector.get(i).getValue()+dt*dXvector.get(i).getValue();
        }
    }

    @Override
    protected void estimJ(double[][] J, List<StringGraph> fx, double[] f0, ArrayList<WorkSpace.Variable> x, double[] fac){
        int ny=f0.length;
        for(int i=0;i<ny;i++){
            double x0=x.get(i).getValue(),
                    del=x0*1e-3;
            del=del==0?1e-6:del;
            x.get(i).set(x0+del);

            evalSysState();
            newtonF();

            for(int j=0;j<ny;j++){
                J[j][i]=(F[j]-f0[j])/del;
            }

            x.get(i).set(x0);
        }
    }
}
