package Elements.Rotational;

import ElementBase.MechPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem;

public class RotationReference extends SchemeElement {
    public RotationReference(Subsystem sys){
        super(sys);
        addMechCont(new MechPin(this,6,6));
    }
    public RotationReference(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{"w.1=0"};
    }

    @Override
    protected String getDescription() {
        return "This is zero speed";
    }

    @Override
    protected void setParams() {
        setName("Rotation reference");
    }
}
