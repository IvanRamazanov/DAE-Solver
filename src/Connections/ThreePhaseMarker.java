package Connections;

import ElementBase.Pin;
import javafx.scene.shape.Circle;

public class ThreePhaseMarker extends LineMarker{

    public ThreePhaseMarker(Wire thisWire){
        super(thisWire);

        Circle view=new Circle();
        view.layoutXProperty().bind(bindX);
        view.layoutYProperty().bind(bindY);
        view.setRadius(4);
        setMarker(view);

        getWire().getWireContacts().add(this);
    }

    /**
     * Creates contact that starts in (x,y) ends in (x,y)
     * @param sx start x point
     * @param sy start y point
     */
    protected ThreePhaseMarker(Wire thisWire, double sx, double sy){
        this(thisWire);
        itsLines.setStartXY(sx, sy);
        bindX.set(sx);
        bindY.set(sy);
        this.setIsPlugged(false);
    }

    /**
     * Create ElectricMarker that goes from 'ec'
     * @param thisWire
     * @param ec
     */
    ThreePhaseMarker(Wire thisWire, Pin ec){
        this(thisWire);
        this.setIsPlugged(false);
        bindStartTo(ec.getBindX(),ec.getBindY());
        ec.setWirePointer(this);
        setItsConnectedPin(ec);
    }

    protected ThreePhaseMarker(Wire thisWire, double startX, double startY, double endX, double endY, boolean isHorizontal, double[] constrList){
        this(thisWire);
        itsLines.setStartXY(startX, startY);
        bindX.set(endX);
        bindY.set(endY);
        itsLines.rearrange(isHorizontal,constrList);
    }

    /**
     * @return the itsBranch
     */
    @Override
    public ThreePhaseWire getWire() {
        return (ThreePhaseWire) super.getWire();
    }

    @Override
    protected boolean isProperInstance(LineMarker lm) {
        return lm instanceof ThreePhaseMarker;
    }
}
