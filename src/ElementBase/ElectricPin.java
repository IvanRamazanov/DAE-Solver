/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Connections.ElectricWire;
import Connections.LineMarker;
import Connections.Wire;
import Connections.WireMarker;
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
import javafx.scene.shape.Circle;
import raschetkz.RaschetKz;


/**
 *
 * @author Ivan
 */
public class ElectricPin extends Pin{

//    public ElectricPin(SchemeElement owner){
//        super(owner);
//    }

    public ElectricPin(Element owner, int x, int y){
        super(owner,x,y);
        setView(new Circle());
        ((Circle) getView()).setRadius(height);
    }

    @Override
    protected boolean isProperInstance(LineMarker lm) {
        return lm instanceof WireMarker;
    }

    @Override
    protected Wire createWire(Pin pin, double x, double y) {
        return new ElectricWire(getOwner().getItsSystem(),pin,x,y);
    }
}

