package ElementBase;

import Connections.LineMarker;
import Connections.MechMarker;
import Connections.MechWire;
import Connections.Wire;
import javafx.scene.shape.Rectangle;

public class MechPin extends Pin{

//    public MechPin(SchemeElement owner){
//        setOwner(owner);
//    }

    public MechPin(Element owner, int x, int y){
        super(owner,x,y);
        setView(new Rectangle(width,height));
    }

    @Override
    protected boolean isProperInstance(LineMarker lm) {
        return lm instanceof MechMarker;
    }

    @Override
    protected Wire createWire(Pin pin, double x, double y) {
        return new MechWire(getSystem(), pin, x, y);
    }
}
