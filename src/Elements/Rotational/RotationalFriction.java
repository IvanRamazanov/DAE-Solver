package Elements.Rotational;

import ElementBase.MechPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem;

public class RotationalFriction extends SchemeElement{
    ScalarParameter f;

    public RotationalFriction(Subsystem sys){
        super(sys);
        addMechCont(new MechPin(this,17,2));
        addMechCont(new MechPin(this,17,52));
    }

    public RotationalFriction(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{"T.2=(w.1-w.2)*"+f.toString(),"T.1+T.2=0"};
    }

    @Override
    protected String getDescription() {
        return "Friction";
    }

    @Override
    protected void setParams() {
        f=new ScalarParameter("Viscous friction factor",3);
        getParameters().add(f);
        setName("Rotational viscous\nfriction");
    }
}
