package Elements.Electric.ThreePhase.Measurements.Multimeter;

import ElementBase.SchemeElement;
import ElementBase.ThreePhasePin;
import Elements.Environment.Subsystem.Subsystem;

public class Multimeter extends SchemeElement{

    public Multimeter(Subsystem sys){
        super(sys);

        addThreePhaseCont(new ThreePhasePin(this,25,5));
        addThreePhaseCont(new ThreePhasePin(this,25,66));

        addMathContact('o');
        addMathContact('o');
    }

    public Multimeter(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        String[] str={
                "p.1-p.4=0","p.2-p.5=0","p.3-p.6=0",
                "i.1+i.4=0","i.2+i.5=0","i.3+i.6=0",
                "O.1={i.1,i.2,i.3}",
                "O.2={p.1-0,p.2-0,p.3-0}"
        };
        return str;
    }

    @Override
    protected String getDescription() {
        return "Measures phase currents and line-line voltage";
    }

    @Override
    protected void setParams() {
        setName("Multimeter");
    }
}
