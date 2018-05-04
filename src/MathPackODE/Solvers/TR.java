package MathPackODE.Solvers;

import MathPack.MatrixEqu;
import MathPack.StringGraph;
import MathPack.WorkSpace;
import MathPackODE.Solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TR extends Solver {
    private double[] x0,newtonF,df0,df1;
    private double[][] dfdx;
    private double tolerance=1e-6;

    @Override
    public void evalNextStep() {
        int cnt=0;
        double t0=time.getValue();
        time.set(t0+dt);
        //  TR step
        estimJ(dfdx, null, df0, Xvector, null); // estimate d(d.X)/dX
        makeTRJ(dfdx);

        while(true){

            trF();
            double err = MatrixEqu.norm(newtonF);
            while (err > tolerance) {
                cnt++;
                MatrixEqu.solveLU(dfdx, newtonF); //  dF*deltaX=f0
                for (int i = 0; i < diffRank; i++) {
                    double val = Xvector.get(i).getValue() - newtonF[i];
                    Xvector.get(i).set(val);
//                    xng[i] = val;
                }

                evalSysState();
                trF(); // eval F(X)

                err = MatrixEqu.norm(newtonF);
                if(cnt>100)
                    throw new Error("Bad TR Newton!");
                if(cnt>20){
                    estimJ(dfdx, null, df1, Xvector, null); // estimate d(d.X)/dX
                    makeTRJ(dfdx);
                    continue;
                }

                if (err > tolerance) {
//                for (int i = 0; i < diffRank; i++)
//                    del[i] *= -1.0;
//
//                updateJ(dF, F, f0, del);
//
//                del = Arrays.copyOf(F, diffRank);

                    //update J
//                    estimJ(dfdx, null, df1, Xvector, null); // estimate d(d.X)/dX
//                    makeTRJ(dfdx);
                } else {
                    break;
                }
            }



//            //err
//            err = 0;
//            double norm = 0;
//            for (int i = 0; i < diffRank; i++) {
//                norm += xn[i] * xn[i];
//                double v = 2.0 * kGamma * dt * (1.0 / gamma * fn[i] - fng[i] / gamma / (1.0 - gamma) + fnn[i] / (1.0 - gamma));
//                err += v * v;
//            }
//            norm = sqrt(norm);
//            err = sqrt(err);
//            double r = err / (relTol * norm + 1e-6);
//            if (r > 2) {
//                dt /= 10.0;
//                time.set(t0);
//
//                if(dt<hmin){
//                    throw new Error("Stepsize too small: "+dt+", at t="+t0);
//                }
//                for(int i=0;i<diffRank;i++){
//                    Xvector.get(i).set(xn[i]);
//                }
//                //evalSysState();
//
////                //update J
//                estimJ(dF, null, fn, Xvector, null); // estimate d(d.X)/dX
//                makeBDFJ(dF);
//
//            } else {
////                dt/=max(pow(r,1.0/4.0),0.4);
//                double temp = 1.25*pow(err/relTol,1.0/3.0);
//                if (temp > 0.2)
//                    dt = dt / temp;
//                else
//                    dt = 5.0*dt;
//                dt=min(dt,hmax);
//                break;
//            }
            break;
        }
        copyArray(Xvector,x0);
        df0=Arrays.copyOf(df1,diffRank);
//        if(!isErrnous)
//            evalSysState();
        //time+=dt;
    }

    /**
     * fng=dXvector
     * xng=Xvector
     */
    void trF(){
        for (int i = 0; i < diffRank; i++) {
            df1[i]=dXvector.get(i).getValue();
            newtonF[i] = (Xvector.get(i).getValue()-x0[i])-dt/2.0*(df1[i]+df0[i]);
        }
    }

    /**
     * Estimates d(d.X)/dX
     * @param J - output
     * @param fx - target function
     * @param f0 - function value at fx(x0)
     * @param x - x0 initial point
     * @param fac
     */
    @Override
    protected void estimJ(double[][] J, List<StringGraph> fx, double[] f0, ArrayList<WorkSpace.Variable> x, double[] fac){
        int ny=f0.length;
        for(int i=0;i<ny;i++){
            double x0=x.get(i).getValue(),
                    del=x0*1e-3;
            del=del==0?1e-6:del;
            x.get(i).set(x0+del);

            //evalSysState();

            for(int j=0;j<ny;j++){
                J[j][i]=(dXvector.get(j).getValue()-f0[j])/del;
            }

            x.get(i).set(x0);
        }
    }

    private void makeTRJ(double[][] J){
        for(int i=0;i<diffRank;i++){
            for(int j=0;j<diffRank;j++){
                double add=i==j?1.0:0.0;
                J[i][j]=add-dt/2.0*J[i][j];
            }
        }
    }

    @Override
    protected void selfInit(){
        x0=new double[diffRank];
        copyArray(Xvector,x0);

        df0=new double[diffRank];
        copyArray(dXvector,df0);
        df1=new double[diffRank];

        newtonF=new double[diffRank];

        dfdx=new double[diffRank][diffRank];

    }
}
