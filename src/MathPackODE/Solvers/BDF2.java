package MathPackODE.Solvers;

import MathPack.MatrixEqu;
import MathPack.StringGraph;
import MathPack.WorkSpace;
import MathPackODE.Solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BDF2 extends Solver {
    double[] xTemp,F,xOld;
    double[][] dF;
    double tolerance=1e-7;

    @Override
    public void evalNextStep() {
        time.set(time.getValue()+dt);

        newtonF(); // eval F(X)
        double[] del = Arrays.copyOf(F, diffRank);
        estimJ(dF,null,del,Xvector,null);

//        double err=MatrixEqu.norm(F);
        double err;
        do {
            double[] f0 = Arrays.copyOf(F, diffRank);


            MatrixEqu.solveLU(dF, del); //  dF*deltaX=f0

            for (int i = 0; i < diffRank; i++) {
                Xvector.get(i).set(Xvector.get(i).getValue() - del[i]);
            }

            evalSysState();
            newtonF(); // eval F(X)

            err = MatrixEqu.norm(F);
            if (err > tolerance){
                for (int i = 0; i < diffRank; i++)
                    del[i] *= -1.0;

                updateJ(dF, F, f0, del);

                del = Arrays.copyOf(F, diffRank);
            }else{

                break;
            }
        }while(err > tolerance);

        xOld=Arrays.copyOf(xTemp,diffRank); //save X_(n-2)
        copyArray(Xvector,xTemp); //save X_(n-1)

    }

    @Override
    protected void selfInit(){
        xTemp=new double[diffRank];
        copyArray(Xvector,xTemp);

        F=new double[diffRank];
        dF=new double[diffRank][diffRank];
    }

    void newtonF(){
        if(xOld==null) {
            for (int i = 0; i < diffRank; i++) {
                F[i] = xTemp[i] - Xvector.get(i).getValue() + dt * dXvector.get(i).getValue();
            }
        }else{
            for (int i = 0; i < diffRank; i++) {
                F[i] = 4.0/3.0*xTemp[i] - Xvector.get(i).getValue() -1.0/3.0*xOld[i] + 2.0/3.0* dt * dXvector.get(i).getValue();
            }
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

