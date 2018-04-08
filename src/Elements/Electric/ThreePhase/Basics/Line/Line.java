package Elements.Electric.ThreePhase.Basics.Line;

import ElementBase.SchemeElement;
import ElementBase.ThreePhasePin;
import Elements.Environment.Subsystem.Subsystem;

public class Line extends SchemeElement{
    ScalarParameter R,L;

    public Line(Subsystem sys){
        super(sys);

        addThreePhaseCont(new ThreePhasePin(this,25,5));
        addThreePhaseCont(new ThreePhasePin(this,25,66));
    }

    public Line(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{
                "p.1-p.4=i.1*"+R+"d.X.1*"+L,
                "p.2-p.5=i.2*"+R+"d.X.2*"+L,
                "p.3-p.6=i.3*"+R+"d.X.3*"+L,
                "i.1+i.4=0",
                "i.2+i.5=0",
                "i.3+i.6=0",
                "X.1=i.1",
                "X.2=i.2",
                "X.3=i.3"
        };
    }

    @Override
    protected String getDescription() {
        return "Cable";
    }

    @Override
    protected void setParams() {
        setName("Transmit line");
    }
}
