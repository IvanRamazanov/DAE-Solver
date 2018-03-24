package Connections;

import ElementBase.Pin;
import Elements.Environment.Subsystem.Subsystem;
import raschetkz.RaschetKz;

public class ThreePhaseWire extends Wire{

    public ThreePhaseWire(Subsystem sys){
        setItsSystem(sys);
        setWireColor("#0505ff");
        sys.getWireList().add(this);
    }

    /**
     * Создает провод и цепляет старт к контакту
     * @param EleCont
     * @param meSceneX
     * @param meSceneY
     */
    public ThreePhaseWire(Subsystem sys, Pin EleCont, double meSceneX, double meSceneY){
        this(sys);
        ThreePhaseMarker wc=new ThreePhaseMarker(this,EleCont);
        wc.setEndPropInSceneCoordinates(meSceneX,meSceneY);
        activeWireConnect=wc;
    }

    public ThreePhaseWire(Subsystem sys,Pin EleCont1,Pin EleCont2){
        this(sys);
        ThreePhaseMarker wc1=new ThreePhaseMarker(this,EleCont1);
        ThreePhaseMarker wc2=new ThreePhaseMarker(this,EleCont2);

        wc2.bindStartTo(wc2.getBindX(),wc2.getBindY());
        wc1.bindStartTo(wc2.getBindX(),wc2.getBindY());

        wc1.bindElemContact(EleCont1);
        wc2.bindElemContact(EleCont2);
    }


    @Override
    protected LineMarker addLineMarker(Wire wire, double ax, double ay, double ex, double ey, boolean isHorizontal, double[] constraints) {
        return new ThreePhaseMarker(wire,ax,ay,ex,ey,isHorizontal,constraints);
    }

    @Override
    protected LineMarker addLineMarker(Wire wire, double x, double y) {
        return new ThreePhaseMarker(wire,x,y);
    }

    @Override
    protected LineMarker addLineMarker(Wire wire) {
        return new ThreePhaseMarker(wire);
    }
}
