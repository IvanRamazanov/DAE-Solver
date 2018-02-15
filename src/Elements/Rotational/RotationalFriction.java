package Elements.Rotational;

import ElementBase.ElemMechPin;
import ElementBase.SchemeElement;

public class RotationalFriction extends SchemeElement{
    Parameter f;

    public RotationalFriction(){
        super();
        addMechCont(new ElemMechPin(this,12,6));
        addMechCont(new ElemMechPin(this,12,30));
    }

    public RotationalFriction(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{"T.1=(w.1-w.2)*"+f.toString()};
    }

    @Override
    protected String getDescription() {
        return "Friction";
    }

    @Override
    protected void setParams() {
        f=new Parameter("Viscous friction factor",3);
        getParameters().add(f);
        setName("Rotational viscous\nfriction");
    }
}
