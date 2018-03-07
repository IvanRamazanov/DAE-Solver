package Connections;

import ElementBase.MathInPin;
import ElementBase.MathOutPin;
import ElementBase.MathPin;
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
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

import static Connections.MathWire.*;

public class MathMarker extends LineMarker{
    //List<MathMarker> subContacts;
    //private MathWire itsWire;

    //private MathPin connectedPin;
    //private Polygon startView;



    MathMarker(){
        super();
//            subContacts=new ArrayList();
        Polygon view=new Polygon(0,0,0,8.0,6.0,6.0/2.0);
        view.setTranslateX(-2.0);
        view.setTranslateY(-3.0);
        view.layoutXProperty().bind(bindX);
        view.layoutYProperty().bind(bindY);
        setView(view);

//            startView=new Polygon(0,0,0,8,6,4);
//            startView.setTranslateX(-2.0);
//            startView.setTranslateY(-3.0);
//            this.itsLines.getStartMarker().centerXProperty().bind(startView.layoutXProperty());
//            this.itsLines.getStartMarker().centerYProperty().bind(startView.layoutYProperty());
//            raschetkz.RaschetKz.drawBoard.getChildren().add(startView);

            /*itsLines.setLineDragDetect(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent mde){
                    if(mde.getButton()==MouseButton.SECONDARY){
                        MathMarker mc=new MathMarker(itsWire,mde.getX(), mde.getY());
                        activeMathMarker=mc;
//                        subContacts.add(mc);
                        mc.view.startFullDrag();
                    }
                }
            });*/

        itsLines.setLineDragDetect((EventHandler<MouseEvent>)(MouseEvent me)->{
            if(me.getButton()== MouseButton.SECONDARY){
                if(getWire().getWireContacts().size()==1){
                    me.consume();
                    return;
                }
                if(getWire().getWireContacts().size()==2){
                    MathMarker newCont=new MathMarker(getWire(),me.getX(), me.getY());
                    Wire.activeWireConnect=newCont;
                    adjustCrosses(newCont,
                            getWire().getWireContacts().get(0),
                            getWire().getWireContacts().get(1));
                    List<Cross> list=new ArrayList();
                    list.add(newCont.getItsLine().getStartMarker());
                    list.add(getWire().getWireContacts().get(0).getItsLine().getStartMarker());
                    list.add(getWire().getWireContacts().get(1).getItsLine().getStartMarker());
                    getWire().getDotList().add(list);

                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
                    newCont.startFullDrag();
                    getWire().getWireContacts().forEach(wc->{
                        wc.show();
                    });
                    me.consume();
                }
                else{
                    MathMarker newCont=new MathMarker(getWire(),me.getX(), me.getY());
                    Wire.activeWireConnect=newCont;
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
                    newCont.startFullDrag();
                    getWire().addContToCont(this.getItsLine().getStartMarker(),newCont.getItsLine().getStartMarker());
                }
                me.consume();
            }
        });

        view.setOnDragDetected(me->{
            Wire.activeWireConnect=this;
            getWire().setDragSource(view);
            this.pushToBack();
            view.startFullDrag();
            view.addEventFilter(MouseEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
            view.addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
        });

    }

    MathMarker(Wire owner){
        this();
        setWire(owner);
        itsLines.setColor(owner.getWireColor());
        getWire().getWireContacts().add(this);
        pushToBack();
    }

    MathMarker(Wire owner,MathPin inp){
        this(owner);
        bindEndTo(inp.getBindX(), inp.getBindY());
        inp.setWirePointer(this);
        setConnectedPin(inp);
        pushToBack();
        if(inp instanceof MathOutPin){
            getWire().setSource((MathOutPin)inp);
            //connectedPin=(MathInPin)inp;
        }
    }

    MathMarker(Wire owner,MathOutPin start,MathInPin end){
        this(owner);
        getWire().setSourcePointer(start);
        setItsConnectedPin(end);
//            source=start;
        bindX.bind(end.getBindX());
        bindY.bind(end.getBindY());

        /*?????????*/itsLines.bindStart(start.getBindX(), start.getBindY());


        plugged.set(true);
        //        destin.hide();
//            source.hide();
        end.setMathConnLink(this);
        start.setWirePointer(this);
        //majorConnect=this;
    }

    MathMarker(Wire owner,double x,double y){
        this(owner);
        itsLines.setStartXY(x, y);
        bindX.set(x);
        bindY.set(y);
        this.setIsPlugged(false);

        //itsLines.bindStart(majorConnect.getItsLine().getStartMarker()); ?

    }

    MathMarker(Wire thisWire,double startX,double startY,double endX,double endY,boolean isHorizontal,double[] constrList){
        this(thisWire);
        itsLines.setStartXY(startX, startY);
        bindX.set(endX);
        bindY.set(endY);
        itsLines.rearrange(isHorizontal,constrList);
    }

    public List<Double> getValue(){
        if(getWire().getSource()!=null){
            return getWire().getSource().getValue();
        }else{
            List<Double> out=new ArrayList();
            out.add(0.0);
            return out;
        }
    }

    /**
     * @return the itsBranch
     */
    public MathWire getWire() {
        return (MathWire) super.getWire();
    }

    /**
     * Удаление контакта и линии
     */
    @Override
    void delete(){
        // check if this is sourceMarker!
        if(this.equals(getWire().getSourceMarker())){
            if(getItsConnectedPin()!=null){
                getItsConnectedPin().setItsConnection(null);
                getItsConnectedPin().getView().setOpacity(1.0);
                setItsConnectedPin(null);
            }
            getWire().getWireContacts().remove(this);
            raschetkz.RaschetKz.drawBoard.getChildren().remove(getView());
            unbindEndPoint();
            unBindStartPoint();
            itsLines.delete();
            itsLines=null;
            getWire().delete();
            setWire(null);
            return;
        }

        dotReduction(Wire.activeWireConnect);

        if(getItsConnectedPin()!=null){
            getItsConnectedPin().clear();
            setItsConnectedPin(null);
        }
        getWire().getWireContacts().remove(this);
        if(getWire().getWireContacts().size()<2)
            getWire().delete();
        setWire(null);
        raschetkz.RaschetKz.drawBoard.getChildren().remove(getView());
        unbindEndPoint();
        unBindStartPoint();
        itsLines.delete();
        itsLines=null;
    }

    @Override
    public void unPlug(){
        Wire.activeWireConnect=this;
        setIsPlugged(false);
        this.bindX.unbind();
        this.bindY.unbind();
        getItsConnectedPin().setItsConnection(null);
        getItsConnectedPin().getView().setOpacity(1.0);

        if(getItsConnectedPin() instanceof MathInPin){  //==? this.connectedPin.clearWireContact();

            unbindEndPoint();
            getView().setVisible(true);
            getView().toBack();
        }else{
            getWire().setSource(null); //TODO IMPLEMENT THIS!!!
//                if(connectedPin!=null)
//                    itsWire.setSource(null);
//                startView.layoutXProperty().unbind();
//                startView.layoutYProperty().unbind();
//                startView.setVisible(true);
//                startView.toBack();
        }


        setItsConnectedPin(null);

//            caller.show();
    }

    @Override
    protected void activate(){
        getItsLine().activate();
        getView().setVisible(false);
//            this.startView.setVisible(false);
    }

    @Override
    protected void diactivate(){
        getItsLine().diactivate();
        //        super.diactivate();
        //        this.startView.setVisible(false);
    }

    public void eraseDragSource(){
        getWire().setDragSource(null);
    }

    /**
     * Links and binds end propetry of line
     * @param pin the destin to set
     */
    final public void setConnectedPin(MathPin pin) {
        setItsConnectedPin(pin);
        bindEndTo(pin.getBindX(), pin.getBindY());
        pin.setWirePointer(this);
        setIsPlugged(true);

        if(pin instanceof MathInPin)
            ((MathInPin)pin).setSource(getWire().getSource());


//            itsWire.getSource().setSource(destin);
    }


    /**
     * Отрисовка линии
     * @param x - координата в scene coord
     * @param y
     */
    @Override
    public void setEndProp(double x,double y){
        Point2D a=new Point2D(x, y);
        a=raschetkz.RaschetKz.drawBoard.sceneToLocal(a);
//            if(dragSource==null)
//                if(!bindX.isBound()){
//                    bindX.set(a.getX());
//                    bindY.set(a.getY());
//                }else{
//                    startView.setLayoutX(a.getX());
//                    startView.setLayoutY(a.getY());
//                }
//            else
//                if(dragSource==view){
//                    bindX.set(a.getX());
//                    bindY.set(a.getY());
//                }else{
//                    startView.setLayoutX(a.getX());
//                    startView.setLayoutY(a.getY());
//                }

        bindX.set(a.getX());
        bindY.set(a.getY());
    }

    @Override
    final public void pushToBack() {
        super.pushToBack();
        //startView.toBack();
    }
}
