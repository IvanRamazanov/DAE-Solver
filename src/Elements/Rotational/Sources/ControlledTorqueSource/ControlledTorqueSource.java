package Elements.Rotational.Sources.ControlledTorqueSource;

import ElementBase.MechPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;

public class ControlledTorqueSource extends SchemeElement{

    public ControlledTorqueSource(Subsystem sys){
        super(sys);
        addMathContact('i');
        addMechCont(new MechPin(this,17,2));
        addMechCont(new MechPin(this,17,52)); //Ref
    }

    public ControlledTorqueSource(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{"T.1=I.1",
                "T.1+T.2=0"};
    }

    @Override
    protected String getDescription() {
        return "Produces constant torque equalve value from mathematical input";
    }

    @Override
    protected void setParams() {

        setName("Controlled torque\nsource");
    }
}
