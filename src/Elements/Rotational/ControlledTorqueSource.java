package Elements.Rotational;

import ElementBase.ElemMechPin;
import ElementBase.SchemeElement;

public class ControlledTorqueSource extends SchemeElement{

    public ControlledTorqueSource(){
        super();
        addMathContact('i');
        addMechCont(new ElemMechPin(this,17,2));
        addMechCont(new ElemMechPin(this,17,52)); //Ref
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
