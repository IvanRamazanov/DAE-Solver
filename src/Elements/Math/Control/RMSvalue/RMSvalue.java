package Elements.Math.Control.RMSvalue;

import ElementBase.DynamMathElem;
import Elements.Environment.Subsystem.Subsystem;
import MathPack.WorkSpace;
import MathPackODE.Solver;

import java.util.ArrayList;
import java.util.List;

public class RMSvalue extends DynamMathElem{

    public RMSvalue(Subsystem sys){
        super(sys);

        addMathContact('i');
        addMathContact('o');
    }

    public RMSvalue(boolean val){
        super(val);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        List<Double> out=new ArrayList<>();
        for(WorkSpace.Variable v:wsX) {
            if(Solver.time.getValue()!=0)
                out.add(Math.sqrt(v.getValue() / Solver.time.getValue()));
            else
                out.add(Math.sqrt(v.getValue()));
        }
        return out;
    }

    @Override
    public void evalDerivatives(){
        List<Double> res=getInputs().get(0).getValue();
        for(int i=0;i<res.size();i++){
            wsDX.get(i).set(res.get(i)*res.get(i));
        }
    }

    @Override
    protected String getDescription() {
        return "RMS value";
    }

    @Override
    protected void setParams() {
        setName("RMS");
    }
}
