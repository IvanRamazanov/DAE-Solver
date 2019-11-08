/*
 * The MIT License
 *
 * Copyright 2018 Ivan.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package Connections;

import ElementBase.MathInPin;
import ElementBase.MathOutPin;
import ElementBase.Pin;
import Elements.Environment.Subsystem.Subsystem;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;

import java.util.List;

/**
 *
 * @author Ivan
 */
public class MathWire extends Wire{
    private MathMarker sourceMarker;
    private static Shape dragSource;
    private MathOutPin source;

    private List<Double> value;

    public static final EventHandler MC_MOUSE_DRAG = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent me) {
            if(!activeWireConnect.getIsPlugged().get()){
                activeWireConnect.setEndPropInSceneCoordinates(me.getSceneX(), me.getSceneY());
            }
            me.consume();
        }
    };
    public static final EventHandler MC_MOUSE_RELEAS= new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent me) {
            ((MathMarker)activeWireConnect).eraseDragSource();
            activeWireConnect.toFront();
            activeWireConnect=null;
            ((Node)me.getSource()).removeEventFilter(MouseEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
            ((Node)me.getSource()).removeEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
            me.consume();
        }
    };

    public MathWire(Subsystem sys){
        setItsSystem(sys);
        setWireColor("#000000");

        sys.getWireList().add(this);
    }

    /**
     * Создает провод и цепляет старт к контакту
     * @param mathPin
     * @param meSceneX
     * @param meSceneY
     */
    public MathWire(Subsystem sys, Pin mathPin,double meSceneX,double meSceneY){
        this(sys);
        sourceMarker=new MathMarker(this);
        MathMarker wc=new MathMarker(this);
        if(mathPin instanceof MathOutPin){
            getSourceMarker().setItsConnectedPin(mathPin);  // link and bind end
            getSourceMarker().bindStartTo(mathPin.getBindX(), mathPin.getBindY()); // zeroLength
            wc.bindStartTo(mathPin.getBindX(), mathPin.getBindY()); // same point
            wc.setEndPropInSceneCoordinates(meSceneX,meSceneY);
            activeWireConnect=wc; // for mouse_drag event
        }else{
            wc.setItsConnectedPin(mathPin); // link and bind end
            wc.bindStartTo(mathPin.getBindX(), mathPin.getBindY());
            getSourceMarker().bindStartTo(mathPin.getBindX(), mathPin.getBindY());
            getSourceMarker().setEndPropInSceneCoordinates(meSceneX,meSceneY);
            activeWireConnect= getSourceMarker();
        }
    }

    public MathWire(Subsystem sys,Pin EleCont1,Pin EleCont2){
        this(sys);
        MathMarker wc1=new MathMarker(this);
        sourceMarker=wc1;
        MathMarker wc2=new MathMarker(this);

        wc2.bindStartTo(wc1.getBindX(),wc1.getBindY());
        wc1.bindStartTo(wc1.getBindX(),wc1.getBindY());

        if(EleCont1 instanceof MathOutPin) {
            wc1.bindElemContact(EleCont1);
            wc2.bindElemContact(EleCont2);
        }else{
            wc1.bindElemContact(EleCont2);
            wc2.bindElemContact(EleCont1);
        }
    }

    public static Shape getDragSource() {
        return dragSource;
    }

    public static void setDragSource(Shape shape) {
        dragSource=shape;
    }

    public boolean setEnd(Pin cont){
        boolean success=false;
        if(getSourceMarker().equals(activeWireConnect)&&cont instanceof MathOutPin){
            activeWireConnect.setItsConnectedPin(cont);
            success=true;
        }else if(!getSourceMarker().equals(activeWireConnect)&&cont instanceof MathInPin){
            activeWireConnect.setItsConnectedPin(cont);
            success=true;
        }
        return success;
    }

    @Override
    public void configure(String info){
        super.configure(info);
        sourceMarker=(MathMarker)getWireContacts().get(0);
        setSourcePointer(source);
    }

    @Override
    protected LineMarker addLineMarker(Wire wire, double ax, double ay, double ex, double ey, boolean isHorizontal, double[] constraints) {
        return new MathMarker(wire,ax,ay,ex,ey,isHorizontal,constraints);
    }

    @Override
    protected LineMarker addLineMarker(Wire wire) {
        return new MathMarker(wire);
    }

    @Override
    protected LineMarker addLineMarker(Wire wire,double x,double y) {
        return new MathMarker(wire,x,y);
    }

//    /**
//     * Разбиндивает все узлы
//     */
//    public void unBindAll(){
//        for(LineMarker wc:this.getWireContacts()){
//            wc.unBindStartPoint();
//        }
//    }

    /**
     * @return the source
     */
    public MathOutPin getSource() {
        return source;
    }

    /**
     * @param source the source to set. Also set link in each marker!
     */
    public void setSourcePointer(MathOutPin source) {
        this.setSource(source);
        for(int i=1;i<getWireContacts().size();i++){
            MathInPin pin=(MathInPin)getWireContacts().get(i).getItsConnectedPin();
            if(pin!=null)
                pin.setSource(source);
        }
    }


    public void delete(){
        getItsSystem().getWireList().remove(this);
        if(!getWireContacts().isEmpty()){
            activeWireConnect= getWireContacts().get(0);// for prevent dead loop
            int i=getWireContacts().size()-1;
            for(;i>=0;i--){
                if(!getWireContacts().isEmpty())
                    getWireContacts().get(i).delete();
            }
            activeWireConnect=null;
        }
        for(int i=getContContList().size()-1;i>=0;i--){
            if(!getContContList().isEmpty())
                getContContList().get(i).delete();
        }
    }

    @Override
    public void setStaticEventFilters(Node source) {
        source.addEventFilter(MouseDragEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
        source.addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
    }

    public MathMarker getSourceMarker() {
        return sourceMarker;
    }

    public void setSource(MathOutPin source) {
        this.source = source;
    }

    public List<Double> getValue() {
        if(value==null){
            value=getSource().getValue();
        }
        return value;
    }

    public void resetValue() {
        value=null;
    }
}

