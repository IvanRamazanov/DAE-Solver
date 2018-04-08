package Elements.Math.Control.ABCtoDQ;

import ElementBase.MathElement;
import Elements.Environment.Subsystem.Subsystem;

import java.util.ArrayList;
import java.util.List;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;

public class ABCtoDQ extends MathElement{
    public ABCtoDQ(Subsystem sys){
        super(sys);

        addMathContact('i');//abc
        addMathContact('i');//theta
        addMathContact('o');//dq0
    }

    public ABCtoDQ(boolean val){
        super(val);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        final List<Double> out=new ArrayList<>(3),
        in=getInputs().get(0).getValue();
        double th=getInputs().get(1).getValue().get(0),
        a=in.get(0),b=in.get(1),c=in.get(2);
        out.add(2.0/3.0*(cos(th)*a+cos(th-2.0/3.0*PI)*b+cos(th+2.0/3.0*PI)*c));
        out.add(2.0/3.0*(-sin(th)*a-sin(th-2.0/3.0*PI)*b-sin(th+2.0/3.0*PI)*c));
        out.add(2.0/3.0*(0.5*a+0.5*b+0.5*c));
        return out;
    }

    @Override
    protected String getDescription() {
        return "Park transformation from ABC into dq0\n"+
                "First input - ABC vector, Second - axes rotation angle.";
    }

    @Override
    protected void setParams() {
        setName("abc-dq0");
    }
}
