package Connections;

import ElementBase.ElemMechPin;
import ElementBase.Pin;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

import static Connections.MechWire.*;

public class MechMarker extends LineMarker{
//    private Wire itsWire;
    //private ElemMechPin itsElemCont;
    //private ReadOnlyObjectProperty<Transform> eleContTransf;


    MechMarker(){
        super();
        Circle view=new Circle();
        view.layoutXProperty().bind(bindX);
        view.layoutYProperty().bind(bindY);
        view.setRadius(4);
        setView(view);

//        itsLines.setLineDragOver(de->{
//            if(activeWireConnect!=null){
//                if(activeWireConnect instanceof MechMarker)
//                    if(activeWireConnect.getWire()!=this.getWire()){
//                        getWire().consumeWire(this,(MouseDragEvent)de);
//                    }
//            }
//        });

        //EVENT ZONE

        EventHandler dragMouseReleas = (EventHandler<MouseEvent>)(MouseEvent me) -> {
            activeWireConnect=null;
            me.consume();
        };

        view.addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
            activeWireConnect=this;
            view.toFront();
            e.consume();
        });
        view.addEventHandler(MouseDragEvent.MOUSE_DRAGGED, MeC_MOUSE_DRAG);
        view.addEventHandler(MouseDragEvent.MOUSE_RELEASED, dragMouseReleas);
        //-------------

    }

    MechMarker(Wire thisWire){
        this();
        setWire(thisWire);
        itsLines.setColor(thisWire.getWireColor());
        getWire().getWireContacts().add(this);
        pushToBack();
    }

    /**
     * Creates contact that starts in (x,y) ends in (x,y)
     * @param sx start x point
     * @param sy start y point
     */
    MechMarker(Wire thisWire,double sx,double sy){
        this(thisWire);
        itsLines.setStartXY(sx, sy);
        bindX.set(sx);
        bindY.set(sy);
        this.setIsPlugged(false);
    }

    /**
     * Create WireMarker that goes from 'ec'
     * @param thisWire
     * @param ec
     */
    MechMarker(MechWire thisWire,Pin ec){
        this(thisWire);
        this.setIsPlugged(false);
//            ec.bindWCstartProp(this);
        bindStartTo(ec.getBindX(),ec.getBindY());
        ec.setWirePointer(this);
        setItsConnectedPin(ec);
    }

    MechMarker(Wire thisWire,double startX,double startY,double endX,double endY,boolean isHorizontal,double[] constrList){
        this(thisWire);
        itsLines.setStartXY(startX, startY);
        bindX.set(endX);
        bindY.set(endY);
        itsLines.rearrange(isHorizontal,constrList);
    }

    /**
     * Удаление контакта и линии
     */
    @Override
    void delete(){
        dotReduction(activeWireConnect);

        if(getItsConnectedPin()!=null){
            getItsConnectedPin().setItsConnection(null);
            getItsConnectedPin().getView().setOpacity(1.0);
            setItsConnectedPin(null);
        }
        getWire().getWireContacts().remove(this);
        if(getWire().getWireContacts().size()<2){
            if(getWire().getWireContacts().isEmpty()){
                getWire().delete();
            }else{
                LineMarker wm=getWire().getWireContacts().get(0);
                if(wm.isPlugged()){
                    getWire().delete();
                }
            }
        }
        setWire(null);
        raschetkz.RaschetKz.drawBoard.getChildren().remove(getView());
        unbindEndPoint();
        unBindStartPoint();
        itsLines.delete();
        itsLines=null;
    }

    /**
     * Unplug (usually active one) wire contact. If Rank==2 delete second WireCont.
     */
    public void unPlug(){
        setIsPlugged(false);
        unbindEndPoint();
        getItsConnectedPin().setItsConnection(null);
        getItsConnectedPin().getView().setOpacity(1.0);
        setItsConnectedPin(null);
        switch(getWire().getRank()){
            case 1:
                MechMarker wc=new MechMarker(getWire(),this.getStartX().get(),this.getStartY().get());
                activeWireConnect=wc;
                //wc.setElemContact(this.getElemContact());
                wc.bindStartTo(bindX,bindY);
                this.bindStartTo(wc.getBindX(),wc.getBindY());
                this.hide();
                break;
            case 2:
                LineMarker loser;
                if(getWire().getWireContacts().get(0)==this){
                    loser=getWire().getWireContacts().get(1);
                }else{
                    loser=getWire().getWireContacts().get(0);
                }
                activeWireConnect=this;
                Pin temp=loser.getItsConnectedPin();
                loser.delete();
                temp.setWirePointer(this);
                setItsConnectedPin(temp);
                this.show();
                break;
            default:
                activeWireConnect=this;
        }
    }

    /**
     * @return the itsBranch
     */
    public MechWire getWire() {
        return (MechWire) super.getWire();
    }

    @Override
    protected boolean isProperInstance(LineMarker lm) {
        return lm instanceof MechMarker;
    }

//    public void setWire(MechWire wire) {
//        getWire()=wire;
//    }


}
