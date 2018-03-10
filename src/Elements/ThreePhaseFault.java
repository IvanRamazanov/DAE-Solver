package Elements;

import ElementBase.ElectricPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem;

import java.util.List;

public class ThreePhaseFault extends SchemeElement{

    ScalarParameter fTime,ron;

    public ThreePhaseFault(Subsystem sys){
        super(sys);

        addElemCont(new ElectricPin(this,9,4));
        addElemCont(new ElectricPin(this,25,4));
        addElemCont(new ElectricPin(this,40,4));
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
        fTime=new ScalarParameter("Fault time",1);
        ron=new ScalarParameter("Short circuit resistance",0.001);
        getParameters().addAll(List.of(fTime,ron));

        setName("Three phase fault");
    }
}
