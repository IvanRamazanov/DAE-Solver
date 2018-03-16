/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connections;

import javafx.scene.Node;
import javafx.scene.shape.Line;
import java.util.List;
import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 *
 * @author Ivan
 */
public class ConnectLine {
    private final SimpleDoubleProperty startX=new SimpleDoubleProperty();
    private final SimpleDoubleProperty startY=new SimpleDoubleProperty();
    private final SimpleDoubleProperty endX=new SimpleDoubleProperty();
    private final SimpleDoubleProperty endY=new SimpleDoubleProperty();
    private Cross startMarker;
    private LineMarker marker;
    private Wire owner;
    private List<ExtLine> lines;
    private ChangeListener propListen;
    protected EventHandler lineDragDetect,lineDragOver;
    private boolean diacive=true;
    private boolean easyDraw=true;
    private String color="#000000";

    ConnectLine(Wire owner){
        this.owner=owner;
        lines=new ArrayList();
        this.lines.add(new ExtLine(0,0,0,0,true));
        this.lines.add(new ExtLine(0,0,0,0,false));
        this.lines.add(new ExtLine(0,0,0,0,true));
        bindLines();
        lines.get(0).startXProperty().bind(startX);
        lines.get(0).startYProperty().bind(startY);
        lines.get(2).endXProperty().bind(endX);
        lines.get(2).endYProperty().bind(endY);
        this.propListen = new ChangeListener<Double>(){
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                Draw();
            }
        };
        startX.addListener(propListen);
        startY.addListener(propListen);
        endX.addListener(propListen);
        endY.addListener(propListen);
        startMarker=new Cross(this);
        startMarker.bindThis(startX, startY);
    }

    /**
     * Create and bind
     * @param owner
     */
    ConnectLine(LineMarker owner){
        this(owner.getWire());
        this.marker=owner;

        endX.bind(owner.getBindX());
        endY.bind(owner.getBindY());
    }

    final void bindLines(){
        int len=lines.size()-1;
        for(int i=0;i<len;i++){
            lines.get(i).endXProperty().bindBidirectional(lines.get(i+1).startXProperty());
            lines.get(i).endYProperty().bindBidirectional(lines.get(i+1).startYProperty());
        }
    }

