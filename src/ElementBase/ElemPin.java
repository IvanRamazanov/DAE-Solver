/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Connections.ElectricWire;
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
public class ElemPin extends Pin{
//    SchemeElement owner;
//    WireMarker wireCont;
//    SimpleDoubleProperty centerX,centerY;

    public ElemPin(SchemeElement owner){
        setOwner(owner);
    }

    public ElemPin(SchemeElement owner, int x, int y){
        setView(new Circle());
        setOwner(owner);
        ((Circle) getView()).setCenterX(x);
        ((Circle) getView()).setCenterY(y);
        ((Circle) getView()).setRadius(4);

        //--Events--
        EventHandler enterMouse=(EventHandler<MouseEvent>) (MouseEvent me) ->{
            getView().setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
            getView().setCursor(Cursor.HAND);
        };
        EventHandler exitMouse= (EventHandler<MouseEvent>)(MouseEvent me) ->{
            getView().setEffect(null);
            getView().setCursor(Cursor.DEFAULT);
        };
        EventHandler dragEnterHndl =(EventHandler<MouseDragEvent>)(MouseDragEvent me) -> {
            if(ElectricWire.activeWireConnect!=null&&getItsConnection()==null){
                System.out.println("You are plugged");
                ElectricWire.activeWireConnect.getWire().setEnd(this);
                getView().toFront();
            }

        };
        EventHandler dragExitHndl =(EventHandler<MouseDragEvent>)(MouseDragEvent me) -> {
            System.out.println("Hello from drag exit (in ElemPin)! Source: "+me.getGestureSource());
            if(ElectricWire.activeWireConnect!=null)
                //if(!ElectricWire.activeWireConnect.getElemContact().equals(this))
                    switch(me.getButton()){
                        case PRIMARY:
                            if(me.isPrimaryButtonDown()){
//                                    this.wireCont=null;
                                ElectricWire.activeWireConnect.unPlug();
                                getView().setOpacity(1);
                            }
                            break;
                        case SECONDARY:
                            if(me.isSecondaryButtonDown()){
//                                    this.wireCont=null;
                                ElectricWire.activeWireConnect.unPlug();
                                getView().setOpacity(1);
                            }
                            break;
                    }
//            this.addEventFilter(MouseEvent.MOUSE_DRAGGED, ElectricWire.WC_MOUSE_DRAG);
//            this.addEventFilter(MouseDragEvent.MOUSE_RELEASED, ElectricWire.WC_MOUSE_RELEAS);

            if(ElectricWire.activeWireConnect!=null&&getItsConnection()!=null){
                //if(Wire.activeWireConnect.getElemContact()!=this){
                if(ElectricWire.activeWireConnect.getIsPlugged().getValue()){


                }
            }
            me.consume();











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
        getView().addEventHandler(MouseEvent.DRAG_DETECTED, me -> {
            if(me.getButton()==MouseButton.PRIMARY){
                if(getItsConnection()==null){
//                    this.setOpacity(0);
                    RaschetKz.BranchList.add(new ElectricWire(this,
                            me.getSceneX(),me.getSceneY()));
                    ElectricWire.activeWireConnect.startFullDrag();
                    getView().addEventFilter(MouseEvent.MOUSE_DRAGGED, ElectricWire.WC_MOUSE_DRAG);
                    getView().addEventFilter(MouseDragEvent.MOUSE_RELEASED, ElectricWire.WC_MOUSE_RELEAS);

                }else{
//                    this.setOpacity(1);
//                    Wire.activeWireConnect=wireCont;
                    System.out.println("Hello from drag detect!");
                    getItsConnection().unPlug();
//                    this.wireCont=null;
//                    ElectricWire.activeWireConnect=getWireContact();
                    getView().startFullDrag();
                    getView().addEventFilter(MouseEvent.MOUSE_DRAGGED, ElectricWire.WC_MOUSE_DRAG);
                    getView().addEventFilter(MouseDragEvent.MOUSE_RELEASED, ElectricWire.WC_MOUSE_RELEAS);
                }
                me.consume();
            }
        });
        getView().addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, dragEnterHndl);
        getView().addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED_TARGET, dragExitHndl);
        getView().addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
            e.consume();
        });
        getView().addEventHandler(MouseDragEvent.MOUSE_DRAGGED, e->{
            e.consume();
        });
        getView().setOnMouseEntered(enterMouse);
        getView().setOnMouseExited(exitMouse);
        //------

        setBindX(new SimpleDoubleProperty(((Circle) getView()).getCenterX()));
        setBindY(new SimpleDoubleProperty(((Circle) getView()).getCenterY()));
        getView().localToSceneTransformProperty().addListener((aza, oldVal, newVal)->{
            Point2D point=newVal.transform(((Circle) getView()).getCenterX(), ((Circle) getView()).getCenterY());
            point=RaschetKz.drawBoard.sceneToLocal(point);
            getBindX().set(point.getX());
            getBindY().set(point.getY());
        });
        getView().setFill(Paint.valueOf("#ffffff"));
        getView().setStrokeWidth(2);
        getView().setStroke(Paint.valueOf("#000000"));
        getView().setCursor(Cursor.HAND);
    }

//    /**
//     * Удаляет следы
//     */
//    void clear(){
//        this.owner=null;
//        if(wireCont!=null){
//            wireCont.unPlug(); //??????????
//            wireCont.setIsPlugged(false);
//        }
//    }

//    /**
//     * If isReal true, add EC pointer to WC and bind CenterProp.
//     * If false just bind CenterProp.
//     * @param contactr wireCont
//     */
//    public void setWirePointer(LineMarker contactr){
//        this.wireCont=contactr;
//        getView().setOpacity(0);
//        //this.wireCont.activate();
//        //contactr.bindElemContact(this);
//        //this.setVisible(false);
//    }

//    public void bindWCendProp(WireMarker wCont){
//        wCont.getBindX().bind(centerX);
//        wCont.getBindY().bind(centerY);
//    }

//    public void bindWCstartProp(WireMarker wCont){
////        wCont.getItsLine().bindCross(wCont.getCenterX(), wCont.getCenterY());
//        wCont.getItsLine().getStartX().bind(centerX);
//        wCont.getItsLine().getStartY().bind(centerY);
//    }

//    public SimpleDoubleProperty getBindX(){
//        return centerX;
//    }
//
//    public SimpleDoubleProperty getBindY(){
//        return centerY;
//    }

//    public WireMarker getWireContact(){
//        return (WireMarker) getItsConnection();
//    }

//    /**
//     * Remove wire cont reference, and setOpacity to 1.0
//     */
//    public void clearWireContact(){
//        setItsConnection(null);
//        getView().setOpacity(1.0);
//    }

//    public SchemeElement getOwner(){
//        return(owner);
//    }

}

