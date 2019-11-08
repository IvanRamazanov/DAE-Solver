package ElementBase;

import Connections.LineMarker;
import Connections.MechMarker;
import Connections.MechWire;
import Connections.Wire;
import Elements.Environment.Subsystem.Subsystem;
import javafx.scene.shape.Rectangle;

public class MechPin extends Pin{

    public MechPin(Element owner, int x, int y){
        super(owner,x,y);
        setView(new Rectangle(4,4));
    }

    @Override
    protected boolean isProperInstance(LineMarker lm) {
        return lm instanceof MechMarker;
    }

    @Override
    protected Wire createWire(Pin pin, double x, double y) {
        return new MechWire(getSystem(), pin, x, y);
    }

    @Override
    public Wire createWire(Subsystem sys, Pin pin1, Pin pin2) {
        return new MechWire(sys,pin1,pin2);
    }
}
