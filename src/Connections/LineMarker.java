/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connections;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;


/**
 *
 * @author Ivan
 */
public abstract class LineMarker{
//    protected SimpleDoubleProperty endXProp;
//    protected SimpleDoubleProperty endYProp;
//    protected SimpleDoubleProperty startXProp;
//    protected SimpleDoubleProperty startYProp;
    protected SimpleDoubleProperty bindX=new SimpleDoubleProperty(),
            bindY=new SimpleDoubleProperty();
    protected ConnectLine itsLines;
    protected SimpleBooleanProperty isPlugged;
    //Cross anchor;
    protected EventHandler lineDraggedDetect;
    protected Shape view;
    
    public static final EventHandler MC_MOUSE_DRAG = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent me) {
            if(!MathMarker.activeMathConnect.getIsPlugged().get()){
                MathMarker.activeMathConnect.setEndProp(me.getSceneX(), me.getSceneY());
            }
            me.consume();
        }
    };
    public static final EventHandler MC_MOUSE_RELEAS= new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent me) {
            MathMarker.activeMathConnect.eraseDragSource();
            MathMarker.activeMathConnect=null;
            ((Node)me.getSource()).removeEventFilter(MouseEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
            ((Node)me.getSource()).removeEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
            me.consume();
        }    
    };
    
    
    
    
    LineMarker(){
        itsLines=new ConnectLine(this);
        this.isPlugged=new SimpleBooleanProperty(false);
        this.isPlugged.addListener((Boolean,old,newval)->{
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
        return isPlugged;
    }
    
    public final void setIsPlugged(boolean val) {
        isPlugged.set(val);
    }
    
    public boolean isFine(){
        return(this.itsLines.isDiacive());
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
    
    void unBindEndPoint(){
        itsLines.getEndX().unbind();
        itsLines.getEndY().unbind();
    }
    
    /**
     * Отрисовка линии
     * @param x - координата в scene coord
     * @param y 
     */
    public void setEndProp(double x,double y){
        Point2D a=new Point2D(x, y);
        a=raschetkz.RaschetKz.drawBoard.sceneToLocal(a);
//        if(itsLines.getEndX().isBound()){
//            itsLines.getStartX().set(a.getX());
//            itsLines.getStartY().set(a.getY());
//        }else{
//            itsLines.getEndX().set(a.getX());
//            itsLines.getEndY().set(a.getY());
//        }
//        if(itsLines.getEndX().isBound()){
            bindX.set(a.getX());
            bindY.set(a.getY());
//        }else{
//            itsLines.setCrossMarkerXY(a.getX(),a.getY());
//        }
    }
    
    public void setStartPoint(double x,double y){
        itsLines.setCrossMarkerXY(x, y);
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
    
    /**
     * @return the itsLine
     */
    public ConnectLine getItsLine() {
        return itsLines;
    }
    
    public void startFullDrag(){
        view.startFullDrag();
    }
    
    public static void adjustCrosses(LineMarker... inp){
//        DoubleProperty xprop=inp[0].getItsLine().getStartX();
//        DoubleProperty yprop=inp[0].getItsLine().getStartY();
//        inp[0].itsLines.setCrossMarkerVisible(true);
//        for(int i=1;i<inp.length;i++){
//            inp[i].getItsLine().getStartX().unbind();
//            inp[i].getItsLine().getStartY().unbind();
//            inp[i].getItsLine().getStartX().bind(xprop);
//            inp[i].getItsLine().getStartY().bind(yprop);
//        }
        Cross sup=inp[0].itsLines.getStartMarker();
        inp[0].itsLines.setCrossMarkerVisible(true);
        for(int i=1;i<inp.length;i++){
            inp[i].itsLines.getStartMarker().bindToCross(sup);
            inp[i].itsLines.setCrossMarkerVisible(false);
        }
    }
    
//    public class LineExt{            
//        //public WireContact superClass;
//        private List<Line> lines;
//        boolean flag=true;
//        ChangeListener propListen;
//
//        LineExt(){
//            this.propListen = new ChangeListener<Double>(){
//                @Override
//                public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
//                    Draw();
//                }
//            };
//            lines=new ArrayList();
//            for(int i=0;i<3;i++){
//                this.lines.add(new Line(0,0,0,0));                   
//            }
//            this.lines.get(2).endXProperty().bind(endXProp);
//            this.lines.get(2).endYProperty().bind(endYProp);
//            this.lines.get(0).startXProperty().bind(startXProp);
//            this.lines.get(0).startYProperty().bind(startYProp);    
//            this.flag=false;
//            this.lines.forEach(line->{
//                line.setStrokeWidth(2);
//                line.setStyle("-fx-stroke: red; -fx-stroke-dash-array: 10 5");
//                line.setCursor(Cursor.HAND);
//                
//
//                EventHandler linePressed=(EventHandler<MouseEvent>)(MouseEvent me)->{
//                    if(me.getButton()==MouseButton.PRIMARY){
//                        line.requestFocus();
//                    }
//                    me.consume();
//                };
//                line.focusedProperty().addListener((obs,oldVal,newVal)->{
////                                            for(Line lin:lines){
////                            lin.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
////                        }
//                if(newVal)
//                        line.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
//                    else
//                        line.setEffect(null);
//                });
//                line.setOnMousePressed(linePressed);
//                line.addEventHandler(MouseDragEvent.DRAG_DETECTED,lineDragDetect);
//                line.setOnMouseReleased(e->{
//                    e.consume();
//                });
//                line.setOnKeyReleased(k->{
//                    if(k.getCode()==KeyCode.DELETE){
//                        delete();
//                    }
//                });
//            });
//            RaschetKz.drawBoard.getChildren().addAll(lines);
//        }
//
////            LineExt(Cross newCr,Cross oldCr){
////                this();
////                this.setAnchor(oldCr.getAnchor());
////                //???????????????????????????????????
////            }
//
//        LineExt(WireConnect owner,Point2D point){
//            this();
//            point=RaschetKz.drawBoard.sceneToLocal(point);
//            //this.superClass=owner;
//            anchor=new Cross(owner,point.getX(),point.getY());
//            this.lines.get(0).startXProperty().bind(anchor.centerXProperty());
//            this.lines.get(0).startYProperty().bind(anchor.centerYProperty());
//            this.lines.get(2).endYProperty().addListener(propListen);
//            this.lines.get(2).endXProperty().addListener(propListen);  
//            this.lines.get(0).startYProperty().addListener(propListen);
//            this.lines.get(0).startXProperty().addListener(propListen);
//            this.Draw();
//        }
//
//        LineExt(WireConnect owner,double x,double y){
//            this();
//            //this.superClass=owner;
//            anchor=new Cross(owner,x,y);
//            this.lines.get(0).startXProperty().bind(anchor.centerXProperty());
//            this.lines.get(0).startYProperty().bind(anchor.centerYProperty());
//            this.lines.get(2).endYProperty().addListener(propListen);
//            this.lines.get(2).endXProperty().addListener(propListen);  
//            this.lines.get(0).startYProperty().addListener(propListen);
//            this.lines.get(0).startXProperty().addListener(propListen);  
//            this.Draw();
//        }
//
////            public WireContact getOwner(){
////                return(superClass);
////            }
//
//        final void Draw(){
//            lines.get(2).setStartY(endYProp.get());
//            lines.get(2).setStartX((endXProp.get()+anchor.centerXProperty().get())/2);
//            lines.get(1).setEndX(lines.get(2).getStartX());
//            lines.get(1).setEndY(lines.get(2).getStartY());
//            lines.get(1).setStartX(lines.get(1).getEndX());
//            lines.get(1).setStartY(anchor.centerYProperty().get());
//            lines.get(0).setEndX(lines.get(1).getStartX());
//            lines.get(0).setEndY(lines.get(1).getStartY());
//        }
//
////            void unAdjustCrosses(Cross... inp){
////                DoubleProperty xprop=inp[0].centerXProperty();
////                DoubleProperty yprop=inp[0].centerYProperty();
////                for(int i=1;i<inp.length;i++){
////                    inp[i].centerXProperty().unbindBidirectional(xprop);
////                    inp[i].centerYProperty().unbindBidirectional(yprop);
////                }
////            }
//
//        void activate(){
//            lines.forEach(line->{
//                line.setStyle("-fx-stroke: black");
//            });
//            this.flag=true;
//            //this.superClass.isActive=true;
//        }
//
//        void diactivate(){
//            lines.forEach(line->{
//                line.setStyle("-fx-stroke: red; -fx-stroke-dash-array: 10 5");
//            });
//            this.flag=false;
//            //this.superClass.isActive=false;
//        }
//        
//        List<Line> getLines(){
//            return this.lines;
//        }
//    }

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
    
}