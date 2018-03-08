/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Connections.LineMarker;
import Connections.MathMarker;
import Connections.MathWire;

import java.util.List;

/**
 *
 * @author Ivan
 */
public class MathOutPin extends MathPin{
    private int index;

    public MathOutPin(){
        super();
    }

//    /**
//     * For physics to math
//     * @param x
//     * @param y
//     */
//    MathOutPin(double x,double y){
//        this();
//        getView().setLayoutX(x);
//        getView().setLayoutY(y);
//    }

    MathOutPin(Element own,double x,double y){
        this();
        getView().setLayoutX(x);
        getView().setLayoutY(y);
        setOwner(own);
    }

    public List<Double> getValue(){
        return ((MathElement)getOwner()).getValue(index);
    }

    @Override
    public void setWirePointer(LineMarker itsConnection) {
        super.setWirePointer(itsConnection);
        ((MathWire)itsConnection.getWire()).setSourcePointer(this);
    }

}


