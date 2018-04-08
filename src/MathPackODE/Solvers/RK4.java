package MathPackODE.Solvers;

import MathPack.WorkSpace;
import MathPackODE.Solver;

import java.util.List;

public class RK4 extends Solver {
    private double bTime;
    private double[] bXVector,
            C={0, 1.0/2.0, 1.0/2.0, 1.0},
            B={1.0/6.0, 1.0/3.0, 1.0/3.0, 1.0/6.0};
    private final int ORDER=4;
    private final double[][] A={
            {1.0/2.0, 0, 0},
            {0, 1.0/2.0, 0},
            {0, 0, 1}
    };
    private double[][] K;

    public RK4(){}

    @Override
    public void evalNextStep() {
        bTime=time;
        copyArray(Xvector,bXVector);  //save X(t0)

        // first step
        copyArray(dXvector,K[0]);  //save K1=dXi(t0+h)
        // n-th step
        for(int i=1;i<ORDER;i++) {
            add(i);
            evalSysState();
            copyArray(dXvector, K[i]);
        }

        for(int i=0;i<diffRank;i++){
            double sum=0;
            for(int j=0;j<K.length;j++) {
                sum += B[j] * K[j][i];
            }
            Xvector.get(i).set(bXVector[i]+dt*sum);
        }

        time=bTime+dt;

        evalSysState(); // for correct outputs
    }

    private void add(int step){
        time=bTime+C[step]*dt;

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

        K=new double[ORDER][diffRank];
    }
}