//    public void hide(){
//        lines.forEach(lin->{
//            lin.setVisible(false);
//        });
//        startMarker.setVisible(false);
//    }

    public void show(){
        lines.forEach(lin->{
            lin.setVisible(true);
        });
    }

    public void toFront(){
        lines.forEach(lin->{
            lin.toFront();
        });
    }

    Wire getWire(){
        return owner;
    }

    List<Double> parseLines(){
        List<Double> constraints=new ArrayList();
        for(int i=1;i<this.lines.size()-1;i++){
            constraints.add(lines.get(i).getConstraint());
        }
        return constraints;
    }

    LineMarker getMarker(){
        return marker;
    }

    void rearrange(boolean isHorizontal,double[] constrList){
        setEasyDraw(false);
        getWire().getItsSystem().getDrawBoard().getChildren().removeAll(lines);
        lines.clear();
        int numOfLines=constrList.length+2;
        if(numOfLines==2){
            ExtLine line1=new ExtLine(startX.doubleValue(),startY.doubleValue(),endX.doubleValue(),endY.doubleValue(),isHorizontal);
            line1.startXProperty().bind(startX);
            line1.startYProperty().bind(startY);
            ExtLine line2=new ExtLine(line1.getStartX(),line1.getStartY(),endX.doubleValue(),endY.doubleValue(),!isHorizontal);
            line2.endXProperty().bind(endX);
            line2.endYProperty().bind(endY);

            line2.startXProperty().bindBidirectional(line1.endXProperty());
            line2.startYProperty().bindBidirectional(line1.endYProperty());
            lines.add(line1);
            lines.add(line2);
        }else{
            ExtLine line1=new ExtLine(startX.doubleValue(),startY.doubleValue(),constrList[0],constrList[0],isHorizontal);
            line1.startXProperty().bind(startX);
            line1.startYProperty().bind(startY);
            lines.add(line1);

            isHorizontal=!isHorizontal;
            for(int i=1;i<numOfLines-1;i++){
                ExtLine line2;
                if(i!=constrList.length)
                    line2=new ExtLine(line1.endXProperty().doubleValue(),line1.endYProperty().doubleValue(),constrList[i],constrList[i],isHorizontal);
                else
                    line2=new ExtLine(line1.endXProperty().doubleValue(),line1.endYProperty().doubleValue(),endX.doubleValue(),endY.doubleValue(),isHorizontal);
                line2.setConstraint(constrList[i-1]);
                line2.startXProperty().bindBidirectional(line1.endXProperty());
                line2.startYProperty().bindBidirectional(line1.endYProperty());
                lines.add(line2);

                line1=line2;
                isHorizontal=!isHorizontal;
            }
            ExtLine line2=new ExtLine(line1.getStartX(),line1.getStartY(),endX.doubleValue(),endY.doubleValue(),isHorizontal);
            line2.endXProperty().bind(endX);
            line2.endYProperty().bind(endY);
            line2.startXProperty().bindBidirectional(line1.endXProperty());
            line2.startYProperty().bindBidirectional(line1.endYProperty());
            lines.add(line2);
        }
        setLineDragDetect(lineDragDetect);
        setLineDragOver(lineDragOver);
    }

    final void Draw(){
        if(isEasyDraw()){
            lines.get(2).setStartY(getEndY().get());
            lines.get(2).setStartX((getEndX().get()+getStartX().get())/2);
            lines.get(1).setEndX(lines.get(2).getStartX());
            lines.get(1).setEndY(lines.get(2).getStartY());
            lines.get(1).setStartX(lines.get(1).getEndX());
            lines.get(1).setStartY(getStartY().get());
            lines.get(0).setEndX(lines.get(1).getStartX());
            lines.get(0).setEndY(lines.get(1).getStartY());
        }else{
            int i=lines.size()-1;
            lines.get(0).shiftLine(lines.get(0).getStartX(), lines.get(0).getStartY());
            lines.get(i).shiftLine(lines.get(i).getEndX(), lines.get(i).getEndY());
        }
    }

    void activate(){
        lines.forEach(line->{
            line.setStyle("-fx-stroke: "+color);
        });
        diacive=false;
    }

    void diactivate(){
        lines.forEach(line->{
            line.setStyle("-fx-stroke: red; -fx-stroke-dash-array: 10 5");
        });
        diacive=true;
    }

    final List<ExtLine> getLines(){
        return this.lines;
    }

    void delete(){
        getWire().getItsSystem().getDrawBoard().getChildren().removeAll(lines);
        setLineDragOver(null);
        setLineDragDetect(null);
        getStartMarker().delete();
        getEndX().unbind();
        getEndY().unbind();
        marker=null;
    }

    /**
     * @param lineDraggDetect the lineDragDetect to set
     */
    public final void setLineDragDetect(EventHandler lineDraggDetect) {
        this.lineDragDetect = lineDraggDetect;
        if(lineDraggDetect==null)
            for(Line line:lines)
                line.setOnDragDetected(null);
        else
            for(Line line:lines)
                line.setOnDragDetected(lineDragDetect);

    }

    public final void setLineDragOver(EventHandler lineDragOver) {
        this.lineDragOver=lineDragOver;
        if(lineDragOver==null)
            for(Line line:lines) {
                line.setOnMouseDragOver(null);
            }
        else
            for(Line line:lines)
                line.setOnMouseDragOver(lineDragOver);
    }

    /**
     *
     * @param rgb "#rrggbb"
     */
    public void setColor(String rgb){
        color=rgb;
        getStartMarker().setFill(Paint.valueOf(color));
    }

    /**
     * @return the diacive
     */
    public boolean isDiacive() {
        return diacive;
    }

    /**
     * @return the propListen
     */
    protected ChangeListener getPropListen() {
        return propListen;
    }

    /**
     * @return the startX
     */
    public DoubleProperty getStartX() {
        return startMarker.centerXProperty();
    }

    /**
     * @return the startY
     */
    public DoubleProperty getStartY() {
        return startMarker.centerYProperty();
    }

    /**
     * @return the endX
     */
    public final SimpleDoubleProperty getEndX() {
        return endX;
    }

    /**
     * @return the endY
     */
    public final SimpleDoubleProperty getEndY() {
        return endY;
    }

    /**
     * In local coords
     * @param x
     * @param y
     */
    public final void setStartXY(double x,double y) {
        getStartMarker().setCenterX(x);
        getStartMarker().setCenterY(y);
    }

    public void setCrossMarkerVisible(boolean flag) {
        getStartMarker().setVisible(flag);
    }

    public void bindStart(SimpleDoubleProperty bx,SimpleDoubleProperty by){
        getStartMarker().centerXProperty().bind(bx);
        getStartMarker().centerYProperty().bind(by);
    }

    public void bindStart(Cross C){
        getStartMarker().centerXProperty().bind(C.centerXProperty());
        getStartMarker().centerYProperty().bind(C.centerYProperty());
    }

    List<Node> getView(){
        List<Node> out=new ArrayList<>();

        for(ExtLine line:getLines()){
            out.add(line);
        }
        out.add(getStartMarker());

        return out;
    }

    /**
     * @return the crossMarker
     */
    public final Cross getStartMarker() {
        return startMarker;
    }

    protected class ExtLine extends Line{
        private boolean horizontal;
        private double constraint;

        /**
         *
         * @param sX
         * @param sY
         * @param eX
         * @param eY
         * @param horizon
         */
        ExtLine(double sX,double sY,double eX,double eY,boolean horizon){
            super(sX,sY,eX,eY);
            if(horizon){
                setEndY(sY);
            }else{
                setEndX(sX);
            }
            this.horizontal=horizon;
            getWire().getItsSystem().getDrawBoard().getChildren().add(this);
            this.setStrokeWidth(2);
            if(diacive){
                this.setStyle("-fx-stroke: red; -fx-stroke-dash-array: 10 5");
            }else{
                this.setStyle("-fx-stroke: "+color);
            }
            this.setCursor(Cursor.HAND);

            this.addEventFilter(MouseEvent.MOUSE_DRAGGED, (MouseEvent me)->{
                if(me.getButton().equals(MouseButton.PRIMARY)){
                    setEasyDraw(false);
                    if(this.startXProperty().isBound()){   // mean, that line is first one in 'lines'
                        ExtLine lin=new ExtLine(this.getStartX(),this.getStartY(),this.getEndX(),this.getEndY(),!this.isHorizontal());
                        this.startXProperty().unbind();
                        lin.startXProperty().bind(startX);
                        this.startYProperty().unbind();
                        lin.startYProperty().bind(startY);
                        lin.endXProperty().bindBidirectional(this.startXProperty());
                        lin.endYProperty().bindBidirectional(this.startYProperty());
                        lines.add(0,lin);
                        lin.addEventHandler(MouseDragEvent.DRAG_DETECTED,lineDragDetect);

                        this.shiftLine(me.getX(),me.getY());
                    }else if(this.endXProperty().isBound()){ // mean, that line is last one in 'lines'
                        ExtLine lin=new ExtLine(this.getStartX(),this.getStartY(),this.getEndX(),this.getEndY(),!this.isHorizontal());
                        this.endXProperty().unbind();
                        lin.endXProperty().bind(endX);
                        this.endYProperty().unbind();
                        lin.endYProperty().bind(endY);
                        lin.startXProperty().bindBidirectional(this.endXProperty());
                        lin.startYProperty().bindBidirectional(this.endYProperty());
                        lines.add(lin);
                        lin.addEventHandler(MouseDragEvent.DRAG_DETECTED,lineDragDetect);

                        this.shiftLine(me.getX(),me.getY());
                    }else{ //casual case
                        this.shiftLine(me.getX(),me.getY());
                    }
                }
            });
            EventHandler linePressed=(EventHandler<MouseEvent>)(MouseEvent me)->{
                if(me.getButton()==MouseButton.PRIMARY){
                    this.requestFocus();
                }
                me.consume();
            };
            this.focusedProperty().addListener((obs,oldVal,newVal)->{
                if(newVal)
                    this.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
                else
                    this.setEffect(null);
            });
            this.setOnMousePressed(linePressed);

            this.setOnMouseReleased(e->{
                e.consume();
            });
            this.setOnKeyReleased(k->{
                if(k.getCode()==KeyCode.DELETE){
                    marker.delete();
                }
            });
        }

        void shiftLine(double newX,double newY){
            if(isHorizontal()){
                if(this.startXProperty().isBound()){
                    this.setEndY(newY);
                }else if(this.endXProperty().isBound()){
                    this.setStartY(newY);
                }else{  //general case
                    this.setStartY(newY);
                    this.setEndY(newY);
                }
                setConstraint(newY);
            }else{
                if(this.startXProperty().isBound()){
                    this.setEndX(newX);
                }else if(this.endXProperty().isBound()){
                    this.setStartX(newX);
                }else{  //general case
                    this.setStartX(newX);
                    this.setEndX(newX);
                }
                setConstraint(newX);
            }
            reduceLines();
        }

        /**
         * @return the horizontal
         */
        public boolean isHorizontal() {
            return horizontal;
        }

        private double getLength(){
            return Math.sqrt(Math.pow(getEndX()-getStartX(), 2)+Math.pow(getEndY()-getStartY(), 2));
        }

        /**
         * @param horizontal the horizontal to set
         */
        private void setHorizontal(boolean horizontal) {
            this.horizontal = horizontal;
        }

        private void reduceLines(){
            int itsIndx=lines.lastIndexOf(this),maxInd=lines.size()-1;
            if(itsIndx==0){

            }else if(itsIndx==maxInd){

            }else{  //general case
                //check length
                if(lines.get(itsIndx-1).getLength()==0){
                    if(itsIndx-1==0){
                        this.startXProperty().unbindBidirectional(lines.get(itsIndx-1).endXProperty());
                        this.startYProperty().unbindBidirectional(lines.get(itsIndx-1).endYProperty());
                        getWire().getItsSystem().getDrawBoard().getChildren().remove(lines.get(itsIndx-1));
                        lines.remove(itsIndx-1);
                        this.startXProperty().bind(startX);
                        this.startYProperty().bind(startY);
                    }else{
                        this.endXProperty().unbindBidirectional(lines.get(itsIndx+1).startXProperty());
                        this.endYProperty().unbindBidirectional(lines.get(itsIndx+1).startYProperty());
                        this.startXProperty().unbindBidirectional(lines.get(itsIndx-1).endXProperty());
                        this.startYProperty().unbindBidirectional(lines.get(itsIndx-1).endYProperty());
                        lines.get(itsIndx-1).startXProperty().unbindBidirectional(lines.get(itsIndx-2).endXProperty());
                        lines.get(itsIndx-1).startYProperty().unbindBidirectional(lines.get(itsIndx-2).endYProperty());
                        lines.get(itsIndx-2).endXProperty().bindBidirectional(lines.get(itsIndx+1).startXProperty());
                        lines.get(itsIndx-2).endYProperty().bindBidirectional(lines.get(itsIndx+1).startYProperty());
                        getWire().getItsSystem().getDrawBoard().getChildren().remove(lines.get(itsIndx));
                        getWire().getItsSystem().getDrawBoard().getChildren().remove(lines.get(itsIndx-1));
                        lines.remove(itsIndx);
                        lines.remove(itsIndx-1);
                    }
                }else if(lines.get(itsIndx+1).getLength()==0){
                    if(itsIndx+1==maxInd){
                        this.endXProperty().unbindBidirectional(lines.get(itsIndx+1).startXProperty());
                        this.endYProperty().unbindBidirectional(lines.get(itsIndx+1).startYProperty());
                        getWire().getItsSystem().getDrawBoard().getChildren().remove(lines.get(itsIndx+1));
                        lines.remove(itsIndx+1);
                        this.endXProperty().bind(endX);
                        this.endYProperty().bind(endY);
                    }else{
                        this.startXProperty().unbindBidirectional(lines.get(itsIndx-1).endXProperty());
                        this.startYProperty().unbindBidirectional(lines.get(itsIndx-1).endYProperty());
                        this.endXProperty().unbindBidirectional(lines.get(itsIndx+1).startXProperty());
                        this.endYProperty().unbindBidirectional(lines.get(itsIndx+1).startYProperty());
                        lines.get(itsIndx+1).endXProperty().unbindBidirectional(lines.get(itsIndx+2).startXProperty());
                        lines.get(itsIndx+1).endYProperty().unbindBidirectional(lines.get(itsIndx+2).startYProperty());
                        lines.get(itsIndx+2).startXProperty().bindBidirectional(lines.get(itsIndx-1).endXProperty());
                        lines.get(itsIndx+2).startYProperty().bindBidirectional(lines.get(itsIndx-1).endYProperty());
                        getWire().getItsSystem().getDrawBoard().getChildren().remove(lines.get(itsIndx+1));
                        getWire().getItsSystem().getDrawBoard().getChildren().remove(lines.get(itsIndx));
                        lines.remove(itsIndx+1);
                        lines.remove(itsIndx);
                    }
                }
            }
        }

        /**
         * @return the constraint
         */
        double getConstraint() {
            return constraint;
        }

        /**
         * @param constraint the constraint to set
         */
        void setConstraint(double constraint) {
            this.constraint = constraint;
        }
    }

    /**
     * @return the easyDraw
     */
    public boolean isEasyDraw() {
        return easyDraw;
    }

    /**
     * @param easyDraw the easyDraw to set
     */
    public void setEasyDraw(boolean easyDraw) {
        this.easyDraw = easyDraw;
    }
}


