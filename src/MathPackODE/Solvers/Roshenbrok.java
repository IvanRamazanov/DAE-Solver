package MathPackODE.Solvers;

import MathPack.StringGraph;
import MathPack.WorkSpace;
import MathPackODE.Solver;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;
import static java.lang.Math.abs;

public class Roshenbrok extends Solver {
    private double[] dfdt,x0,F0,F1,F2,k1,k2,k3;
    private double[][] W,dfdx;
    private double[] fac;
    private final double d=1.0/(2.0+sqrt(2.0)),
                    e32=6+sqrt(2.0),
                    eps= ulp(1.0),
                    sqrtEps=sqrt(eps),
            hmin=16.0*eps*tEnd,
            hmax=1e-5,
            pow=1.0/3.0;

    public Roshenbrok(){}

    @Override
    public void evalNextStep() {
        double t0=time;

        // F0 exist
        estimJ(dfdx,algSystem,F0,Xvector,fac); //TODO not correct estimJ!
        evalW();

        // prepare k1 to use as an output
        estimT(); //estimate dfdt

        boolean noFailed=true;
        double error;
        while(true) {

            for (int i = 0; i < diffRank; i++) {
                k1[i] = F0[i] + dt * d * dfdt[i];
            }

            //eval k1
            MathPack.MatrixEqu.solveLU(W, k1);

            //eval F1=F(t+0.5*dt, x+0.5*dt*k1)
            time =t0 + 0.5 * dt;
            for (int i = 0; i < diffRank; i++)
                Xvector.get(i).set(x0[i] + 0.5 * dt * k1[i]);
            evalSysState();
            for (int i = 0; i < diffRank; i++)
                F1[i] = dXvector.get(i).getValue();

            // prepare k2
            for (int i = 0; i < diffRank; i++)
                k2[i] = F1[i] - k1[i];
            //eval k2
            MathPack.MatrixEqu.solveLU(W, k2);
            for (int i = 0; i < diffRank; i++)
                k2[i] += k1[i];

            // eval x_(n+1)


            //eval F2=F(t_(n+1), x_(n+1))
            for (int i = 0; i < diffRank; i++) {
                double val = x0[i] + dt * k2[i];
                Xvector.get(i).set(val);
            }
            time = t0 + dt;
            evalSysState();
            for (int i = 0; i < diffRank; i++)
                F2[i] = dXvector.get(i).getValue();

            //eval k3
            for (int i = 0; i < diffRank; i++)
                k3[i] = F2[i] - e32 * (k2[i] - F1[i]) - 2.0 * (k1[i] - F0[i]) + dt * d * dfdt[i];
            MathPack.MatrixEqu.solveLU(W, k3);

            error = error();

            if (error > relTol) {
                if (dt <= hmin)
                    throw new Error("Step size smaller, than dt_min");

                dt = max(hmin, dt * max(0.1, 0.8 * pow(relTol / error, pow)));
                noFailed=false;
            } else {
                for (int i = 0; i < diffRank; i++) {
                    F0[i] = F2[i];
                    x0[i] = Xvector.get(i).getValue();
                }
                break;
            }
        }

        if(noFailed){
            double temp = 1.25*pow(error/relTol,pow);
            if (temp > 0.2)
                dt = dt / temp;
            else
                dt = 5.0*dt;
        }
        dt=min(dt,hmax);
    }

    @Override
    protected void selfInit(){
        dfdt=new double[diffRank];
        dfdx=new double[diffRank][diffRank];
        W=new double[diffRank][diffRank];
        x0=new double[diffRank];
        F0=new double[diffRank];
        F1=new double[diffRank];
        F2=new double[diffRank];
        k1=new double[diffRank];
        k2=new double[diffRank];
        k3=new double[diffRank];
        fac=new double[diffRank];

        for(int i=0;i<diffRank;i++) {
//            fac[i]=sqrtEps;
            fac[i]=1e10;
        }
        for(int j=0;j<diffRank;j++) {
            F0[j]=dXvector.get(j).getValue();
            x0[j]=Xvector.get(j).getValue();
        }
    }

    /**
     * x0 must be set
     */
    private void estimT(){
        //dfdt=(f1-f0)/tdel
        //    tdel = (t + min(sqrt(eps)*(t+dt),dt)) - t;
        double t0=time,
                tdel=(t0+min(sqrtEps*(t0+dt),dt))-t0;

        time+=tdel; // frankly, must be +=tdel
        evalSysState();

        for(int i=0;i<diffRank;i++) {
            F1[i] = dXvector.get(i).getValue();
            dfdt[i]=(F1[i]-F0[i])/tdel;
        }
        time=t0;
    }

    private double error(){
        double sum=0.0;
        for(int i=0;i<diffRank;i++)
            sum+=(k1[i] - 2.0 * k2[i] + k3[i])*(k1[i] - 2.0 * k2[i] + k3[i]);
        sum=sqrt(sum);
        sum=dt / 6.0 * sum;
        return sum;
    }

    private void evalW(){
        for(int i=0;i<diffRank;i++){
            for(int j=0;j<diffRank;j++){
                W[i][j]=-1.0*dt*d*dfdx[i][j];

                if(i==j)
                    W[i][j]+=1.0;
            }
        }
    }

    @Override
    /**
     * Evaluates estimation of Jacobian of fx near f0=fx(x0) point.
     * @param J - output
     * @param fx - target function
     * @param f0 - function value at fx(x0)
     * @param vars - x0 initial point
     * @param fac
     */
    protected void estimJ(double[][] J, List<StringGraph> fx, double[] f0, ArrayList<WorkSpace.Variable> x, double[] fac){
        final double eps=Math.ulp(1.0),
                br=pow(eps,0.875),
                bl=pow(eps,0.75),
                bu=pow(eps,0.25),
                facmin=pow(eps,0.98)*1e-10,
                facmax=1e15;
        int ny=diffRank;
        double[] f1=new double[ny],x0=new double[ny];
        for(int i=0;i<ny;i++){
            x0[i]= x.get(i).getValue();
        }

        // row cycle
        int i=0;
        while(i<ny){

            double yScale=max(abs(x0[i]),0.0); //?
            yScale=yScale==0.0?160*Math.ulp(1.0):yScale;

            double del=fac[i]*abs(yScale);
            x.get(i).set(x0[i]+del);  //shift X

            evalSysState();

            for(int m=0;m<ny;m++) {
//                f1[m] = fx.get(m).evaluate(this.vars);
                f1[m] = dXvector.get(m).getValue();
            }
            double maxDiff=-1.0;
            int max=-1;

            for(int j=0;j<ny;j++){
//                f1[j]=dXvector.get(j).getValue();

                double aVal=abs(f1[j]-f0[j]);
                if(aVal>maxDiff) {
                    maxDiff=aVal;
                    max = j;
                }
            }

            double scale=max(abs(f1[max]),abs(f0[max]));

            boolean confirm=false;
            if(min(abs(f1[max]),abs(f0[max]))==0) {
                confirm=true;
            }else if(maxDiff>bu*scale){
                fac[i]=max(0.013*fac[i],facmin);
                if(fac[i]==facmin)
                    confirm=true;
            }else if(maxDiff<=bl*scale){
                fac[i]=min(11*fac[i],facmax);
                if(fac[i]==facmax)
                    confirm=true;
            }else if(maxDiff<br*scale){
                System.out.println("bad!");
            }else{
                confirm=true;
            }
            if(confirm) {
                for (int j = 0; j < ny; j++) {
                    J[j][i] = (f1[j] - f0[j]) / del;
                }
                x.get(i).set(x0[i]);
                i++;
            }
        }
    }
}
