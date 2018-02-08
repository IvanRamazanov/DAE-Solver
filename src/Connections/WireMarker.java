package Connections;

import ElementBase.ElemPin;
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

    private ElectricWire itsWire;
    private ElemPin itsElemCont;
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
                    itsWire.consumeWire(this,(MouseDragEvent)de);
                }
            }
        });

        itsLines.setLineDragDetect((EventHandler<MouseEvent>)(MouseEvent me)->{
            if(me.getButton()== MouseButton.SECONDARY){
                if(itsWire.getWireContacts().size()==1){
                    me.consume();
                    return;
                }
                if(itsWire.getWireContacts().size()==2){
                    WireMarker newCont=new WireMarker(itsWire,me.getX(), me.getY());
                    activeWireConnect=newCont;
                    adjustCrosses(newCont,
                            itsWire.getWireContacts().get(0),
                            itsWire.getWireContacts().get(1));
                    List<Cross> list=new ArrayList();
                    list.add(newCont.getItsLine().getStartMarker());
                    list.add(itsWire.getWireContacts().get(0).getItsLine().getStartMarker());
                    list.add(itsWire.getWireContacts().get(1).getItsLine().getStartMarker());
                    itsWire.dotList.add(list);

                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
                    newCont.startFullDrag();
                    itsWire.showAll();
                    me.consume();
                }
                else{
                    WireMarker newCont=new WireMarker(itsWire,me.getX(), me.getY());
                    activeWireConnect=newCont;
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
                    newCont.startFullDrag();
                    itsWire.addContToCont(this.getItsLine().getStartMarker(),newCont.getItsLine().getStartMarker());
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
            view.setCursor(Cursor.HAND);
        };

        EventHandler exitMouse= (EventHandler<MouseEvent>)(MouseEvent me) ->{
            view.setEffect(null);
            view.setCursor(Cursor.DEFAULT);
        };

        view.addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
            activeWireConnect=this;
            view.toFront();
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

    WireMarker(ElectricWire thisWire){
        this();
        this.itsWire=thisWire;
        itsWire.getWireContacts().add(this);
        pushToBack();
    }

    /**
     * Creates contact that starts in (x,y) ends in (x,y)
     * @param sx start x point
     * @param sy start y point
     */
    WireMarker(ElectricWire thisWire,double sx,double sy){
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
        this.itsElemCont=ec;
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
        //reduce dotList
        Point2D p=MathPack.MatrixEqu.findFirst(itsWire.dotList, this.getItsLine().getStartMarker());
        if(p!=null&&activeWireConnect==null){
            switch(itsWire.dotList.size()){
                case 1: //triple line Wire
                    itsWire.dotList.get(0).remove((int)p.getY());
                    WireMarker major= (WireMarker) itsWire.dotList.get(0).get(0).getOwner().marker;
                    WireMarker minor= (WireMarker) itsWire.dotList.get(0).get(1).getOwner().marker;
                    itsWire.dotList.remove(0);
                    if(!major.isPlugged()&&!minor.isPlugged()){
                        // TODO implement this!

                    }else if(!major.isPlugged()){
                        // only major should left
                        ElemPin ep=minor.getElemContact();
                        minor.delete();

                        // set pointers
                        ep.setWirePointer(major);
                        major.itsElemCont=ep;

                        major.bindStartTo(ep.getBindX(),ep.getBindY());
                        major.hideStartMarker();
                    }else if (!minor.isPlugged()){
                        // only minor should left
                        ElemPin ep=major.getElemContact();
                        major.delete();

                        //set pointers
                        ep.setWirePointer(minor);
                        minor.itsElemCont=ep;

                        minor.bindStartTo(ep.getBindX(),ep.getBindY());
                        minor.hideStartMarker();
                    }else {
                        // bind to each other
                        major.getItsLine().getStartX().bind(minor.getBindX());
                        major.getItsLine().getStartY().bind(minor.getBindY());
                        minor.getItsLine().getStartX().bind(major.getBindX());
                        minor.getItsLine().getStartY().bind(major.getBindY());

                        if(minor.getItsLine().isEasyDraw()){
                            minor.hide();
                            major.hideStartMarker();
                        }else{
                            major.hide();
                            minor.hideStartMarker();
                        }
                    }
                    break;
                default: // case of cont to cont
                    List<Cross> line=itsWire.dotList.get((int)p.getX());
                    int len=line.size(),ind=(int)p.getY();
                    if(len==3){
                        if(line.get((ind+1)%len).getOwner() instanceof CrossToCrossLine && line.get((ind+2)%len).getOwner() instanceof CrossToCrossLine){

                        }else{ // only one crToCrLine's cross
                            CrossToCrossLine loser;
                            ConnectLine master;
                            Cross reducedOne;
                            if(line.get((ind+1)%len).getOwner() instanceof CrossToCrossLine){
                                loser=(CrossToCrossLine)line.get((ind+1)%len).getOwner();
                                master=line.get((ind+2)%len).getOwner();
                            }else{
                                loser=(CrossToCrossLine)line.get((ind+2)%len).getOwner();
                                master=line.get((ind+1)%len).getOwner();
                            }
                            if(loser.getStartMarker().equals(line.get(ind))){
                                reducedOne=loser.getEndCrossMarker();
                            }else{
                                reducedOne=loser.getStartMarker();
                            }
                            Point2D nP=MathPack.MatrixEqu.findFirst(itsWire.dotList,reducedOne);
                            itsWire.dotList.get((int)nP.getX()).set((int)nP.getY(),master.getStartMarker());
                            master.getStartMarker().unbind();
                            master.setStartXY(reducedOne.getCenterX(), reducedOne.getCenterY());

                            itsWire.dotList.remove((int)p.getX());
                            //deleting
                            loser.deleteQuiet();

                        }
                        itsWire.bindCrosses();
                    }else if(len>3){
                        throw new Error("Size > 3 not supported yet...");
                    }

            }

        }

        if(this.itsElemCont!=null){
            this.itsElemCont.clearWireContact();
            itsElemCont=null;
        }
        itsWire.getWireContacts().remove(this);
        if(itsWire.getWireContacts().size()<2){
            if(itsWire.getWireContacts().isEmpty()){
                itsWire.delete();
            }else{
                WireMarker wm=itsWire.getWireContacts().get(0);
                if(wm.isPlugged()){
                    itsWire.delete();
                }
            }
        }
        itsWire=null;
        raschetkz.RaschetKz.drawBoard.getChildren().remove(view);
        unbindEndPoint();
        unBindStartPoint();
        itsLines.delete();
        itsLines=null;
    }

    public ElemPin getElemContact(){
        return(this.itsElemCont);
    }

    /**
     *
     * @param eleCont
     */
    public void bindElemContact(ElemPin eleCont){
        this.itsElemCont=eleCont;
//            eleCont.bindWCendProp(this);
        bindEndTo(eleCont.getBindX(), eleCont.getBindY());
        eleCont.setWirePointer(this);
        this.setIsPlugged(true);
    }

    /**
     * Simply set field
     * @param pin
     */
    public void setElemContact(ElemPin pin){
        this.itsElemCont=pin;
    }

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
    public void unPlug(){
        setIsPlugged(false);
        unbindEndPoint();
        this.itsElemCont.clearWireContact();
        this.itsElemCont=null;
        switch(this.itsWire.getRank()){
            case 1:
                WireMarker wc=new WireMarker(itsWire,this.getStartX().get(),this.getStartY().get());
                activeWireConnect=wc;
                //wc.setElemContact(this.getElemContact());
                wc.bindStartTo(bindX,bindY);
                this.bindStartTo(wc.getBindX(),wc.getBindY());
                this.hide();
                break;
            case 2:
                WireMarker loser;
                if(this.itsWire.getWireContacts().get(0)==this){
                    loser=this.itsWire.getWireContacts().get(1);
                }else{
                    loser=this.itsWire.getWireContacts().get(0);
                }
                activeWireConnect=this;
                ElemPin temp=loser.itsElemCont;
                loser.delete();
                temp.setWirePointer(this);
                this.itsElemCont=temp;
                this.show();
                break;
            default:
                activeWireConnect=this;
        }
    }

    /**
     * @return the itsBranch
     */
    public ElectricWire getWire() {
        return itsWire;
    }

    public void setWire(ElectricWire wire) {
        itsWire=wire;
    }

    public DoubleProperty getStartX(){
        return this.itsLines.getStartX();
    }

    public DoubleProperty getStartY(){
        return this.itsLines.getStartY();
    }
}
