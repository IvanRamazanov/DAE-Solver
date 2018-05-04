package Elements.Electric.ThreePhase.Basics.CompositLoad;

import ElementBase.SchemeElement;
import ElementBase.ThreePhasePin;
import Elements.Environment.Subsystem.Subsystem;

public class CompositLoad extends SchemeElement {
    ScalarParameter R,L;

    public CompositLoad(Subsystem sys){
        super(sys);

        addThreePhaseCont(new ThreePhasePin(this,25,5));
        addThreePhaseCont(new ThreePhasePin(this,25,66));
    }

    public CompositLoad(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        double r=R.getValue(),
                l=L.getValue();
        return new String[]{
                "d.X.1=(p.1-p.4-i.1*"+r+")/"+l,
                "d.X.2=(p.2-p.5-i.2*"+r+")/"+l,
                "d.X.3=(p.3-p.6-i.3*"+r+")/"+l,
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
        return "Three phase RL Load";
    }

    @Override
    protected void setParams() {
        getParameters().add(R=new ScalarParameter("Resistance, Ohm",0.009));
        getParameters().add(L=new ScalarParameter("Inductance, H",0.06));

        getInitials().add(new InitParam("Ia",0.0));
        getInitials().add(new InitParam("Ib",0.0));
        getInitials().add(new InitParam("Ic",0.0));
        setName("RL Load");
    }
}
