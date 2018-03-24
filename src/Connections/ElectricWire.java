/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connections;

import ElementBase.Pin;
import Elements.Environment.Subsystem.Subsystem;

/**
 *
 * @author Ivan
 */
public class ElectricWire extends Wire{


    public ElectricWire(Subsystem sys){
        setItsSystem(sys);
        setWireColor("#b87333");
        sys.getWireList().add(this);
    }

    /**
     * Создает провод и цепляет старт к контакту
     * @param EleCont
     * @param meSceneX
     * @param meSceneY
     */
    public ElectricWire(Subsystem sys, Pin EleCont, double meSceneX, double meSceneY){
        this(sys);
        ElectricMarker wc=new ElectricMarker(this,EleCont);
        wc.setEndPropInSceneCoordinates(meSceneX,meSceneY);
        activeWireConnect=wc;
    }

    public ElectricWire(Subsystem sys,Pin EleCont1,Pin EleCont2){
        this(sys);
        ElectricMarker wc1=new ElectricMarker(this,EleCont1);
        ElectricMarker wc2=new ElectricMarker(this,EleCont2);

        wc2.bindStartTo(wc2.getBindX(),wc2.getBindY());
        wc1.bindStartTo(wc2.getBindX(),wc2.getBindY());

        wc1.bindElemContact(EleCont1);
        wc2.bindElemContact(EleCont2);
    }

    @Override
    protected LineMarker addLineMarker(Wire wire, double ax, double ay, double ex, double ey, boolean isHorizontal, double[] constraints) {
        return new ElectricMarker(wire,ax,ay,ex,ey,isHorizontal,constraints);
    }

    @Override
    protected LineMarker addLineMarker(Wire wire) {
        return new ElectricMarker(wire);
    }

    @Override
    protected LineMarker addLineMarker(Wire wire,double x,double y) {
        return new ElectricMarker(wire,x,y);
    }


}
