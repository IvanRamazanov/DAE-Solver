/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connections;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

/**
 *
 * @author Ivan
 */
public class Cross extends Circle{
    private double initX,initY,anchX,anchY;
    private ConnectLine owner;
    
//    private SimpleDoubleProperty bindX=new SimpleDoubleProperty(),bindY=new SimpleDoubleProperty();
    
    Cross(ConnectLine owner){
        this.setOnMousePressed((MouseEvent me)->{
            initX=this.getCenterX();
            initY=this.getCenterY();
            anchX=me.getX();
            anchY=me.getY();
            me.consume();
        });
        this.setOnMouseDragged((MouseEvent me)->{
            this.setCenterX(initX+me.getX()-anchX);
            this.setCenterY(initY+me.getY()-anchY);
            me.consume();
        });
        this.setVisible(false);
        this.setRadius(3);
        raschetkz.RaschetKz.drawBoard.getChildren().add(this);
        this.owner=owner;
    }

    Cross(ConnectLine owner,double cX,double cY){
        this(owner);
        this.setCenterX(cX);
        this.setCenterY(cY);
    }
    
    /**
     * Binds cX,cY to this cross
     * @param cX
     * @param cY 
     */
    public void bind(SimpleDoubleProperty cX,SimpleDoubleProperty cY){
        cX.bind(centerXProperty());
        cY.bind(centerYProperty());
        
//        if(bindSource!=null){
//            bindSource.getBindMinors().remove(this);
//            bindSource=null;
//        }
    }
    
    public void unbind(){
        centerXProperty().unbind();
        this.centerYProperty().unbind();
    };
    
    public void bindToCross(Cross superCross){
        centerXProperty().bind(superCross.centerXProperty());
        centerYProperty().bind(superCross.centerYProperty());
        setVisible(false);
    }

    /**
     * @return the ownerLineMarker
     */
    public ConnectLine getOwner() {
        return owner;
    }
    
    public void delete(){
        unbind();
        raschetkz.RaschetKz.drawBoard.getChildren().remove(this);
        owner=null;
    }
}
