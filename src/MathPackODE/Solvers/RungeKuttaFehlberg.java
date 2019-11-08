package MathPackODE.Solvers;

import MathPackODE.Solver;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class RungeKuttaFehlberg extends Solver {
    private double bTime;
    private double[] bXVector,error,relErr,bCommVect,
    C={0, 1.0/4.0, 3.0/8.0, 12.0/13.0, 1.0, 1.0/2.0},
    B={16.0/135.0, 0, 6656.0/12825.0, 28561.0/56430.0, -9.0/50.0, 2.0/55.0},
    Br={25.0/216.0, 0, 1408.0/2565.0, 2197.0/4104.0, -1.0/5.0, 0};
    private final int ORDER=6;
    private final double[][] A={
            {1.0/4.0,0,0,0,0},
            {3.0/32.0, 9.0/32.0, 0, 0, 0},
            {1932.0/2197.0, -7200.0/2197.0, 7296.0/2197.0, 0,0},
            {439.0/216.0, -8, 3680.0/513.0, -845.0/4104.0, 0},
            {-8.0/27.0, 2, -3544.0/2565.0, 1859.0/4104.0, -11/40}
    };
    private double[][] K;

    public RungeKuttaFehlberg(){

    }

    @Override
    public void evalNextStep() {
        bTime=time.getValue();
        copyArray(Xvector,bXVector);  //save X(t0)
        copyArray(commonVarsVector,bCommVect);

        // first step
        copyArray(dXvector,K[0]);  //save K1=dXi(t0+h)
        // n-th step
        for(int i=1;i<ORDER;i++) {
            add(i);
            evalSysState();
            copyArray(dXvector, K[i]);
        }

        double denum=1e-3;
        for(int i=0;i<diffRank;i++){
            double sum=0,errSum=0;
            for(int j=0;j<K.length;j++) {
                sum += B[j] * K[j][i];
                errSum += abs((B[j]-Br[j])*K[j][i]);
            }
            double val=bXVector[i]+dt*sum;
            denum=max(max(abs(val),abs(Xvector.get(i).getValue())),denum);
            Xvector.get(i).set(val);


            error[i]=errSum*dt;

            relErr[i]=error[i];
        }

        double err=-1;
        for(int i=0;i<relErr.length;i++){
            err=max(relErr[i]/denum,err);
        }
//        double err= MatrixEqu.norm(error);

//        double alpha = Math.pow(absTol/err,1.0/(ORDER+1.0));
        double alpha =1.0 / ( 1.25 * Math.pow(err/relTol,1.0/(5.0)) );
        if(err>relTol){
            //Bad try. Recalculate
            time.set(bTime);
            for(int i=0;i<diffRank;i++){
                Xvector.get(i).set(bXVector[i]);
            }
            for(int i=0;i<bCommVect.length;i++)
                commonVarsVector.get(i).set(bCommVect[i]);

            alpha*=0.8;
            dt=alpha*dt;
        }else{
            time.set(bTime+dt);
            dt=alpha*dt;

            evalSysState(); // for correct outputs
        }

    }

    private void add(int step){
        time.set(bTime+C[step]*dt);

        for(int i=0;i<diffRank;i++){
            double sum=0;
            for(int j=0;j<step;j++){
                sum+=K[j][i]*A[step-1][j];
            }
            Xvector.get(i).set(bXVector[i]+dt*sum);
        }
    }

    @Override
    protected void selfInit(){
        bXVector=new double[diffRank];
        bCommVect=new double[commonVarsVector.size()];

        K=new double[ORDER][diffRank];
        error=new double[diffRank];
        relErr=new double[diffRank];
    }
}
