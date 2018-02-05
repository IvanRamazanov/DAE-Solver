/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Connections.MathWire;
import Connections.MathWire.MathMarker;
//import static ElementBase.MathElement.mathCont;

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
import javafx.scene.shape.Polygon;

/**
 *
 * @author Иван
 */
abstract public class MathPin {
    protected Polygon view;
    protected SimpleDoubleProperty arrowX;
    protected SimpleDoubleProperty arrowY;
    protected MathMarker itsConnection;
    protected final double arrowHeight=8,arrowWidth=6;
    //protected char type;
    
    public MathPin(){
        view=new Polygon(0,0,0,arrowHeight,arrowWidth,arrowHeight/2);
        view.setTranslateX(-2.0);
        view.setTranslateY(-3.0);
            //--Events--
        EventHandler enterMouse = new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent me){
                view.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
                view.setCursor(Cursor.HAND);
            }
            
        };
        EventHandler exitMouse =  new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent me){
                view.setEffect(null);
                view.setCursor(Cursor.DEFAULT);
            }
        };
        view.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, e->{
            if(itsConnection==null){
                Connections.MathWire.activeMathMarker.getWire().setEnd(this);
                itsConnection=Connections.MathWire.activeMathMarker;
//                view.setOpacity(0);
                view.toFront();
            }
        });
        view.addEventHandler(MouseEvent.DRAG_DETECTED, me -> {
            if(me.getButton()==MouseButton.PRIMARY){
                if(itsConnection==null){
                    view.setOpacity(0);
                    raschetkz.RaschetKz.mathContsList.add(new MathWire(this,
                            me.getSceneX(),me.getSceneY()));
                    MathWire.activeMathMarker.startFullDrag();
                    view.addEventFilter(MouseEvent.MOUSE_DRAGGED, MathWire.MC_MOUSE_DRAG);
                    view.addEventFilter(MouseDragEvent.MOUSE_RELEASED, MathWire.MC_MOUSE_RELEAS);

                }else{
//                    this.setOpacity(1);
//                    Wire.activeWireConnect=wireCont;
                    itsConnection.unPlug();
//                    this.wireCont=null;
                    MathWire.activeMathMarker.startFullDrag();
                    view.addEventFilter(MouseEvent.MOUSE_DRAGGED, MathWire.MC_MOUSE_DRAG);
                    view.addEventFilter(MouseDragEvent.MOUSE_RELEASED, MathWire.MC_MOUSE_RELEAS);
                }
                me.consume();
            }
            // old version
//            if(me.getButton()==MouseButton.PRIMARY){
//                if(itsConnection==null){
////                    view.setOpacity(0);
//                    view.toFront();
//                    MathWire mc=new MathWire(this);
//                    itsConnection=mc;
//                    raschetkz.RaschetKz.mathContsList.add(mc);
//                    mc.pushToBack();
//                    Connections.MathWire.activeMathMarker=mc;
//                    view.startFullDrag();
//                    view.addEventFilter(MouseEvent.MOUSE_DRAGGED, Connections.MathWire.MC_MOUSE_DRAG);
//                    view.addEventFilter(MouseDragEvent.MOUSE_RELEASED, Connections.MathWire.MC_MOUSE_RELEAS);
//                }else{
//                    System.out.println("Hi, im dragger!");
//                    itsConnection.unPlug(this);
//                    
////                    view.setOpacity(1);
//////                    //Wire.activeWireContact=wireCont;
////                    itsConnection.unPlug();
//                    itsConnection=null;
//                    view.startFullDrag(); //ACTIVE CONTACT!!!!!
//                    view.addEventFilter(MouseEvent.MOUSE_DRAGGED, Connections.MathWire.MC_MOUSE_DRAG);
//                    view.addEventFilter(MouseDragEvent.MOUSE_RELEASED, Connections.MathWire.MC_MOUSE_RELEAS);
//                }
//                me.consume();
//            }
        });
        EventHandler dragExitHndl = new EventHandler<MouseDragEvent>(){
            @Override
            public void handle(MouseDragEvent me){
    //            if(me.getGestureSource()==null){
    //                me.consume();
    //            }else   if(((WireContact)me.getGestureSource()).getElemContact()==this
    //                    &&((WireContact)me.getGestureSource()).getWire().getRank()==1){
    //                me.consume();
    //            }else{
    //                switch(me.getButton()){
    //                    case PRIMARY:
    //                        if(me.isPrimaryButtonDown()){
    //                            this.wireCont=null;
    //                            ((WireContact)me.getGestureSource()).unPlug();
    //                            this.setOpacity(1);
    //                        }
    //                        break;
    //                    case SECONDARY:
    //                        if(me.isSecondaryButtonDown()){
    //                            this.wireCont=null;
    //                            ((WireContact)me.getGestureSource()).unPlug();
    //                            this.setOpacity(1);
    //                        }
    //                        break;
    //                }                    
    //                me.consume();
    //            }
            }
        };
        //------
        view.addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
            e.consume();
        });
        view.addEventHandler(MouseDragEvent.MOUSE_DRAGGED, e->{
            e.consume();
        });
        view.setOnMouseEntered(enterMouse);
        view.setOnMouseExited(exitMouse);
        
        view.addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, dragExitHndl);

        arrowX=new SimpleDoubleProperty();
        arrowY=new SimpleDoubleProperty();
        view.localToSceneTransformProperty().addListener((aza,oldVal,newVal)->{
            Point2D point=newVal.transform(arrowHeight/2, 0);
            point=raschetkz.RaschetKz.drawBoard.sceneToLocal(point);
            arrowX.set(point.getX());
            arrowY.set(point.getY());
        });
    }
    
//    MathContact(double x,double y){
//            this();
//            view.setLayoutX(x);
//            view.setLayoutY(y);
//        }
    
    /**
     * @return the arrowX
     */
    public SimpleDoubleProperty getArrowX() {
        return arrowX;
    }
    
    public void clearPin(){
        itsConnection=null;
        view.setOpacity(1.0);
    }

    /**
     * @return the arrowY
     */
    public SimpleDoubleProperty getArrowY() {
        return arrowY;
    }
    
    /**
     * @return the itsConnection
     */
    public MathMarker getItsConnection() {
        return itsConnection;
    }

    /**
     * @param itsConnection the itsConnection to set
     */
    public void setItsConnection(MathMarker itsConnection) {
        this.itsConnection = itsConnection;
        view.setOpacity(0.0);
    }
}
