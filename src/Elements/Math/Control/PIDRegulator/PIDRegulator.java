package Elements.Math.Control.PIDRegulator;

import ElementBase.DynamMathElem;
import ElementBase.Updatable;
import Elements.Environment.Subsystem.Subsystem;
import MathPackODE.Solver;

import java.util.ArrayList;
import java.util.List;

public class PIDRegulator extends DynamMathElem implements Updatable{
    private ScalarParameter Ki,Kp,Kd;
    private Double x_diff,time_old;
    private double input;

    public PIDRegulator(Subsystem sys){
        super(sys);
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
        double kp=Kp.getValue()*input,
                kd=Kd.getValue()*(input-x_diff)/(Solver.time-time_old),
                ki=Ki.getValue()*wsX.get(0).getValue();
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
//        x_diff=null;
//        time_old=null;
        x_diff=Double.valueOf(input);
        time_old=Double.valueOf(Solver.time+1);
    }

    @Override
    protected void setParams() {
        Kp=new ScalarParameter("Proportional coefficient",1);
        Ki=new ScalarParameter("Integration coefficient",1);
        Kd=new ScalarParameter("Differentiation coefficient",0);
        getParameters().add(Kp);
        getParameters().add(Ki);
        getParameters().add(Kd);

        setName("PID regulator");
    }

    @Override
    public void preEvaluate(double time) {
//        x_diff=input;
//        time_old=Solver.time;
        x_diff=input;
        time_old=time;
    }

    @Override
    public void postEvaluate(double time) {
//        x_diff=input;
//        time_old=time;
    }

    @Override
    public void evalDerivatives(){
        List<Double> res=getInputs().get(0).getValue();
        for(int i=0;i<res.size();i++){
            wsDX.get(i).set(res.get(i));
        }
    }
}
