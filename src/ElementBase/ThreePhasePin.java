package ElementBase;

import Connections.LineMarker;
import Connections.ThreePhaseMarker;
import Connections.ThreePhaseWire;
import Connections.Wire;
import Elements.Environment.Subsystem.Subsystem;
import javafx.scene.shape.Circle;

public class ThreePhasePin extends Pin{

    public ThreePhasePin(Element owner, int x, int y){
        super(owner,x,y);
        setView(new Circle(4));
    }

    @Override
    protected boolean isProperInstance(LineMarker lm) {
        return lm instanceof ThreePhaseMarker;
    }

    @Override
    public Wire createWire(Pin pin, double x, double y) {
        return new ThreePhaseWire(getSystem(),pin,x,y);
    }

    @Override
    public Wire createWire(Subsystem sys, Pin pin1, Pin pin2) {
        return new ThreePhaseWire(sys,pin1,pin2);
    }
}
