package Elements.Math;

import ElementBase.DynamMathElem;
import MathPackODE.Solver;

import java.util.ArrayList;
import java.util.List;

public class Delay extends DynamMathElem{
    List<Double> tmp=new ArrayList<>();

    public Delay(){
        super();
        addMathContact('i');
        addMathContact('o');

        tmp.add(0.0);
    }

    public Delay(boolean val){
        super(val);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        return tmp;
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
    public void updateOutputs(Solver solver){
        tmp=getInputs().get(0).getValue();
    }
}
