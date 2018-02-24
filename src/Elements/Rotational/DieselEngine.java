package Elements.Rotational;

import ElementBase.ElemMechPin;
import ElementBase.SchemeElement;

public class DieselEngine extends SchemeElement{
    public DieselEngine(){
        super();
        addMechCont(new ElemMechPin(this,40,30));
        addMechCont(new ElemMechPin(this,4,30));
        addMathContact('i'); // fuel consumption
    }

    public DieselEngine(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[0];
    }

    @Override
    protected String getDescription() {
        return null;
    }

    @Override
    protected void setParams() {

    }
}
