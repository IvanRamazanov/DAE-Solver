package Connections;

import ElementBase.ElemPin;
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

import static Connections.ElectricWire.*;

public class WireMarker extends LineMarker{

    //private Wire itsWire;
//    private ElemPin itsElemCont;
    //private ReadOnlyObjectProperty<Transform> eleContTransf;


    WireMarker(){
        super();
        view=new Circle();
        view.layoutXProperty().bind(bindX);
        view.layoutYProperty().bind(bindY);
        ((Circle)view).setRadius(4);
        itsLines.setColor(COLOR);

        itsLines.setLineDragOver(de->{
            if(activeWireConnect!=null){
                if(activeWireConnect.getWire()!=this.getWire()){
                    getWire().consumeWire(this,(MouseDragEvent)de);
                }
            }
        });

        itsLines.setLineDragDetect((EventHandler<MouseEvent>)(MouseEvent me)->{
            if(me.getButton()== MouseButton.SECONDARY){
                if(getWire().getWireContacts().size()==1){
                    me.consume();
                    return;
                }
                if(getWire().getWireContacts().size()==2){
                    WireMarker newCont=new WireMarker(getWire(),me.getX(), me.getY());
                    activeWireConnect=newCont;
                    adjustCrosses(newCont,
                            getWire().getWireContacts().get(0),
                            getWire().getWireContacts().get(1));
                    List<Cross> list=new ArrayList();
                    list.add(newCont.getItsLine().getStartMarker());
                    list.add(getWire().getWireContacts().get(0).getItsLine().getStartMarker());
                    list.add(getWire().getWireContacts().get(1).getItsLine().getStartMarker());
                    getWire().getDotList().add(list);

                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
                    newCont.startFullDrag();
                    getWire().showAll();
                    me.consume();
                }
                else{
                    WireMarker newCont=new WireMarker(getWire(),me.getX(), me.getY());
                    activeWireConnect=newCont;
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
                    newCont.startFullDrag();
                    getWire().addContToCont(this.getItsLine().getStartMarker(),newCont.getItsLine().getStartMarker());
                }
                me.consume();
            }
        });

        //EVENT ZONE
        EventHandler connDragDetectHandle =(EventHandler<MouseEvent>) (MouseEvent me) -> {
            if(me.getButton()==MouseButton.PRIMARY){
                this.pushToBack();
                startFullDrag();
            }
            me.consume();
        };

        EventHandler dragMouseReleas = (EventHandler<MouseEvent>)(MouseEvent me) -> {
            activeWireConnect=null;
            me.consume();
        };

        EventHandler enterMouse= (EventHandler<MouseEvent>)(MouseEvent me) ->{
            view.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
            //view.toFront();
            view.setCursor(Cursor.HAND);
        };

        EventHandler exitMouse= (EventHandler<MouseEvent>)(MouseEvent me) ->{
            view.setEffect(null);
            view.setCursor(Cursor.DEFAULT);
        };

        view.addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
            activeWireConnect=this;
            e.consume();
        });
        view.addEventHandler(MouseDragEvent.DRAG_DETECTED, connDragDetectHandle);
        view.addEventHandler(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
        view.addEventHandler(MouseDragEvent.MOUSE_RELEASED, dragMouseReleas);
        view.addEventHandler(MouseEvent.MOUSE_ENTERED, enterMouse);
        view.addEventHandler(MouseEvent.MOUSE_EXITED, exitMouse);
        //-------------

        raschetkz.RaschetKz.drawBoard.getChildren().add(view);
    }

    WireMarker(Wire thisWire){
        this();
        setWire(thisWire);
        getWire().getWireContacts().add(this);
        pushToBack();
    }

    /**
     * Creates contact that starts in (x,y) ends in (x,y)
     * @param sx start x point
     * @param sy start y point
     */
    WireMarker(Wire thisWire,double sx,double sy){
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
    WireMarker(ElectricWire thisWire,ElemPin ec){
        this(thisWire);
        this.setIsPlugged(false);
//            ec.bindWCstartProp(this);
        bindStartTo(ec.getBindX(),ec.getBindY());
        ec.setWirePointer(this);
        setItsConnectedPin(ec);
    }

    WireMarker(ElectricWire thisWire,double startX,double startY,double endX,double endY,int numOfLines,boolean isHorizontal,List<Double> constrList){
        this(thisWire);
        itsLines.setStartXY(startX, startY);
        bindX.set(endX);
        bindY.set(endY);
        itsLines.rearrange(numOfLines,isHorizontal,constrList);
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
                WireMarker wm=getWire().getWireContacts().get(0);
                if(wm.isPlugged()){
                    getWire().delete();
                }
            }
        }
        setWire(null);
        raschetkz.RaschetKz.drawBoard.getChildren().remove(view);
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

    public void show(){
        this.itsLines.show();
    }

    /**
     * push to front of view
     */
    public void toFront(){
        this.view.toFront();
    }

    /**
     * Unplug (usually active one) wire contact. If Rank==2 delete second WireCont.
     */
    @Override
    public void unPlug(){
        setIsPlugged(false);
        unbindEndPoint();
        getItsConnectedPin().setItsConnection(null);
        getItsConnectedPin().getView().setOpacity(1.0);
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
                WireMarker loser;
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
                temp.toFront();
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

//    public void setWire(ElectricWire wire) {
//        itsWire=wire;
//    }

    public DoubleProperty getStartX(){
        return this.itsLines.getStartX();
    }

    public DoubleProperty getStartY(){
        return this.itsLines.getStartY();
    }
}
