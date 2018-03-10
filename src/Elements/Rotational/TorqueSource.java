package Elements.Rotational;

import ElementBase.MechPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem;

public class TorqueSource extends SchemeElement {
    ScalarParameter A;
    public TorqueSource(Subsystem sys){
        super(sys);
        addMechCont(new MechPin(this,17,2));
        addMechCont(new MechPin(this,17,52)); //Ref
    }

    public TorqueSource(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{"T.1="+A.toString(),
        "T.1+T.2=0"};
    }

    @Override
    protected String getDescription() {
        return "Produces constant torque";
    }

    @Override
    protected void setParams() {
        A=new ScalarParameter("Torque value",1);
        getParameters().add(A);

        setName("Constant torque\nsource");
    }
}
