package Connections;

import ElementBase.MathInPin;
import ElementBase.MathOutPin;
import ElementBase.MathPin;
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
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

import static Connections.MathWire.*;

public class MathMarker extends LineMarker{

    MathMarker(){
        super();
        Polygon view=new Polygon(0,0,0,8.0,6.0,6.0/2.0);
        view.setTranslateX(-2.0);
        view.setTranslateY(-3.0);
        view.layoutXProperty().bind(bindX);
        view.layoutYProperty().bind(bindY);
        setView(view);

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

    MathMarker(Wire owner,double x,double y){
        this(owner);
        itsLines.setStartXY(x, y);
        bindX.set(x);
        bindY.set(y);
        this.setIsPlugged(false);
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
    @Override
    final public void setItsConnectedPin(Pin pin) {
        super.setItsConnectedPin(pin);
        bindEndTo(pin.getBindX(), pin.getBindY());
        pin.setWirePointer(this);
        setIsPlugged(true);

        if(pin instanceof MathInPin)
            ((MathInPin)pin).setSource(getWire().getSource());


//            itsWire.getSource().setSource(destin);
    }

    @Override
    protected boolean isProperInstance(LineMarker lm) {
        return lm instanceof MathMarker && lm.getWire().getWireContacts().indexOf(lm)==0; // only for source marker
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
        bindX.set(a.getX());
        bindY.set(a.getY());
    }

    @Override
    final public void pushToBack() {
        super.pushToBack();
        //startView.toBack();
    }
}
