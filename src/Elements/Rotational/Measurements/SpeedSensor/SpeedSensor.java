package Elements.Rotational.Measurements.SpeedSensor;

import ElementBase.MechPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;

public class SpeedSensor extends SchemeElement{
    public SpeedSensor(Subsystem sys){
        super(sys);
        addMechCont(new MechPin(this,17,2));
        addMechCont(new MechPin(this,17,52));
        addMathContact('o');
    }

    public SpeedSensor(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{"T.1+T.2=0","T.1=0","O.1=w.1-w.2"};
    }

    @Override
    protected String getDescription() {
        return "Ideal speed sensor";
    }

    @Override
    protected void setParams() {
        setName("Speed sensor");
    }
}
