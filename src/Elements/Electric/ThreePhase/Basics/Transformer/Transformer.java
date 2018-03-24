package Elements.Electric.ThreePhase.Basics.Transformer;

import ElementBase.SchemeElement;
import ElementBase.ThreePhasePin;
import Elements.Environment.Subsystem.Subsystem;

public class Transformer extends SchemeElement{

    public Transformer(Subsystem sys){
        super(sys);

        addThreePhaseCont(new ThreePhasePin(this,5,30));
        addThreePhaseCont(new ThreePhasePin(this,45,30));
    }

    public Transformer(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[0];
    }

    @Override
    protected String getDescription() {
        return "Transformer";
    }

    @Override
    protected void setParams() {
        setName("Transformer");
    }
}
