/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Connections.ElectricWire;
import Connections.ElectricWire.WireMarker;
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
 * @author Иван
 */
public class ElemPin extends Circle{
    ShemeElement owner;
    WireMarker wireCont;
    SimpleDoubleProperty centerX,centerY;

    public ElemPin(ShemeElement owner){
        this.owner=owner;
    }

    public ElemPin(ShemeElement owner,int x,int y){
        this.owner=owner;
        this.setCenterX(x);
        this.setCenterY(y);
        this.setRadius(4);

        //--Events--
        EventHandler enterMouse=(EventHandler<MouseEvent>) (MouseEvent me) ->{
            this.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
            this.setCursor(Cursor.HAND);
        };
        EventHandler exitMouse= (EventHandler<MouseEvent>)(MouseEvent me) ->{
            this.setEffect(null);
            this.setCursor(Cursor.DEFAULT);
        };
        EventHandler dragEnterHndl =(EventHandler<MouseDragEvent>)(MouseDragEvent me) -> {
            if(ElectricWire.activeWireConnect!=null&&this.wireCont==null){
                ElectricWire.activeWireConnect.getWire().setEnd(this);
                this.toFront();
            }
            
        };
        EventHandler dragExitHndl =(EventHandler<MouseDragEvent>)(MouseDragEvent me) -> {
            
            if(ElectricWire.activeWireConnect!=null){
                //if(Wire.activeWireConnect.getElemContact()!=this){
                if(ElectricWire.activeWireConnect.getIsPlugged().getValue()){
                    switch(me.getButton()){
                        case PRIMARY:
                            if(me.isPrimaryButtonDown()){
//                                    this.wireCont=null;
                                ElectricWire.activeWireConnect.unPlug();
                                this.setOpacity(1);
                            }
                            break;
                        case SECONDARY:
                            if(me.isSecondaryButtonDown()){
//                                    this.wireCont=null;
                                ElectricWire.activeWireConnect.unPlug();
                                this.setOpacity(1);
                            }
                            break;
                    }
                }
            }
            
            
            
            
            
            
            
            
            
            
            
//                if(me.getGestureSource()==null){
//                    me.consume();
//                }else   if(((WireConnect)me.getGestureSource()).getElemContact()==this
//                        &&((WireConnect)me.getGestureSource()).getWire().getRank()==1){
//                    me.consume();
//                }else{
//                    switch(me.getButton()){
//                        case PRIMARY:
//                            if(me.isPrimaryButtonDown()){
//                                this.wireCont=null;
//                                ((WireConnect)me.getGestureSource()).unPlug();
//                                this.setOpacity(1);
//                            }
//                            break;
//                        case SECONDARY:
//                            if(me.isSecondaryButtonDown()){
//                                this.wireCont=null;
//                                ((WireConnect)me.getGestureSource()).unPlug();
//                                this.setOpacity(1);
//                            }
//                            break;
//                    }                    
//                    me.consume();
//                }





        };
        this.addEventHandler(MouseEvent.DRAG_DETECTED, me -> {
            if(me.getButton()==MouseButton.PRIMARY){
                if(wireCont==null){
//                    this.setOpacity(0);
                    RaschetKz.BranchList.add(new ElectricWire((ElemPin)me.getSource(),
                            me.getSceneX(),me.getSceneY()));
                    ElectricWire.activeWireConnect.startFullDrag();
                    this.addEventFilter(MouseEvent.MOUSE_DRAGGED, ElectricWire.WC_MOUSE_DRAG);
                    this.addEventFilter(MouseDragEvent.MOUSE_RELEASED, ElectricWire.WC_MOUSE_RELEAS);

                }else{
//                    this.setOpacity(1);
//                    Wire.activeWireConnect=wireCont;
                    this.wireCont.unPlug();
//                    this.wireCont=null;
                    ElectricWire.activeWireConnect.startFullDrag();
                    this.addEventFilter(MouseEvent.MOUSE_DRAGGED, ElectricWire.WC_MOUSE_DRAG);
                    this.addEventFilter(MouseDragEvent.MOUSE_RELEASED, ElectricWire.WC_MOUSE_RELEAS);
                }
                me.consume();
            }
        });
        this.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, dragEnterHndl);
        this.addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, dragExitHndl);
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
            e.consume();
        });
        this.addEventHandler(MouseDragEvent.MOUSE_DRAGGED, e->{
            e.consume();
        });
        this.setOnMouseEntered(enterMouse);
        this.setOnMouseExited(exitMouse);
        //------

        this.centerX=new SimpleDoubleProperty(this.getCenterX());
        this.centerY=new SimpleDoubleProperty(this.getCenterY());
        this.localToSceneTransformProperty().addListener((aza,oldVal,newVal)->{
            Point2D point=newVal.transform(getCenterX(), getCenterY());
            point=RaschetKz.drawBoard.sceneToLocal(point);
            this.centerX.set(point.getX());
            this.centerY.set(point.getY());
        });
        this.setFill(Paint.valueOf("#ffffff"));
        this.setStrokeWidth(2);
        this.setStroke(Paint.valueOf("#000000"));
        this.setCursor(Cursor.HAND);
    }

    /**
     * Удаляет следы
     */
    void clear(){
        this.owner=null;
        if(wireCont!=null){
            wireCont.unPlug(); //??????????
            wireCont.setIsPlugged(false);
        }
    }

    /**
     * If isReal true, add EC pointer to WC and bind CenterProp.
     * If false just bind CenterProp.
     * @param contactr wireCont
     */
    public void setWirePointer(WireMarker contactr){
        this.wireCont=contactr;
        this.setOpacity(0);
        //this.wireCont.activate();
        //contactr.setElemContact(this);
        //this.setVisible(false);
    }

//    public void bindWCendProp(WireMarker wCont){
//        wCont.getBindX().bind(centerX);
//        wCont.getBindY().bind(centerY);
//    }

//    public void bindWCstartProp(WireMarker wCont){
////        wCont.getItsLine().bindCross(wCont.getCenterX(), wCont.getCenterY());
//        wCont.getItsLine().getStartX().bind(centerX);
//        wCont.getItsLine().getStartY().bind(centerY);
//    }
    
    public SimpleDoubleProperty getBindX(){
        return centerX;
    }
    
    public SimpleDoubleProperty getBindY(){
        return centerY;
    }

    public WireMarker getWireContact(){
        return(wireCont);
    }

    /**
     * Remove wire cont reference, and setOpacity to 1.0
     */
    public void clearWireContact(){
        this.wireCont=null;
        this.setOpacity(1.0);
    }

    public ShemeElement getOwner(){
        return(owner);
    }

}
