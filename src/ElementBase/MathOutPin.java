/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Connections.MathWire.MathMarker;
import java.util.List;

/**
 *
 * @author Иван
 */
public class MathOutPin extends MathPin{
    private int index;
    private MathElement owner;
//    private MathInPin source;

    public MathOutPin(){
        super();
    }

    public MathOutPin(MathElement own){
        this();
        owner=own;
    }

    /**
     * For physics to math
     * @param x
     * @param y 
     */
    MathOutPin(double x,double y){
        this();
        view.setLayoutX(x);
        view.setLayoutY(y);
    }

    MathOutPin(MathElement own,double x,double y){
        this();
        view.setLayoutX(x);
        view.setLayoutY(y);
        owner=own;
    }

    public List<Double> getValue(){
        return owner.getValue(index);
    }

    void delete(){
        raschetkz.RaschetKz.drawBoard.getChildren().remove(view);
        owner=null;
    }
    
    @Override
    public void setItsConnection(MathMarker itsConnection) {
        super.setItsConnection(itsConnection);
        itsConnection.getWire().setSourcePointer(this);
    }

//    /**
//     * @return the source
//     */
//    public MathInPin getSource() {
//        return source;
//    }
    
    public void setMathConnLink(MathMarker mc){
        setItsConnection(mc);
//        setSource(mc.getWire().getSource());
    }

//    /**
//     * @param source the source to set
//     */
//    public void setSource(MathInPin source) {
//        this.source = source;
//    }
    
//    @Override
//    public void clearPin(){
////        source=null;
//        owner=null;
//    }
}

