package ElementBase;

import Connections.MechMarker;
import Connections.MechWire;
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
import javafx.scene.shape.Rectangle;
import raschetkz.RaschetKz;

public class ElemMechPin extends Pin{
    SchemeElement owner;
    MechMarker wireCont;
    SimpleDoubleProperty centerX,centerY;

    public ElemMechPin(SchemeElement owner){
        this.owner=owner;
    }

    public ElemMechPin(SchemeElement owner, int x, int y){
        setView(new Rectangle(4,4));
        this.owner=owner;
        getView().setLayoutX(x);
        getView().setLayoutY(y);
//        ((Circle)view).setRadius(4);

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
            if(MechWire.activeWireConnect!=null&&this.wireCont==null){
                System.out.println("You are plugged");
                MechWire.activeWireConnect.getWire().setEnd(this);
                getView().toFront();
            }

        };
        EventHandler dragExitHndl =(EventHandler<MouseDragEvent>)(MouseDragEvent me) -> {
            System.out.println("Hello from drag exit (in ElemPin)! Source: "+me.getGestureSource());
            if(MechWire.activeWireConnect!=null)
                //if(!ElectricWire.activeWireConnect.getElemContact().equals(this))
                switch(me.getButton()){
                    case PRIMARY:
                        if(me.isPrimaryButtonDown()){
//                                    this.wireCont=null;
                            MechWire.activeWireConnect.unPlug();
                            getView().setOpacity(1);
                        }
                        break;
                    case SECONDARY:
                        if(me.isSecondaryButtonDown()){
//                                    this.wireCont=null;
                            MechWire.activeWireConnect.unPlug();
                            getView().setOpacity(1);
                        }
                        break;
                }
//            this.addEventFilter(MouseEvent.MOUSE_DRAGGED, ElectricWire.WC_MOUSE_DRAG);
//            this.addEventFilter(MouseDragEvent.MOUSE_RELEASED, ElectricWire.WC_MOUSE_RELEAS);

            if(MechWire.activeWireConnect!=null&&getWireContact()!=null){
                //if(Wire.activeWireConnect.getElemContact()!=this){
                if(MechWire.activeWireConnect.getIsPlugged().getValue()){


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
            if(me.getButton()== MouseButton.PRIMARY){
                if(wireCont==null){
//                    this.setOpacity(0);
                    RaschetKz.MechWireList.add(new MechWire((ElemMechPin)me.getSource(),
                            me.getSceneX(),me.getSceneY()));
                    MechWire.activeWireConnect.startFullDrag();
                    getView().addEventFilter(MouseEvent.MOUSE_DRAGGED, MechWire.MeC_MOUSE_DRAG);
                    getView().addEventFilter(MouseDragEvent.MOUSE_RELEASED, MechWire.MeC_MOUSE_RELEAS);

                }else{
//                    this.setOpacity(1);
//                    Wire.activeWireConnect=wireCont;
                    System.out.println("Hello from drag detect!");
                    this.wireCont.unPlug();
//                    this.wireCont=null;
//                    ElectricWire.activeWireConnect=getWireContact();
                    getView().startFullDrag();
                    getView().addEventFilter(MouseEvent.MOUSE_DRAGGED, MechWire.MeC_MOUSE_DRAG);
                    getView().addEventFilter(MouseDragEvent.MOUSE_RELEASED, MechWire.MeC_MOUSE_RELEAS);
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

        this.centerX=new SimpleDoubleProperty(getView().getLayoutX());
        this.centerY=new SimpleDoubleProperty(getView().getLayoutY());
        getView().localToSceneTransformProperty().addListener((aza, oldVal, newVal)->{
            Point2D point=newVal.transform(getView().getLayoutX(), getView().getLayoutY());
            point=RaschetKz.drawBoard.sceneToLocal(point);
            this.centerX.set(point.getX());
            this.centerY.set(point.getY());
        });
        getView().setFill(Paint.valueOf("#ffffff"));
        getView().setStrokeWidth(2);
        getView().setStroke(Paint.valueOf("#000000"));
        getView().setCursor(Cursor.HAND);
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
    public void setWirePointer(MechMarker contactr){
        this.wireCont=contactr;
        getView().setOpacity(0);
        //this.wireCont.activate();
        //contactr.bindElemContact(this);
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

    public MechMarker getWireContact(){
        return(wireCont);
    }

    /**
     * Remove wire cont reference, and setOpacity to 1.0
     */
    public void clearWireContact(){
        this.wireCont=null;
        getView().setOpacity(1.0);
    }

    public SchemeElement getOwner(){
        return(owner);
    }
}
