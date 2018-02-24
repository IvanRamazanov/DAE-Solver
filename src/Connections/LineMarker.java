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
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.List;


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
    protected Shape view;

    private Wire itsWire;
    private Pin itsConnectedPin;

    LineMarker(){
        itsLines=new ConnectLine(this);
        this.plugged=new SimpleBooleanProperty(false);
        this.plugged.addListener((Boolean,old,newval)->{
            if(newval){
                this.activate();
            }else{
                this.diactivate();
            }
        });
    }

    abstract void delete();

    public void pushToBack() {
        view.toBack();
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

    abstract public void unPlug();

    /**
     * Отрисовка линии
     * @param x - координата в scene coord
     * @param y
     */
    public void setEndProp(double x,double y){
        Point2D a=new Point2D(x, y);
        a=raschetkz.RaschetKz.drawBoard.sceneToLocal(a);
        if(!bindX.isBound()){
            bindX.set(a.getX());
            bindY.set(a.getY());
        }
    }

    public void setStartPoint(double x,double y){
        itsLines.setStartXY(x, y);
    }

    /**
     * Делает контакт и линию активной
     */
    protected void activate(){
        //activeWireContact=this;
        view.setVisible(false);
        getItsLine().activate();
    }

    /**
     * Деактивирует контакт и делает линию пунктирной
     */
    protected void diactivate(){
        getItsLine().diactivate();
        view.setVisible(true);
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
                    LineMarker major = getWire().getDotList().get(0).get(0).getOwner().marker;
                    LineMarker minor = getWire().getDotList().get(0).get(1).getOwner().marker;
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
        view.startFullDrag();
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
}
