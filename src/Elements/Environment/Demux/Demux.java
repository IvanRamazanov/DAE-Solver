package Elements.Environment.Demux;

import ElementBase.MathElement;
import Elements.Environment.Subsystem.Subsystem;

import java.util.ArrayList;
import java.util.List;

public class Demux extends MathElement{

    public Demux(Subsystem sys){
        super(sys);

        addMathContact('i');
        addMathContact('o');
        addMathContact('o');
        addMathContact('o');
    }

    public Demux(boolean val){
        super(val);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        final List<Double> out=new ArrayList<>();
        out.add(getInputs().get(0).getValue().get(outIndex));
        return out;
    }

    @Override
    protected String getDescription() {
        return "azaz";
    }

    @Override
    protected void setParams() {
        setName("demux");
    }
}
