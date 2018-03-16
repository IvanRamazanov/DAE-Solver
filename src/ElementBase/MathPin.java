/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Connections.LineMarker;
import Connections.MathMarker;
import Connections.MathWire;
//import static ElementBase.MathElement.mathCont;

import Connections.Wire;

import javafx.scene.shape.Polygon;

/**
 *
 * @author Ivan
 */
abstract public class MathPin extends Pin{

    public MathPin(){

    }

    public MathPin(Element owner){
        super(owner);
        height=8;
        width=6;
        setView(new Polygon(0,0,0,height,width,height/2));
        getView().setTranslateX(-2.0);
        getView().setTranslateY(-3.0);
//        //--Events--
//        EventHandler enterMouse = new EventHandler<MouseEvent>(){
//            @Override
//            public void handle(MouseEvent me){
//                getMarker().setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
//                getMarker().setCursor(Cursor.HAND);
//            }
//
//        };
//        EventHandler exitMouse =  new EventHandler<MouseEvent>(){
//            @Override
//            public void handle(MouseEvent me){
//                getMarker().setEffect(null);
//                getMarker().setCursor(Cursor.DEFAULT);
//            }
//        };
//        getMarker().addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, e->{
//            if(getItsConnection()==null){
//                if(Wire.activeWireConnect instanceof MathMarker)
//                    if(((MathMarker)Wire.activeWireConnect).getWire().setEnd(this)){ // check if was connected
//                        setItsConnection(Wire.activeWireConnect);
////                view.setOpacity(0);
//                        getMarker().toFront();
//                    }
//            }
//        });
//        getMarker().addEventHandler(MouseEvent.DRAG_DETECTED, me -> {
//            if(me.getButton()==MouseButton.PRIMARY){
//                if(getItsConnection()==null){
//                    getMarker().setOpacity(0);
//                    raschetkz.RaschetKz.wireList.add(new MathWire(this,
//                            me.getSceneX(),me.getSceneY()));
//                    getMarker().startFullDrag();
//                    getMarker().addEventFilter(MouseEvent.MOUSE_DRAGGED, MathWire.MC_MOUSE_DRAG);
//                    getMarker().addEventFilter(MouseDragEvent.MOUSE_RELEASED, MathWire.MC_MOUSE_RELEAS);
//
//                }else{
////                    this.setOpacity(1);
////                    Wire.activeWireConnect=wireCont;
//                    getItsConnection().unPlug();
////                    this.wireCont=null;
//                    getMarker().startFullDrag();
//                    getMarker().addEventFilter(MouseEvent.MOUSE_DRAGGED, MathWire.MC_MOUSE_DRAG);
//                    getMarker().addEventFilter(MouseDragEvent.MOUSE_RELEASED, MathWire.MC_MOUSE_RELEAS);
//                }
//                me.consume();
//            }
//        });
//        EventHandler dragExitHndl = new EventHandler<MouseDragEvent>(){
//            @Override
//            public void handle(MouseDragEvent me){
//                //            if(me.getGestureSource()==null){
//                //                me.consume();
//                //            }else   if(((WireContact)me.getGestureSource()).getElemContact()==this
//                //                    &&((WireContact)me.getGestureSource()).getWire().getRank()==1){
//                //                me.consume();
//                //            }else{
//                //                switch(me.getButton()){
//                //                    case PRIMARY:
//                //                        if(me.isPrimaryButtonDown()){
//                //                            this.wireCont=null;
//                //                            ((WireContact)me.getGestureSource()).unPlug();
//                //                            this.setOpacity(1);
//                //                        }
//                //                        break;
//                //                    case SECONDARY:
//                //                        if(me.isSecondaryButtonDown()){
//                //                            this.wireCont=null;
//                //                            ((WireContact)me.getGestureSource()).unPlug();
//                //                            this.setOpacity(1);
//                //                        }
//                //                        break;
//                //                }
//                //                me.consume();
//                //            }
//            }
//        };
//        //------
//        getMarker().addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
//            e.consume();
//        });
//        getMarker().addEventHandler(MouseDragEvent.MOUSE_DRAGGED, e->{
//            e.consume();
//        });
//        getMarker().setOnMouseEntered(enterMouse);
//        getMarker().setOnMouseExited(exitMouse);
//
//        getMarker().addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, dragExitHndl);
//
//        setBindX(new SimpleDoubleProperty());
//        setBindY(new SimpleDoubleProperty());
//        getMarker().localToSceneTransformProperty().addListener((aza, oldVal, newVal)->{
//            double x=arrowWidth/2;
//            double y=arrowHeight/2;
//            Point2D point=newVal.transform(x,y);
//            point=raschetkz.RaschetKz.drawBoard.sceneToLocal(point);
//            getBindX().set(point.getX());
//            getBindY().set(point.getY());
//        });
    }

    @Override
    public void setWirePointer(LineMarker contactr) {
        setItsConnection(contactr);
    }
    @Override
    protected boolean isProperInstance(LineMarker lm) {
        return lm instanceof MathMarker;
    }

    @Override
    protected Wire createWire(Pin pin, double x, double y) {
        return new MathWire(getSystem(), pin, x, y);
    }

}

