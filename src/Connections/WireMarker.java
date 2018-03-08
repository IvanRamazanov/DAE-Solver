package Connections;

import ElementBase.ElemPin;
import ElementBase.Pin;
import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

import static Connections.ElectricWire.*;

public class WireMarker extends LineMarker{

    //private Wire itsWire;
//    private ElemPin itsElemCont;
    //private ReadOnlyObjectProperty<Transform> eleContTransf;


    private WireMarker(){
        super();
        Circle view=new Circle();
        view.layoutXProperty().bind(bindX);
        view.layoutYProperty().bind(bindY);
        view.setRadius(4);
        setView(view);



        //EVENT ZONE


        EventHandler dragMouseReleas = (EventHandler<MouseEvent>)(MouseEvent me) -> {
            activeWireConnect=null;
            me.consume();
        };



        view.addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
            activeWireConnect=this;
            e.consume();
        });

        view.addEventHandler(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
        view.addEventHandler(MouseDragEvent.MOUSE_RELEASED, dragMouseReleas);

        //-------------


    }

    protected WireMarker(Wire thisWire){
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
    protected WireMarker(Wire thisWire,double sx,double sy){
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
    WireMarker(ElectricWire thisWire,Pin ec){
        this(thisWire);
        this.setIsPlugged(false);
//            ec.bindWCstartProp(this);
        bindStartTo(ec.getBindX(),ec.getBindY());
        ec.setWirePointer(this);
        setItsConnectedPin(ec);
    }

    protected WireMarker(Wire thisWire,double startX,double startY,double endX,double endY,boolean isHorizontal,double[] constrList){
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

//    public ElemPin getElemContact(){
//        return(this.itsElemCont);
//    }



//    /**
//     * Simply set field
//     * @param pin
//     */
//    public void setElemContact(ElemPin pin){
//        this.itsElemCont=pin;
//    }



    /**
     * Unplug (usually active one) wire contact. If Rank==2 delete second WireCont.
     */
    @Override
    public void unPlug(){
        setIsPlugged(false);
        unbindEndPoint();
        getItsConnectedPin().setItsConnection(null);
        getItsConnectedPin().getView().setOpacity(1.0);
        //getItsConnectedPin().toFront();
        setItsConnectedPin(null);
        switch(getWire().getRank()){
            case 1:
                System.out.println("Unplug in WireMarker case 1");
                WireMarker wc=new WireMarker(getWire(),this.getStartX().get(),this.getStartY().get());
                activeWireConnect=wc;
                //wc.setElemContact(this.getElemContact());
                wc.bindStartTo(bindX,bindY);
                this.bindStartTo(wc.getBindX(),wc.getBindY());
                this.hide();
                break;
            case 2:
                System.out.println("Unplug in WireMarker case 2");
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
                //temp.toFront();
                break;
            default:
                System.out.println("Unplug in WireMarker case default");
                activeWireConnect=this;
        }
    }

    /**
     * @return the itsBranch
     */
    public ElectricWire getWire() {
        return (ElectricWire) super.getWire();
    }

    @Override
    protected boolean isProperInstance(LineMarker lm) {
        return lm instanceof WireMarker;
    }

//    public void setWire(ElectricWire wire) {
//        itsWire=wire;
//    }

//    public DoubleProperty getStartX(){
//        return this.itsLines.getStartX();
//    }
//
//    public DoubleProperty getStartY(){
//        return this.itsLines.getStartY();
//    }
}
