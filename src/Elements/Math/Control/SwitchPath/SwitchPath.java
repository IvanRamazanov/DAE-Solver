package Elements.Math.Control.SwitchPath;

import ElementBase.MathElement;
import Elements.Environment.Subsystem.Subsystem;

import java.util.List;

public class SwitchPath extends MathElement{

    ScalarParameter threshold;

    public SwitchPath(Subsystem sys){
        super(sys);

        addMathContact('i');
        addMathContact('i');
        addMathContact('i');
        addMathContact('o');
    }

    public SwitchPath(boolean val){
        super(val);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        boolean flag=getInputs().get(1).getValue().get(0) > threshold.getValue();
        if(flag)
            return getInputs().get(0).getValue();
        else
            return getInputs().get(2).getValue();
    }

    @Override
    protected String getDescription() {
        return "If second input's value bigger, than threshold, then first input passes.";
    }

    @Override
    protected void setParams() {
        getParameters().add(threshold=new ScalarParameter("Threshold",0.0));

        setName("Switch");
    }
}
