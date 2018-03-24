/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connections;

import ElementBase.Pin;
import Elements.Environment.Subsystem.Subsystem;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import raschetkz.RaschetKz;

/**
 *
 * @author Ivan
 */
public class MechWire extends Wire {

    public MechWire(Subsystem sys) {
        setItsSystem(sys);
        setWireColor("#949899");
        sys.getWireList().add(this);
    }

    /**
     * Создает провод и цепляет старт к контакту
     *
     * @param EleCont
     * @param meSceneX
     * @param meSceneY
     */
    public MechWire(Subsystem sys, Pin EleCont, double meSceneX, double meSceneY) {
        this(sys);
        MechMarker wc = new MechMarker(this, EleCont);
        wc.setEndPropInSceneCoordinates(meSceneX, meSceneY);
        activeWireConnect = wc;
    }

    public MechWire(Subsystem sys, Pin EleCont1, Pin EleCont2) {
        this(sys);
        MechMarker wc1 = new MechMarker(this, EleCont1);
        MechMarker wc2 = new MechMarker(this, EleCont2);

        wc2.bindStartTo(wc2.getBindX(),wc2.getBindY());
        wc1.bindStartTo(wc2.getBindX(),wc2.getBindY());

        wc1.bindElemContact(EleCont1);
        wc2.bindElemContact(EleCont2);
//        wc2.hide();
    }

    @Override
    protected LineMarker addLineMarker(Wire wire, double ax, double ay, double ex, double ey, boolean isHorizontal, double[] constraints) {
        return new MechMarker(wire,ax,ay,ex,ey,isHorizontal,constraints);
    }

    @Override
    protected LineMarker addLineMarker(Wire wire,double x,double y) {
        return new MechMarker(wire,x,y);
    }

    @Override
    protected LineMarker addLineMarker(Wire wire) {
        return new MechMarker(wire);
    }

//    void consumeWire(MechMarker eventSource,MouseDragEvent mde){
//        double x=mde.getX(),y=mde.getY();
//        Wire consumedWire=activeWireConnect.getWire();
//
//        switch(consumedWire.getRank()){
//            case 1:
//                activeWireConnect.setWire(this);
//
//                // add to this wire
//                this.getWireContacts().add(activeWireConnect);
//
//                consumedWire.getWireContacts().remove(0);
//                consumedWire.delete();  // remove empty wire
//                //flip
//                activeWireConnect.getItsLine().getStartMarker().unbind();
//                activeWireConnect.setStartPoint(x,y);
//                activeWireConnect.bindElemContact(activeWireConnect.getItsConnectedPin());
//
//                switch(this.getRank()) {
//                    case 1+1:
//
//
//                        MechMarker wm = new MechMarker(this, x, y);
//                        // adjustment
//                        List<Cross> row = new ArrayList();
//                        row.add(activeWireConnect.getItsLine().getStartMarker());
//                        row.add(eventSource.getItsLine().getStartMarker());
//                        row.add(wm.getItsLine().getStartMarker());
//                        getDotList().add(row);
//                        bindCrosses();
//
//                        // move before rebind
//                        wm.setEndProp(eventSource.getBindX().doubleValue(), eventSource.getBindY().doubleValue());
//                        eventSource.bindElemContact(eventSource.getItsConnectedPin());
//                        break;
//                    case 2+1:
//                        // adjustment
//                        row = new ArrayList();
//                        row.add(activeWireConnect.getItsLine().getStartMarker());
//                        row.add(this.getWireContacts().get(0).getItsLine().getStartMarker());
//                        row.add(this.getWireContacts().get(1).getItsLine().getStartMarker());
//                        getDotList().add(row);
//                        bindCrosses();
//                        this.showAll();
//                        break;
//                    default:
//                        addContToCont(eventSource.getItsLine().getStartMarker(),activeWireConnect.getItsLine().getStartMarker());
//                        break;
//                }
//                break;
//            case 2:
//                System.out.println("Hi, you there!"); // TODO This case is present, when fully unplugged wire connects.
//
//                break;
//            default:
//                double sx=activeWireConnect.getStartX().get(),
//                        sy=activeWireConnect.getStartY().get();
//                int rank=this.getRank();
//                // merge lists
//                Point2D p=MathPack.MatrixEqu.findFirst(consumedWire.getDotList(),activeWireConnect.getItsLine().getStartMarker());
//                p=p.add(getDotList().size(),0);
//                getDotList().addAll(consumedWire.getDotList());
//                consumedWire.getWireContacts().remove(activeWireConnect);
//                this.getWireContacts().addAll(getWireContacts());
//                consumedWire.getWireContacts().clear();
//                this.getContContList().addAll(consumedWire.getContContList());
//                consumedWire.getContContList().clear();
//                consumedWire.delete();
//
//                // replace with crosToCros
//                CrossToCrossLine replacementLine = this.addContToCont(sx,sy,x,y);
//                getDotList().get((int) p.getX()).set((int)p.getY(),replacementLine.getStartMarker());
//                activeWireConnect.delete();
//
//                switch(rank){
//                    case 1:
//                        break;
//                    case 2:
//                        List<Cross> row=new ArrayList();
//                        row.add(replacementLine.getEndCrossMarker());
//                        row.add(this.getWireContacts().get(0).getItsLine().getStartMarker());
//                        row.add(this.getWireContacts().get(1).getItsLine().getStartMarker());
//                        getDotList().add(row);
//                        bindCrosses();
//                        showAll();
//                        break;
//                    default:
//                        this.addContToCont(eventSource.getItsLine().getStartMarker(),replacementLine.getEndCrossMarker());
//                }
//        }
//
//    }

//    MechMarker addContact(){
//        MechMarker wc=new MechMarker(this);
//        return wc;
//    }

//    public void delete(){
//        RaschetKz.wireList.remove(this);
//        if(!getWireContacts().isEmpty()){
//            activeWireConnect=getWireContacts().get(0);// for prevent dead loop
//            int i=getWireContacts().size()-1;
//            for(;i>=0;i--){
//                if(!getWireContacts().isEmpty())
//                    getWireContacts().get(i).delete();
//            }
//            activeWireConnect=null;
//        }
//        for(int i=getContContList().size()-1;i>=0;i--){
//            if(!getContContList().isEmpty())
//                getContContList().get(i).delete();
//        }
//        getDotList().clear();
//    }

//    @Override
//    public void setStaticEventFilters(Node source) {
//        source.addEventFilter(MouseDragEvent.MOUSE_DRAGGED, MeC_MOUSE_DRAG);
//        source.addEventFilter(MouseDragEvent.MOUSE_RELEASED, MeC_MOUSE_RELEAS);
//    }
}

