/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connections;

import ElementBase.Pin;
import MathPack.MatrixEqu;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

import static Connections.Wire.activeWireConnect;


/**
 *
 * @author Ivan
 */
public abstract class LineMarker{

    protected SimpleDoubleProperty bindX=new SimpleDoubleProperty(),
            bindY=new SimpleDoubleProperty();
    protected ConnectLine itsLines;
    protected SimpleBooleanProperty plugged;
    protected EventHandler lineDraggedDetect;
    private Shape marker;

    private Wire itsWire;
    private Pin itsConnectedPin;

    LineMarker(Wire itswire){
        setWire(itswire);

        itsLines=new ConnectLine(this);
        this.plugged=new SimpleBooleanProperty(false);
        this.plugged.addListener((Boolean,old,newval)->{
            if(newval){
                this.activate();
            }else{
                this.diactivate();
            }
        });

        EventHandler<MouseEvent> dd=new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                if(me.getButton()== MouseButton.SECONDARY){
                    if(getWire().getWireContacts().size()==1){
                        me.consume();
                        return;
                    }
                    if(getWire().getWireContacts().size()==2){
                        LineMarker newCont=getWire().addLineMarker(getWire(),me.getX(), me.getY());
                        Wire.activeWireConnect=newCont;
                        adjustCrosses(newCont,
                                getWire().getWireContacts().get(0),
                                getWire().getWireContacts().get(1));
                        List<Cross> list=new ArrayList();
                        list.add(newCont.getItsLine().getStartMarker());
                        list.add(getWire().getWireContacts().get(0).getItsLine().getStartMarker());
                        list.add(getWire().getWireContacts().get(1).getItsLine().getStartMarker());
                        getWire().getDotList().add(list);

                        getWire().setStaticEventFilters((Node)me.getSource());

                        newCont.startFullDrag();
                        getWire().showAll();
                        me.consume();
                    }
                    else{
                        LineMarker newCont=getWire().addLineMarker(getWire(),me.getX(), me.getY());
                        Wire.activeWireConnect=newCont;

                        getWire().setStaticEventFilters((Node)me.getSource());

                        newCont.startFullDrag();
                        getWire().addContToCont(getItsLine().getStartMarker(),newCont.getItsLine().getStartMarker());
                    }
                    me.consume();
                }
            }
        };

        itsLines.setLineDragDetect(dd);

        itsLines.setLineDragOver(de->{
            if(Wire.activeWireConnect!=null){
                if(isProperInstance(Wire.activeWireConnect))
                    if(Wire.activeWireConnect.getWire()!=this.getWire()){
                        getWire().consumeWire(this,(MouseDragEvent)de);
                    }
            }
        });
    }

    void delete(){
        dotReduction(activeWireConnect);

        if(getItsConnectedPin()!=null){
            getItsConnectedPin().setItsConnection(null);
            getItsConnectedPin().getView().setOpacity(1.0);
            //setItsConnectedPin(null);
        }
        getWire().getWireContacts().remove(this);
        if(getWire().getWireContacts().size()<2){
            if(getWire().getWireContacts().isEmpty()){
                getWire().delete();
            }else{
                LineMarker wm=getWire().getWireContacts().get(0);
                if(wm.isPlugged()){
                    getWire().delete();
                }
            }
        }
        getWire().getItsSystem().getDrawBoard().getChildren().remove(getMarker());
        setWire(null);
        unbindEndPoint();
        unBindStartPoint();
        itsLines.delete();
        itsLines=null;
    }

    public void unPlug(){
        setIsPlugged(false);
        unbindEndPoint();
        getItsConnectedPin().setItsConnection(null);
        getItsConnectedPin().getView().setOpacity(1.0);
        //getItsConnectedPin().toFront();
        setItsConnectedPin(null);
        switch(getWire().getRank()){
            case 1:
                //System.out.println("Unplug in WireMarker case 1");
                //WireMarker wc=new WireMarker(getWire(),this.getStartX().get(),this.getStartY().get());
                LineMarker wc=getWire().addLineMarker(getWire(),this.getStartX().get(),this.getStartY().get());
                activeWireConnect=wc;
                //wc.setElemContact(this.getElemContact());
                wc.bindStartTo(bindX,bindY);
                this.bindStartTo(wc.getBindX(),wc.getBindY());
                this.hide();
                break;
            case 2:
                //System.out.println("Unplug in WireMarker case 2");
                LineMarker loser;
                if(getWire().getWireContacts().get(0)==this){
                    loser=getWire().getWireContacts().get(1);
                }else{
                    loser=getWire().getWireContacts().get(0);
                }
                activeWireConnect=this;
                Pin temp=loser.getItsConnectedPin();
                loser.delete();
                temp.setWirePointer(this);
                setItsConnectedPin(temp);
                this.show();
                //temp.toFront();
                break;
            default:
                //System.out.println("Unplug in WireMarker case default");
                activeWireConnect=this;
        }
    }

    public void pushToBack() {
        marker.toBack();
        for(Line l:this.itsLines.getLines()){
            l.toBack();
        }
    }

    public SimpleBooleanProperty getIsPlugged() {
        return plugged;
    }

    public final void setIsPlugged(boolean val) {
        plugged.set(val);
    }

    public boolean isPlugged(){
        return(plugged.get());
    }

    void Draw(){
        this.itsLines.Draw();
    }

    public void hide(){
        this.itsLines.hide();
    }

    void unBindStartPoint(){
        itsLines.getStartX().unbind();
        itsLines.getStartY().unbind();
    }

    final void bindStartTo(SimpleDoubleProperty x,SimpleDoubleProperty y){
        this.itsLines.getStartMarker().bind(x, y);
    }

    final void bindEndTo(SimpleDoubleProperty x,SimpleDoubleProperty y){
        bindX.bind(x);
        bindY.bind(y);
    }

    void unbindEndPoint(){
        bindX.unbind();
        bindY.unbind();
    }

    /**
     *
     * @param eleCont
     */
    public void bindElemContact(Pin eleCont){
        setItsConnectedPin(eleCont);
        bindEndTo(eleCont.getBindX(), eleCont.getBindY());
        eleCont.setWirePointer(this);
        this.setIsPlugged(true);

        eleCont.toFront();
    }

    /**
     * Отрисовка линии
     * @param x - координата в scene coord
     * @param y
     */
    public void setEndPropInSceneCoordinates(double x,double y){
        Point2D a=new Point2D(x, y);
        a=getWire().getItsSystem().getDrawBoard().sceneToLocal(a);
        if(!bindX.isBound()){
            bindX.set(a.getX());
            bindY.set(a.getY());
        }
    }

    public void setEndPoint(double x,double y){
        bindX.set(x);
        bindY.set(y);
    }

    public void setStartPoint(double x,double y){
        itsLines.setStartXY(x, y);
    }

    /**
     * Делает контакт и линию активной
     */
    protected void activate(){
        //activeWireContact=this;
        marker.setVisible(false);
        getItsLine().activate();
    }

    /**
     * Деактивирует контакт и делает линию пунктирной
     */
    protected void diactivate(){
        getItsLine().diactivate();
        marker.setVisible(true);
        //activeWireContact=null;
    }

    void hideStartMarker(){
        getItsLine().getStartMarker().setVisible(false);
    }

    /**
     * @return the itsLine
     */
    public ConnectLine getItsLine() {
        return itsLines;
    }

    protected void dotReduction(LineMarker active){
        //reduce dotList
        Point2D p=MathPack.MatrixEqu.findFirst(getWire().getDotList(), this.getItsLine().getStartMarker());
        if(p!=null&&active==null){
            switch(getWire().getDotList().size()){
                case 1: //triple line Wire
                    getWire().getDotList().get(0).remove((int)p.getY());
                    LineMarker major = getWire().getDotList().get(0).get(0).getOwner().getMarker();
                    LineMarker minor = getWire().getDotList().get(0).get(1).getOwner().getMarker();
                    getWire().getDotList().remove(0);
                    if(!major.isPlugged()&&!minor.isPlugged()){
                        // TODO implement this! Floating wire case

                    }else if(!major.isPlugged()){
                        // only major should left
                        Pin ep=minor.getItsConnectedPin();
                        minor.delete();

                        // set pointers
                        ep.setWirePointer(major);
                        major.setItsConnectedPin(ep);

                        major.bindStartTo(ep.getBindX(),ep.getBindY());
                        major.hideStartMarker();
                    }else if (!minor.isPlugged()){
                        // only minor should left
                        Pin ep=major.getItsConnectedPin();
                        major.delete();

                        //set pointers
                        ep.setWirePointer(minor);
                        minor.setItsConnectedPin(ep);

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
                    List<Cross> line=getWire().getDotList().get((int)p.getX());
                    int dotBindLineLen=line.size(),ind=(int)p.getY();
                    if(dotBindLineLen==3){
                        if(line.get((ind+1)%dotBindLineLen).getOwner() instanceof CrossToCrossLine &&
                                line.get((ind+2)%dotBindLineLen).getOwner() instanceof CrossToCrossLine){
                            CrossToCrossLine loser,master=(CrossToCrossLine)line.get((ind+1)%dotBindLineLen).getOwner();
                            if(master.isEasyDraw()){
                                loser=master;
                                master=(CrossToCrossLine)line.get((ind+2)%dotBindLineLen).getOwner();
                            }else
                                loser=(CrossToCrossLine)line.get((ind+2)%dotBindLineLen).getOwner();
                            Cross rep,los;
                            if(line.contains(loser.getStartMarker())) {
                                los = loser.getEndCrossMarker();
                            }else{
                                los=loser.getStartMarker();
                            }
                            if(line.contains(master.getStartMarker())) {
                                rep = master.getStartMarker();
                                rep.unbind();
                                master.setStartXY(los.getCenterX(),los.getCenterY());
                            }else{
                                rep=master.getEndCrossMarker();
                                rep.unbind();
                                master.setEndXY(los.getCenterX(),los.getCenterY());
                            }
                            Point2D nP= MatrixEqu.findFirst(getWire().getDotList(),los);
                            getWire().getDotList().get((int)nP.getX()).set((int)nP.getY(),rep);
                            loser.deleteQuiet();
                            getWire().getDotList().remove((int)p.getX());
                        }else{ // only one crToCrLine's cross
                            CrossToCrossLine loser;
                            ConnectLine master;
                            Cross reducedOne;
                            if(line.get((ind+1)%dotBindLineLen).getOwner() instanceof CrossToCrossLine){
                                loser=(CrossToCrossLine)line.get((ind+1)%dotBindLineLen).getOwner();
                                master=line.get((ind+2)%dotBindLineLen).getOwner();
                            }else{
                                loser=(CrossToCrossLine)line.get((ind+2)%dotBindLineLen).getOwner();
                                master=line.get((ind+1)%dotBindLineLen).getOwner();
                            }
                            if(loser.getStartMarker().equals(line.get(ind))){
                                reducedOne=loser.getEndCrossMarker();  //TODO I think this is always false!
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
                    }else if(dotBindLineLen>3){
                        throw new Error("Size > 3 not supported yet...");
                    }

            }

        }
    }

    public void startFullDrag(){
        marker.startFullDrag();
    }

    public static void adjustCrosses(LineMarker... inp){
        Cross sup=inp[0].itsLines.getStartMarker();
        inp[0].itsLines.setCrossMarkerVisible(true);
        for(int i=1;i<inp.length;i++){
            inp[i].itsLines.getStartMarker().bindToCross(sup);
            inp[i].itsLines.setCrossMarkerVisible(false);
        }
    }

    /**
     * @return the centerX
     */
    public SimpleDoubleProperty getBindX() {
        return bindX;
    }

    /**
     * @return the centerY
     */
    public SimpleDoubleProperty getBindY() {
        return bindY;
    }

    public Wire getWire() {
        return itsWire;
    }

    public void setWire(Wire w) {
        itsWire=w;
    }

    public Pin getItsConnectedPin() {
        return itsConnectedPin;
    }

    public void setItsConnectedPin(Pin itsConnectedPin) {
        this.itsConnectedPin = itsConnectedPin;
    }

    public DoubleProperty getStartX(){
        return this.itsLines.getStartX();
    }

    public DoubleProperty getStartY(){
        return this.itsLines.getStartY();
    }

    protected Shape getMarker() {
        return marker;
    }

    protected void setMarker(Shape vview) {
        if(marker !=null)
            getWire().getItsSystem().getDrawBoard().getChildren().remove(marker);

        marker = vview;

        EventHandler connDragDetectHandle =(EventHandler<MouseEvent>) (MouseEvent me) -> {
            if(me.getButton()== MouseButton.PRIMARY){
                this.pushToBack();
                startFullDrag();
            }
            me.consume();
        };

        EventHandler enterMouse= (EventHandler<MouseEvent>)(MouseEvent me) ->{
            marker.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
            //marker.toFront();
            marker.setCursor(Cursor.HAND);
        };

        EventHandler exitMouse= (EventHandler<MouseEvent>)(MouseEvent me) ->{
            marker.setEffect(null);
            marker.setCursor(Cursor.DEFAULT);
        };

        marker.addEventHandler(MouseDragEvent.DRAG_DETECTED, connDragDetectHandle);
        marker.addEventHandler(MouseEvent.MOUSE_ENTERED, enterMouse);
        marker.addEventHandler(MouseEvent.MOUSE_EXITED, exitMouse);

        getWire().getItsSystem().getDrawBoard().getChildren().add(marker);
    }

    /**
     * push to front of marker
     */
    public void toFront(){
        itsLines.toFront();
        getMarker().toFront();
    }

    public void show(){
        this.itsLines.show();
    }

    List<Node> getView(){
        List<Node> out=new ArrayList<>();

        out.add(getMarker());
        out.addAll(getItsLine().getView());

        return out;
    }

    abstract protected boolean isProperInstance(LineMarker lm);
}
