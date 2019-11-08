package Elements.Electric.Basic.ThreePhLoad;

import ElementBase.ElectricPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;

public class ThreePhLoad extends SchemeElement{
    ScalarParameter Ra,Rb,Rc;

    public ThreePhLoad(Subsystem sys){
        super(sys);
        addElectricCont(new ElectricPin(this, 4, 4));   //A
        addElectricCont(new ElectricPin(this, 20, 4));  //B
        addElectricCont(new ElectricPin(this, 40, 4));  //C
        addElectricCont(new ElectricPin(this, 20, 60));
//        addElemCont(new ElectricPass(this, 4, 60));  //N
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
        getParameters().add(Ra=new ScalarParameter("Ra",10));
        getParameters().add(Rb=new ScalarParameter("Rb",10));
        getParameters().add(Rc=new ScalarParameter("Rc",10));

        setName("Three-phase load");
    }
}
