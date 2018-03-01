package Elements;

import ElementBase.ElemPin;
import ElementBase.SchemeElement;

import java.util.List;

public class ThreePhLoad extends SchemeElement{
    Parameter Ra,Rb,Rc;

    public ThreePhLoad(){
        super();
        addElemCont(new ElemPin(this, 4, 4));   //A
        addElemCont(new ElemPin(this, 20, 4));  //B
        addElemCont(new ElemPin(this, 40, 4));  //C
        addElemCont(new ElemPin(this, 20, 60));
//        addElemCont(new ElemPin(this, 4, 60));  //N
    }

    public ThreePhLoad(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{
                "p.1=p.4+i.1*"+Ra,
                "p.2=p.4+i.2*"+Rb,
                "p.3=p.4+i.3*"+Rc,
                "i.1+i.2+i.3+i.4=0"
        };
    }

    @Override
    protected String getDescription() {
        return "Three phase load.";
    }

    @Override
    protected void setParams() {
        Ra=new Parameter("Ra",10);
        Rb=new Parameter("Rb",10);
        Rc=new Parameter("Rc",10);
        getParameters().addAll(List.of(Ra,Rb,Rc));

        setName("Three-phase load");
    }
}
