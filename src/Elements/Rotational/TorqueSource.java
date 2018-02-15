package Elements.Rotational;

import ElementBase.ElemMechPin;
import ElementBase.SchemeElement;

public class TorqueSource extends SchemeElement {
    Parameter A;
    public TorqueSource(){
        super();
        addMechCont(new ElemMechPin(this,12,6)); //C
        addMechCont(new ElemMechPin(this,12,30)); //Ref
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
        A=new Parameter("Torque value",1);
        getParameters().add(A);

        setName("Constant torque\nsource");
    }
}
