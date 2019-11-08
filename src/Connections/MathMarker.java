package Connections;

import ElementBase.MathInPin;
import ElementBase.Pin;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;

import static Connections.MathWire.MC_MOUSE_DRAG;
import static Connections.MathWire.MC_MOUSE_RELEAS;

public class MathMarker extends LineMarker{

    MathMarker(Wire owner){
        super(owner);

        Polygon view=new Polygon(0,0,0,8.0,6.0,6.0/2.0);
        view.setTranslateX(-2.0);
        view.setTranslateY(-3.0);
        view.layoutXProperty().bind(bindX);
        view.layoutYProperty().bind(bindY);
        setMarker(view);

        view.setOnDragDetected(me->{
            Wire.activeWireConnect=this;
            getWire().setDragSource(view);
            this.pushToBack();
            view.startFullDrag();
            view.addEventFilter(MouseEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
            view.addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
        });

        getWire().getWireContacts().add(this);
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
    @Override
    public MathWire getWire() {
        return (MathWire) super.getWire();
    }

    /**
     * Удаление контакта и линии
     */
    @Override
    void delete(){
        if(this.equals(getWire().getSourceMarker())){
            Wire w=getWire();
            super.delete();
            w.delete();
        }else
            super.delete();
    }

    @Override
    public void unPlug(){
        Wire.activeWireConnect=this;
        setIsPlugged(false);
        this.bindX.unbind();
        this.bindY.unbind();
        Pin p=getItsConnectedPin();
        p.setItsConnection(null);
        p.getView().setOpacity(1.0);

        if(p instanceof MathInPin){  //==? this.connectedPin.clearWireContact();

            ((MathInPin)p).setSource(null);

            unbindEndPoint();
            getMarker().setVisible(true);
            getMarker().toBack();
        }else{
            getWire().setSource(null); //TODO IMPLEMENT THIS!!!
            unbindEndPoint();
            getMarker().setVisible(true);
            getMarker().toBack();
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
        getMarker().setVisible(false);
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
        if(pin!=null) {
            bindEndTo(pin.getBindX(), pin.getBindY());
            pin.setWirePointer(this);
            setIsPlugged(true);

            if (pin instanceof MathInPin)
                ((MathInPin) pin).setSource(getWire().getSource());
        }
    }

    @Override
    protected boolean isProperInstance(LineMarker lm) {
        return lm instanceof MathMarker && lm.getWire().getWireContacts().indexOf(lm)==0; // only for source marker
    }

}
