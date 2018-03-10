package ElementBase;

import Connections.LineMarker;
import Connections.MechMarker;
import Connections.MechWire;
import Connections.Wire;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import raschetkz.RaschetKz;

public class MechPin extends Pin{

//    public MechPin(SchemeElement owner){
//        setOwner(owner);
//    }

    public MechPin(SchemeElement owner, int x, int y){
        super(owner,x,y);
        setView(new Rectangle(width,height));
    }

    @Override
    protected boolean isProperInstance(LineMarker lm) {
        return lm instanceof MechMarker;
    }

    @Override
    protected Wire createWire(Pin pin, double x, double y) {
        return new MechWire(getOwner().getItsSystem(), pin, x, y);
    }
}
