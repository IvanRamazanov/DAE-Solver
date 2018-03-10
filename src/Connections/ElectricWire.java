/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connections;

import ElementBase.Pin;
import Elements.Environment.Subsystem;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import raschetkz.RaschetKz;

/**
 *
 * @author Ivan
 */
public class ElectricWire extends Wire{
    public static final EventHandler WC_MOUSE_DRAG = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent me) {
            //System.out.println("WC_MOUSE_DRAG");
            if(activeWireConnect!=null)
                if(!activeWireConnect.getIsPlugged().get()){
                    activeWireConnect.setEndPropInSceneCoordinates(me.getSceneX(), me.getSceneY());
                }
            me.consume();
        }
    };
    public static final EventHandler WC_MOUSE_RELEAS= new EventHandler<MouseDragEvent>() {
        @Override
        public void handle(MouseDragEvent me){
            //System.out.println("WC_DELETE");
            if(activeWireConnect!=null)
                activeWireConnect.toFront();
            activeWireConnect=null;
//            ((Node)me.getGestureSource()).removeEventFilter(MouseEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
//            ((Node)me.getGestureSource()).removeEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
            ((Node)me.getGestureSource()).setOnMouseDragged(null);
            ((Node)me.getGestureSource()).setOnMouseDragReleased(null);
            me.consume();
        }
    };

    public ElectricWire(Subsystem sys){
        setItsSystem(sys);
        setWireColor("#b87333");
        RaschetKz.wireList.add(this);
    }

    /**
     * Создает провод и цепляет старт к контакту
     * @param EleCont
     * @param meSceneX
     * @param meSceneY
     */
    public ElectricWire(Subsystem sys, Pin EleCont, double meSceneX, double meSceneY){
        this(sys);
        WireMarker wc=new WireMarker(this,EleCont);
        wc.setEndPropInSceneCoordinates(meSceneX,meSceneY);
        activeWireConnect=wc;
    }

    public ElectricWire(Subsystem sys,Pin EleCont1,Pin EleCont2){
        this(sys);
        WireMarker wc1=new WireMarker(this,EleCont1);
        WireMarker wc2=new WireMarker(this,EleCont2);

        wc2.bindStartTo(wc1.getBindX(),wc1.getBindY());
        wc1.bindStartTo(wc2.getBindX(),wc2.getBindY());

        wc1.bindElemContact(EleCont1);
        wc2.bindElemContact(EleCont2);
        wc2.hide();
    }

    /**
     * Биндит линию к контакту элемента
     * @param elemCont контакт элемента
     */
    @Override
    public boolean setEnd(Pin elemCont){
        switch(getWireContacts().size()){
            case 1:
                //System.out.println("Set end case 1");
                Pin oldEc=activeWireConnect.getItsConnectedPin();   // начальный O--->
                activeWireConnect.bindElemContact(elemCont);           // --->О цепляем
                LineMarker wcNew=addLineMarker(this); // ? bind?      // <---O новый
                wcNew.bindElemContact(oldEc);
                wcNew.bindStartTo(elemCont.getBindX(),elemCont.getBindY());
                wcNew.hide();
                break;
            case 2:
                //System.out.println("Set end case 2");
                if(!getWireContacts().get(0).isPlugged()&&!getWireContacts().get(1).isPlugged()) {   // free floating wire case
                    LineMarker loser;
                    if(getWireContacts().get(0).equals(activeWireConnect))
                        loser=getWireContacts().get(1);
                    else
                        loser=getWireContacts().get(0);
                    activeWireConnect.setEndPoint(loser.getBindX().get(),loser.getBindY().get());
                    activeWireConnect.bindStartTo(elemCont.getBindX(),elemCont.getBindY());
                    elemCont.setWirePointer(activeWireConnect);
                    activeWireConnect.setItsConnectedPin(elemCont);
                    loser.delete();
                }else{
                    throw new Error("Unexpected case!");
                }
                break;
            default:
                //System.out.println("Set end case default");
                activeWireConnect.bindElemContact(elemCont);
        }
        return true;
    }

//    void consumeWire(WireMarker eventSource,MouseDragEvent mde){
//        double x=mde.getX(),y=mde.getY();
//        Wire consumedWire=activeWireConnect.getWire();
//
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
//                        WireMarker wm = new WireMarker(this, x, y);
//                        // adjustment
//                        List<Cross> row = new ArrayList();
//                        row.add(activeWireConnect.getItsLine().getStartMarker());
//                        row.add(eventSource.getItsLine().getStartMarker());
//                        row.add(wm.getItsLine().getStartMarker());
//                        this.getDotList().add(row);
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
//                        this.getDotList().add(row);
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
//                        this.getDotList().add(row);
//                        this.bindCrosses();
//                        showAll();
//                        break;
//                    default:
//                        this.addContToCont(eventSource.getItsLine().getStartMarker(),replacementLine.getEndCrossMarker());
//                }
//        }
//    }

    @Override
    protected LineMarker addLineMarker(Wire wire, double ax, double ay, double ex, double ey, boolean isHorizontal, double[] constraints) {
        return new WireMarker(wire,ax,ay,ex,ey,isHorizontal,constraints);
    }

    @Override
    protected LineMarker addLineMarker(Wire wire) {
        return new WireMarker(wire);
    }

    @Override
    protected LineMarker addLineMarker(Wire wire,double x,double y) {
        return new WireMarker(wire,x,y);
    }

    @Override
    public void setStaticEventFilters(Node source) {
        source.setOnMouseDragged(WC_MOUSE_DRAG);
        source.setOnMouseDragReleased( WC_MOUSE_RELEAS);
    }


}
