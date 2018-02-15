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

    private MathPin connectedPin;
    //private Polygon startView;



    MathMarker(){
        super();
//            subContacts=new ArrayList();
        view=new Polygon(0,0,0,8.0,6.0,6.0/2.0);
        view.setTranslateX(-2.0);
        view.setTranslateY(-3.0);
        view.layoutXProperty().bind(bindX);
        view.layoutYProperty().bind(bindY);
        raschetkz.RaschetKz.drawBoard.getChildren().add(view);

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
        itsLines.setColor(COLOR);
        itsLines.setLineDragDetect((EventHandler<MouseEvent>)(MouseEvent me)->{
            if(me.getButton()== MouseButton.SECONDARY){
                if(getWire().getWireContacts().size()==1){
                    me.consume();
                    return;
                }
                if(getWire().getWireContacts().size()==2){
                    MathMarker newCont=new MathMarker(getWire(),me.getX(), me.getY());
                    activeMathMarker=newCont;
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
                    activeMathMarker=newCont;
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
                    newCont.startFullDrag();
                    getWire().addContToCont(this,newCont);
                }
                me.consume();
            }
        });

//            itsLines.getEndX().bind(bindX);
//            itsLines.getEndY().bind(bindY);


//            startView.setOnMouseEntered(me->{
//                startView.toFront();
//                startView.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
//                startView.setCursor(Cursor.HAND);
//            });
//            startView.setOnMouseExited(me->{
//                startView.setEffect(null);
//                startView.setCursor(Cursor.DEFAULT);
//                dragSource=null;
//            });
//            startView.setOnDragDetected(me->{
//                activeMathMarker=this;
//                this.pushToBack();
//                startView.startFullDrag();
//                startView.addEventFilter(MouseEvent.MOUSE_DRAGGED, Connections.LineMarker.MC_MOUSE_DRAG);
//                startView.addEventFilter(MouseDragEvent.MOUSE_RELEASED, Connections.LineMarker.MC_MOUSE_RELEAS);
//            });

        view.setOnMouseEntered(me->{
//                view.toFront();
            view.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
            view.setCursor(Cursor.HAND);
        });
        view.setOnMouseExited(me->{
            view.setEffect(null);
            view.setCursor(Cursor.DEFAULT);
        });
        view.setOnDragDetected(me->{
            getWire().activeMathMarker=this;
            getWire().setDragSource(view);
            this.pushToBack();
            view.startFullDrag();
            view.addEventFilter(MouseEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
            view.addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
        });

    }

    MathMarker(MathWire owner){
        this();
        setWire(owner);
        getWire().getWireContacts().add(this);
        pushToBack();
    }

    MathMarker(MathWire owner,MathPin inp){
        this(owner);
        bindEndTo(inp.getArrowX(), inp.getArrowY());
        inp.setItsConnection(this);
        setConnectedPin(inp);
        pushToBack();
        if(inp instanceof MathInPin){

            connectedPin=(MathInPin)inp;
        }else{

            getWire().setSource((MathOutPin)inp);
//                inp.hide();
        }
    }

    MathMarker(MathWire owner,MathOutPin start,MathInPin end){
        this(owner);
        getWire().setSourcePointer(start);
        connectedPin=end;
//            source=start;
        bindX.bind(end.getArrowX());
        bindY.bind(end.getArrowY());

        /*?????????*/itsLines.bindStart(start.getArrowX(), start.getArrowY());


        plugged.set(true);
        //        destin.hide();
//            source.hide();
        end.setMathConnLink(this);
        start.setMathConnLink(this);
        //majorConnect=this;
    }

    MathMarker(MathWire owner,double x,double y){
        this(owner);
        itsLines.setStartXY(x, y);
        bindX.set(x);
        bindY.set(y);
        this.setIsPlugged(false);

        //itsLines.bindStart(majorConnect.getItsLine().getStartMarker()); ?

    }

    MathMarker(MathWire thisWire,double startX,double startY,double endX,double endY,int numOfLines,boolean isHorizontal,List<Double> constrList){
        this(thisWire);
        itsLines.setStartXY(startX, startY);
        bindX.set(endX);
        bindY.set(endY);
        itsLines.rearrange(numOfLines,isHorizontal,constrList);
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

    public DoubleProperty getStartX(){
        return this.itsLines.getStartX();
    }

    public DoubleProperty getStartY(){
        return this.itsLines.getStartY();
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
     * Удаление контакта и линии
     */
    @Override
    void delete(){
        // check if this is sourceMarker!
        if(this.equals(getWire().getSourceMarker())){
            if(connectedPin!=null){
                connectedPin.clearPin();
                connectedPin=null;
            }
            getWire().getWireContacts().remove(this);
            raschetkz.RaschetKz.drawBoard.getChildren().remove(view);
            unbindEndPoint();
            unBindStartPoint();
            itsLines.delete();
            itsLines=null;
            getWire().delete();
            setWire(null);
            return;
        }

        //reduce dotList
        Point2D p=MathPack.MatrixEqu.findFirst(getWire().getDotList(), this.getItsLine().getStartMarker());
        if(p!=null&&activeMathMarker==null){
            switch(getWire().getDotList().size()){
                case 1: //triple line Wire
                    getWire().getDotList().get(0).remove((int)p.getY());
                    LineMarker major=getWire().getDotList().get(0).get(0).getOwner().marker;
                    LineMarker minor=getWire().getDotList().get(0).get(1).getOwner().marker;
                    major.getItsLine().getStartX().bind(minor.getBindX());
                    major.getItsLine().getStartY().bind(minor.getBindY());
                    minor.getItsLine().getStartX().bind(major.getBindX());
                    minor.getItsLine().getStartY().bind(major.getBindY());
                    minor.hide();
                    getWire().getDotList().remove(0);
                    break;
                default: // case of cont to cont
                    List<Cross> line=getWire().getDotList().get((int)p.getX());
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
                            Point2D nP=MathPack.MatrixEqu.findFirst(getWire().getDotList(),reducedOne);
                            getWire().getDotList().get((int)nP.getX()).set((int)nP.getY(),master.getStartMarker());
                            master.getStartMarker().unbind();
                            master.setStartXY(reducedOne.getCenterX(), reducedOne.getCenterY());

                            getWire().getDotList().remove((int)p.getX());
                            //deleting
                            loser.deleteQuiet();

                        }
                        getWire().bindCrosses();
                    }else if(len>3){
                        throw new Error("Size > 3 not supported yet...");
                    }

            }

        }

        if(connectedPin!=null){
            connectedPin.clearPin();
            connectedPin=null;
        }
        getWire().getWireContacts().remove(this);
        if(getWire().getWireContacts().size()<2)
            getWire().delete();
        setWire(null);
        raschetkz.RaschetKz.drawBoard.getChildren().remove(view);
        unbindEndPoint();
        unBindStartPoint();
        itsLines.delete();
        itsLines=null;
    }

    @Override
    public void unPlug(){
        activeMathMarker=this;
        setIsPlugged(false);
        this.bindX.unbind();
        this.bindY.unbind();

        if(connectedPin instanceof MathInPin){  //==? this.connectedPin.clearWireContact();
            connectedPin.clearPin();
            unbindEndPoint();
            view.setVisible(true);
            view.toBack();
        }else{
            getWire().setSource(null);
//                if(connectedPin!=null)
//                    itsWire.setSource(null);
            connectedPin.clearPin();
//                startView.layoutXProperty().unbind();
//                startView.layoutYProperty().unbind();
//                startView.setVisible(true);
//                startView.toBack();
        }


        connectedPin=null;

//            caller.show();
    }

    @Override
    protected void activate(){
        getItsLine().activate();
        this.view.setVisible(false);
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
     * @return the destin
     */
    public MathPin getConnectedPin() {
        return connectedPin;
    }

    /**
     * Links and binds end propetry of line
     * @param pin the destin to set
     */
    final public void setConnectedPin(MathPin pin) {
        connectedPin = pin;
        bindEndTo(pin.getArrowX(), pin.getArrowY());
        pin.setItsConnection(this);
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
