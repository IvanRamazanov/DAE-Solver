package Elements;

import ElementBase.ElemPin;
import ElementBase.SchemeElement;

import java.util.List;

public class ThreePhaseFault extends SchemeElement{

    Parameter fTime,ron;

    public ThreePhaseFault(){
        super();

        addElemCont(new ElemPin(this,9,4));
        addElemCont(new ElemPin(this,25,4));
        addElemCont(new ElemPin(this,40,4));
    }

    public ThreePhaseFault(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{
                "p.1=Z.1+if(gr(time,"+fTime+"),"+ron+",1e6)*i.1",
                "p.2=Z.1+if(gr(time,"+fTime+"),"+ron+",1e6)*i.2",
                "p.3=Z.1+if(gr(time,"+fTime+"),"+ron+",1e6)*i.3",
                "i.1+i.2+i.3=0"
        };
    }

    @Override
    protected String getDescription() {
        return "This block represents three-phase short circuit that triggers at T_on moment of simulation time.";
    }

    @Override
    protected void setParams() {
        fTime=new Parameter("Fault time",1);
        ron=new Parameter("Short circuit resistance",0.001);
        getParameters().addAll(List.of(fTime,ron));

        setName("Three phase fault");
    }
}
