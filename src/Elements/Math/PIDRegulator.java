package Elements.Math;

import ElementBase.DynamMathElem;
import MathPackODE.Solver;

import java.util.ArrayList;
import java.util.List;

public class PIDRegulator extends DynamMathElem{
    private Parameter Ki,Kp,Kd;
    private Double x_diff,time_old;
    private double input;

    public PIDRegulator(){
        super();
        addMathContact('i');
        addMathContact('o');
    }

    public PIDRegulator(boolean val){
        super(val);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        input=getInputs().get(0).getValue().get(0);
        List<Double> out=new ArrayList<>();
        if(x_diff==null){ // first step
            x_diff=Double.valueOf(input);
            time_old=Double.valueOf(Solver.time+1);
        }
        double kp=Kp.getDoubleValue()*input,
                kd=Kd.getDoubleValue()*(input-x_diff)/(Solver.time-time_old),
                ki=Ki.getDoubleValue()*X_old.get(0);
        out.add(kp+ki+kd);
        return out;
    }

    @Override
    protected String getDescription() {
        return "This block represents PID-controller";
    }

    @Override
    public void init(){
        super.init();
        x_diff=null;
        time_old=null;
    }

    @Override
    public void updateOutputs(Solver solver){
        super.updateOutputs(solver);
        x_diff=input;
        time_old=solver.time;
    }

    @Override
    protected void setParams() {
        Kp=new Parameter("Proportional coefficient",1);
        Ki=new Parameter("Integration coefficient",1);
        Kd=new Parameter("Differentiation coefficient",1);
        getParameters().add(Kp);
        getParameters().add(Ki);
        getParameters().add(Kd);
    }
}
