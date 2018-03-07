package Elements.Math;

import ElementBase.MathElement;
import ElementBase.Updatable;

import java.util.ArrayList;
import java.util.List;

public class Delay extends MathElement implements Updatable{
    private List<Double> tmp=new ArrayList<>(),
                        out=new ArrayList<>();;

    public Delay(){
        super();
        addMathContact('i');
        addMathContact('o');
    }

    public Delay(boolean val){
        super(val);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        return out;
    }

    @Override
    protected String getDescription() {
        return "Zero-hold";
    }

    @Override
    protected void setParams() {
        setName("Delay");
    }

    @Override
    public void init(){
        out.clear();
        out.add(0.0);
    }

    @Override
    public void preEvaluate(double time) {
        tmp=getInputs().get(0).getValue();
    }

    @Override
    public void postEvaluate(double time) {
        out=tmp;
    }
}
