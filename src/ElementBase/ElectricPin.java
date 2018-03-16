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
import javafx.scene.shape.Circle;


/**
 *
 * @author Ivan
 */
public class ElectricPin extends Pin{

//    public ElectricPass(SchemeElement owner){
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
        return new ElectricWire(getSystem(),pin,x,y);
    }
}

