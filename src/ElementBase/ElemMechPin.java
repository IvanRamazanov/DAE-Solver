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
//    SchemeElement owner;
//    MechMarker wireCont;
//    SimpleDoubleProperty centerX,centerY;
    private final double width=4,height=4;

    public ElemMechPin(SchemeElement owner){
        setOwner(owner);
    }

    public ElemMechPin(SchemeElement owner, int x, int y){
        setView(new Rectangle(width,height));
        setOwner(owner);
        getView().setLayoutX(x);
        getView().setLayoutY(y);

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
            if(MechWire.activeWireConnect!=null&&getItsConnection()==null){
                System.out.println("You are plugged");
                MechWire.activeWireConnect.getWire().setEnd(this);
                getView().toFront();
            }

        };
        EventHandler dragExitHndl =(EventHandler<MouseDragEvent>)(MouseDragEvent me) -> {
            System.out.println("Hello from drag exit (in ElemMechPin)! Source: "+me.getGestureSource());
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

            if(MechWire.activeWireConnect!=null&&getItsConnection()!=null){
                //if(Wire.activeWireConnect.getElemContact()!=this){
                if(MechWire.activeWireConnect.getIsPlugged().getValue()){


                }
            }
            me.consume();
        };
        getView().addEventHandler(MouseEvent.DRAG_DETECTED, me -> {
            if(me.getButton()== MouseButton.PRIMARY){
                if(getItsConnection()==null){
                    RaschetKz.MechWireList.add(new MechWire(this,
                            me.getSceneX(),me.getSceneY()));
                    MechWire.activeWireConnect.startFullDrag();
                    getView().addEventFilter(MouseEvent.MOUSE_DRAGGED, MechWire.MeC_MOUSE_DRAG);
                    getView().addEventFilter(MouseDragEvent.MOUSE_RELEASED, MechWire.MeC_MOUSE_RELEAS);

                }else{
                    System.out.println("Hello from drag detect!");
                    getItsConnection().unPlug();
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

        setBindX(new SimpleDoubleProperty());
        setBindY(new SimpleDoubleProperty());
        getView().localToSceneTransformProperty().addListener((aza, oldVal, newVal)->{
//            Point2D point=newVal.transform(getView().getLayoutX(), getView().getLayoutY());
            Point2D point=newVal.transform(2,2);
            point=RaschetKz.drawBoard.sceneToLocal(point);
            getBindX().set(point.getX());
            getBindY().set(point.getY());
        });
        getView().setFill(Paint.valueOf("#ffffff"));
        getView().setStrokeWidth(2);
        getView().setStroke(Paint.valueOf("#000000"));
        getView().setCursor(Cursor.HAND);
    }



    /**
     * If isReal true, add EC pointer to WC and bind CenterProp.
     * If false just bind CenterProp.
     * @param contactr wireCont
     */
    public void setWirePointer(MechMarker contactr){
        setItsConnection(contactr);
        getView().setOpacity(0);
        //this.wireCont.activate();
        //contactr.bindElemContact(this);
        //this.setVisible(false);
    }

}
