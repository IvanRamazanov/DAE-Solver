/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Connections.LineMarker;
import Connections.MathWire;

import java.util.List;

/**
 *
 * @author Ivan
 */
public class MathOutPin extends MathPin{
    private int index;

    public MathOutPin(){

    }

    public MathOutPin(Element owner){
        super(owner);
    }

//    /**
//     * For physics to math
//     * @param x
//     * @param y
//     */
//    MathOutPin(double x,double y){
//        this();
//        getMarker().setLayoutX(x);
//        getMarker().setLayoutY(y);
//    }

    public MathOutPin(Element own,double x,double y){
        this(own);
        getView().setLayoutX(x);
        getView().setLayoutY(y);
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


