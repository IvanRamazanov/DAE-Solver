/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Connections.ElectricMarker;
import Connections.ElectricWire;
import Connections.LineMarker;
import Connections.Wire;
import Elements.Environment.Subsystem.Subsystem;
import javafx.scene.shape.Circle;


/**
 *
 * @author Ivan
 */
public class ElectricPin extends Pin{

    public ElectricPin(Element owner, int x, int y){
        super(owner,x,y);
        setView(new Circle(4));

    }

    @Override
    protected boolean isProperInstance(LineMarker lm) {
        return lm instanceof ElectricMarker;
    }

    @Override
    protected Wire createWire(Pin pin, double x, double y) {
        return new ElectricWire(getSystem(),pin,x,y);
    }

    @Override
    public Wire createWire(Subsystem sys, Pin pin1, Pin pin2) {
        return new ElectricWire(sys,pin1,pin2);
    }
}

