package Elements.Math.Control.ABCtoAB0;

import ElementBase.MathElement;
import Elements.Environment.Subsystem.Subsystem;

import java.util.ArrayList;
import java.util.List;

public class ABCtoAB0 extends MathElement{
    public ABCtoAB0(Subsystem sys){
        super(sys);

        addMathContact('i');//abc
        addMathContact('o');//dq0
    }

    public ABCtoAB0(boolean val){
        super(val);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        final List<Double> out=new ArrayList<>(3),
                in=getInputs().get(0).getValue();
        double  a=in.get(0),b=in.get(1),c=in.get(2);
        out.add(2.0/3.0*a-1.0/3.0*b-1.0/3.0*c);
        out.add(0.5773502691896257*b-0.5773502691896257*c);
        out.add(1.0/3.0*(a+b+c));
        return out;
    }

    @Override
    protected String getDescription() {
        return "Park transformation from ABC into AlphaBeta0";
    }

    @Override
    protected void setParams() {
        setName("abc-ab0");
    }
}
