package Elements.Math.Control.Decompose;

import ElementBase.MathElement;
import Elements.Environment.Subsystem.Subsystem;

import java.util.ArrayList;
import java.util.List;

public class Decompose extends MathElement{

    public Decompose(Subsystem sys){
        super(sys);

        addMathContact('i');
        addMathContact('o'); // mag
        addMathContact('o'); // deg
    }

    public Decompose(boolean val){
        super(val);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        List<Double> out=new ArrayList<>();
        List<Double> in=getInputs().get(0).getValue();
        switch(outIndex){
            case 0:
                out.add(Math.sqrt(in.get(0)*in.get(0)+in.get(1)*in.get(1)));
                return out;
            case 1:
                if(in.get(0)==0.0){
                    if(in.get(1)<0)
                        out.add(StrictMath.PI/2.0);
                    else
                        out.add(0.0);
                }else {
                    out.add(Math.atan(in.get(1) / in.get(0)));
                }
                return out;
            default:
                return null;
        }

    }

    @Override
    protected String getDescription() {
        return "Complex to magnitude and degree";
    }

    @Override
    protected void setParams() {
        setName("Decompose");
    }
}
