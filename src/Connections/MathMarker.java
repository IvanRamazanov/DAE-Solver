/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connections;

import ElementBase.MathInPin;
import ElementBase.MathPin;
import ElementBase.MathOutPin;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 *
 * @author Ivan
 */
public class MathMarker extends LineMarker{
    List<MathMarker> subContacts;
    public static MathMarker activeMathConnect;
    private static MathMarker majorConnect;
    private MathOutPin source;
    private MathInPin destin;
    private Polygon startView;
    private static Shape dragSource;
    
    MathMarker(){
        super();
        subContacts=new ArrayList();
        view=new Polygon(0,0,0,8.0,6.0,6.0/2.0);
        view.setTranslateX(-2.0);
        view.setTranslateY(-3.0);
        view.layoutXProperty().bind(bindX);
        view.layoutYProperty().bind(bindY);
        raschetkz.RaschetKz.drawBoard.getChildren().add(view);
        startView=new Polygon(0,0,0,8,6,4);
        startView.setTranslateX(-2.0);
        startView.setTranslateY(-3.0);
        this.itsLines.getStartMarker().centerXProperty().bind(startView.layoutXProperty());
        this.itsLines.getStartMarker().centerYProperty().bind(startView.layoutYProperty());
        raschetkz.RaschetKz.drawBoard.getChildren().add(startView);
        itsLines.setLineDragDetect(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent mde){
                if(mde.getButton()==MouseButton.SECONDARY){
                    MathMarker mc=new MathMarker(mde.getX(), mde.getY());
                    activeMathConnect=mc;
                    subContacts.add(mc);
                    mc.view.startFullDrag();
                }
            }
        });
        itsLines.getEndX().bind(bindX);
        itsLines.getEndY().bind(bindY);
        
        startView.setOnMouseEntered(me->{
            startView.toFront();
            startView.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
            startView.setCursor(Cursor.HAND);
        });
        startView.setOnMouseExited(me->{
            startView.setEffect(null);
            startView.setCursor(Cursor.DEFAULT);
            dragSource=null;
        });
        startView.setOnDragDetected(me->{
            activeMathConnect=this;
            this.pushToBack();
            startView.startFullDrag();
            startView.addEventFilter(MouseEvent.MOUSE_DRAGGED, Connections.LineMarker.MC_MOUSE_DRAG);
            startView.addEventFilter(MouseDragEvent.MOUSE_RELEASED, Connections.LineMarker.MC_MOUSE_RELEAS);
        });
        
        view.setOnMouseEntered(me->{
            view.toFront();
            view.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
            view.setCursor(Cursor.HAND);
        });
        view.setOnMouseExited(me->{
            view.setEffect(null);
            view.setCursor(Cursor.DEFAULT);
        });
        view.setOnDragDetected(me->{
            activeMathConnect=this;
            dragSource=view;
            this.pushToBack();
            view.startFullDrag();
            view.addEventFilter(MouseEvent.MOUSE_DRAGGED, Connections.LineMarker.MC_MOUSE_DRAG);
            view.addEventFilter(MouseDragEvent.MOUSE_RELEASED, Connections.LineMarker.MC_MOUSE_RELEAS);
        });
        
    }
    
    public MathMarker(MathPin inp){
        this();
        switch(inp.getType()){
            case 'i':
                bindX.bind(inp.getArrowX());
                bindY.bind(inp.getArrowY());
                startView.setLayoutX(inp.getArrowX().get());
                startView.setLayoutY(inp.getArrowY().get());
//                this.
                pushToBack();
                destin=(MathInPin)inp;
                break;
            case 'o':
                pushToBack();
//                itsLines.bindCross(inp.getArrowX(), inp.getArrowY());
                bindX.set(inp.getArrowX().get());
                bindY.set(inp.getArrowY().get());
                startView.layoutXProperty().bind(inp.getArrowX());
                startView.layoutYProperty().bind(inp.getArrowY());
                source=(MathOutPin)inp;
                inp.hide();
        }
        
        majorConnect=this;
    }
    
    public MathMarker(MathOutPin start,MathInPin end){
        this();
        destin=end;
        source=start;
        bindX.bind(end.getArrowX());
        bindY.bind(end.getArrowY());
        itsLines.bindStart(start.getArrowX(), start.getArrowY());
        isPlugged.set(true);
//        destin.hide();
        source.hide();
        end.setMathConnLink(this);
        start.setMathConnLink(this);
        majorConnect=this;
    }
    
    public MathMarker(double x,double y){
        this();
        source=majorConnect.getSource();
        bindX.set(x);
        bindY.set(y);
        itsLines.bindStart(majorConnect.getItsLine().getStartMarker());
    }
    
    public List<Double> getValue(){
        if(getSource()!=null){
            return getSource().getValue();
        }else{
            List<Double> out=new ArrayList();
            out.add(0.0);
            return out;
        }
    }
    
    @Override
    public void delete() {
        throw new UnsupportedOperationException("deleting wir Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void unPlug(MathPin caller){
        activeMathConnect=this;
        setIsPlugged(false);
        if(caller instanceof MathInPin){
            destin=null;
            caller.clearPin();
            if(source!=null)
                source.setSource(null);
            bindX.unbind();
            bindY.unbind();
            view.setVisible(true);
            view.toBack();
        }else{
            source=null;
            if(destin!=null)
                destin.setSource(null);
            caller.clearPin();
            startView.layoutXProperty().unbind();
            startView.layoutYProperty().unbind();
            startView.setVisible(true);
            startView.toBack();
        }
        caller.show();
    }
    
    @Override
    protected void activate(){
        getItsLine().activate();
        this.view.setVisible(false);
        this.startView.setVisible(false);
    }
    
    @Override
    protected void diactivate(){
        getItsLine().diactivate();
//        super.diactivate();
//        this.startView.setVisible(false);
    }
    
    public void plug(MathPin cont){
        switch(cont.getType()){
            case 'i':
                if(getDestin()==null){
                    setDestin((MathInPin)cont);
                    bindX.bind(cont.getArrowX());
                    bindY.bind(cont.getArrowY());
                    isPlugged.set(true);
                }
                break;
            case 'o':
                if(getSource()==null){
                    setSource((MathOutPin)cont);
                    startView.layoutXProperty().bind(cont.getArrowX());
                    startView.layoutYProperty().bind(cont.getArrowY());
                    isPlugged.set(true);
                    cont.hide();
                }
        }
        
    }

    /**
     * @param source the source to set
     */
    public void setSource(MathOutPin source) {
        this.source = source;
        this.destin.setSource(source);
        this.source.setSource(destin);
    }

    /**
     * @return the source
     */
    public MathOutPin getSource() {
        return source;
    }
    
    public void eraseDragSource(){
        dragSource=null;
    }

    /**
     * @return the destin
     */
    public MathInPin getDestin() {
        return destin;
    }

    /**
     * @param destin the destin to set
     */
    public void setDestin(MathInPin destin) {
        this.destin = destin;
        destin.setSource(this.source);
        this.source.setSource(destin);
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
//        if(itsLines.getEndX().isBound()){
//            itsLines.getStartX().set(a.getX());
//            itsLines.getStartY().set(a.getY());
//        }else{
//            itsLines.getEndX().set(a.getX());
//            itsLines.getEndY().set(a.getY());
//        }
        if(dragSource==null)
            if(!bindX.isBound()){
                bindX.set(a.getX());
                bindY.set(a.getY());
            }else{
                startView.setLayoutX(a.getX());
                startView.setLayoutY(a.getY());
            }
        else
            if(dragSource==view){
                bindX.set(a.getX());
                bindY.set(a.getY());
            }else{
                startView.setLayoutX(a.getX());
                startView.setLayoutY(a.getY());
            }
    }
    
    @Override
    public void pushToBack() {
        super.pushToBack();
        startView.toBack();
    }
}
