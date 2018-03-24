package Elements.Electric.ThreePhase.Basics.ThreeToOne;

import ElementBase.ElectricPin;
import ElementBase.SchemeElement;
import ElementBase.ThreePhasePin;
import Elements.Environment.Subsystem.Subsystem;

public class ThreeToOne extends SchemeElement{

    public ThreeToOne(Subsystem sys){
        super(sys);

        addElectricCont(new ElectricPin(this,8,10)); //p.1
        addElectricCont(new ElectricPin(this,8,35));
        addElectricCont(new ElectricPin(this,8,55));
        addThreePhaseCont(new ThreePhasePin(this,45,35)); //p.4-p.6
    }

    public ThreeToOne(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{
                "p.1-p.4=0",
                "i.1+i.4=0",
                "p.2-p.5=0",
                "i.2+i.5=0",
                "p.3-p.6=0",
                "i.3+i.6=0"
        };
    }

    @Override
    protected String getDescription() {
        return "Transfer between threephase and single phase circiuts";
    }

    @Override
    protected void setParams() {
        setName("Three-to-one phase");
    }
}
