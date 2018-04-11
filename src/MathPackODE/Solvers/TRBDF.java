package MathPackODE.Solvers;

import MathPack.MatrixEqu;
import MathPack.StringGraph;
import MathPack.WorkSpace;
import MathPackODE.Solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.StrictMath.*;

public class TRBDF extends Solver{
    double[] xn,
            newtonF,
            fn, // dx/dt=f(xn)=fn
            fng, // dx/dt=f(xng)=fng
            fnn,
            xng;
    double[][] dF;
    double tolerance=1e-6,
            gamma=2.0-sqrt(2.0),
            hmax=1e-3,
            hmin=1e-16,
    //            gamma=1,
    kGamma=(-3.0*gamma*gamma+4.0*gamma-2.0)/(12.0*(2.0-gamma));

    @Override
    public void evalNextStep() {
        double t0=time;
        time+=gamma*dt;
        //  TR step
        estimJ(dF, null, fn, Xvector, null); // estimate d(d.X)/dX
        makeTRJ(dF);

        while(true){
            trF();
            double err = MatrixEqu.norm(newtonF);
            while (err > tolerance) {
                MatrixEqu.solveLU(dF, newtonF); //  dF*deltaX=f0
                for (int i = 0; i < diffRank; i++) {
                    double val = Xvector.get(i).getValue() + newtonF[i];
                    Xvector.get(i).set(val);
                    xng[i] = val;
                }

                evalSysState();
                trF(); // eval F(X)

                err = MatrixEqu.norm(newtonF);
                if (err > tolerance) {
//                for (int i = 0; i < diffRank; i++)
//                    del[i] *= -1.0;
//
//                updateJ(dF, F, f0, del);
//
//                del = Arrays.copyOf(F, diffRank);

                    //update J
                    estimJ(dF, null, fng, Xvector, null); // estimate d(d.X)/dX
                    makeTRJ(dF);
                } else {
                    break;
                }
            }

            time=t0+dt;
            // BDF2 step
            estimJ(dF, null, fng, Xvector, null); // estimate d(d.X)/dX
            makeBDFJ(dF);
            bdfF();
            err = MatrixEqu.norm(newtonF);
            while (err > tolerance) {
                MatrixEqu.solveLU(dF, newtonF); //  dF*deltaX=f0
                for (int i = 0; i < diffRank; i++) {
                    Xvector.get(i).set(Xvector.get(i).getValue() + newtonF[i]);
                }

                evalSysState();
                bdfF(); // eval F(X)

                err = MatrixEqu.norm(newtonF);
                if (err > tolerance) {
//                for (int i = 0; i < diffRank; i++)
//                    del[i] *= -1.0;
//
//                updateJ(dF, F, f0, del);
//
//                del = Arrays.copyOf(F, diffRank);


                    //update J
                    estimJ(dF, null, fng, Xvector, null); // estimate d(d.X)/dX
                    makeBDFJ(dF);
                } else {
                    break;
                }
            }

            //err
            err = 0;
            double norm = 0;
            for (int i = 0; i < diffRank; i++) {
                norm += xn[i] * xn[i];
                double v = 2.0 * kGamma * dt * (1.0 / gamma * fn[i] - fng[i] / gamma / (1.0 - gamma) + fnn[i] / (1.0 - gamma));
                err += v * v;
            }
            norm = sqrt(norm);
            err = sqrt(err);
            double r = err / (relTol * norm + 1e-6);
            if (r > 2) {
                dt /= 10.0;
                time=t0;

                if(dt<hmin){
                    throw new Error("Stepsize too small: "+dt+", at t="+t0);
                }
                for(int i=0;i<diffRank;i++){
                    Xvector.get(i).set(xn[i]);
                }
                //evalSysState();

//                //update J
                estimJ(dF, null, fn, Xvector, null); // estimate d(d.X)/dX
                makeBDFJ(dF);

            } else {
//                dt/=max(pow(r,1.0/4.0),0.4);
                double temp = 1.25*pow(err/relTol,1.0/3.0);
                if (temp > 0.2)
                    dt = dt / temp;
                else
                    dt = 5.0*dt;
                dt=min(dt,hmax);
                break;
            }

        }
        copyArray(Xvector,xn);
        fn=Arrays.copyOf(fnn,diffRank);
//        if(!isErrnous)
//            evalSysState();
        //time+=dt;
    }

    @Override
    protected void selfInit(){
        xn=new double[diffRank];
        xng=new double[diffRank];
        copyArray(Xvector,xn);

        fn=new double[diffRank];
        copyArray(dXvector,fn);
        fng=new double[diffRank];
        fnn=new double[diffRank];

        newtonF=new double[diffRank];

        dF=new double[diffRank][diffRank];

    }

    /**
     * fng=dXvector
     * xng=Xvector
     */
    void trF(){
        for (int i = 0; i < diffRank; i++) {
            fng[i]=dXvector.get(i).getValue();
            newtonF[i] = gamma*dt/2.0*(fng[i]+fn[i])-(Xvector.get(i).getValue()-xn[i]);
        }
    }

    /**
     * x_(n+1)=Xvector
     */
    void bdfF(){
        for (int i = 0; i < diffRank; i++) {
            fnn[i]=dXvector.get(i).getValue();
            newtonF[i] = -(Xvector.get(i).getValue()-xng[i]/(gamma*(2.0-gamma))+xn[i]*(1.0-gamma)*(1.0-gamma)/gamma/(2.0-gamma))
                    +(1.0-gamma)/(2.0-gamma)*dt*fnn[i];
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

            evalSysState();

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
                J[i][j]=add-gamma*dt/2.0*J[i][j];
            }
        }
    }
    private void makeBDFJ(double[][] J){
        for(int i=0;i<diffRank;i++){
            for(int j=0;j<diffRank;j++){
                double add=i==j?1.0:0.0;
//                J[i][j]=add-(1.0-gamma)/(2.0-gamma)*dt*J[i][j];
                J[i][j]=add-(1.0-gamma)/(2.0-gamma)*dt*-4.0;
            }
        }
    }
}