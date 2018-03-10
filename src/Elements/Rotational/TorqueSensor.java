package Elements.Rotational;

import ElementBase.MechPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem;

public class TorqueSensor extends SchemeElement{
    public TorqueSensor(Subsystem sys){
        super(sys);
        addMechCont(new MechPin(this,17,2));
        addMechCont(new MechPin(this,17,52));

        addMathContact('o');
    }

    public TorqueSensor(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{
                "w.1-w.2=0",
                "T.1+T.2=0",
                "O.1=T.1"
        };
    }

    @Override
    protected String getDescription() {
        return "Ideal torque sensor";
    }

    @Override
    protected void setParams() {
        setName("Torque sensor");
    }
}
