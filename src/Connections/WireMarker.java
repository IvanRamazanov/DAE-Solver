package Connections;

import ElementBase.Pin;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

import static Connections.ElectricWire.*;

public class WireMarker extends LineMarker{

    public WireMarker(Wire thisWire){
        super(thisWire);

        Circle view=new Circle();
        view.layoutXProperty().bind(bindX);
        view.layoutYProperty().bind(bindY);
        view.setRadius(4);
        setMarker(view);


        view.addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
            activeWireConnect=this;
            System.out.println("pressed");
            e.consume();

            view.setOnMouseDragged(WC_MOUSE_DRAG);
            view.setOnMouseDragReleased(WC_MOUSE_RELEAS);

//            view.addEventHandler(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
//            view.addEventHandler(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
        });


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
     * @return the itsBranch
     */
    public ElectricWire getWire() {
        return (ElectricWire) super.getWire();
    }

    @Override
    protected boolean isProperInstance(LineMarker lm) {
        return lm instanceof WireMarker;
    }

}
