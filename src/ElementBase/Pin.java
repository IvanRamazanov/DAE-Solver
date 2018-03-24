/*
 * The MIT License
 *
 * Copyright 2018 Ivan.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ElementBase;

import Connections.LineMarker;
import Connections.Wire;
import Elements.Environment.Subsystem.Subsystem;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;

/**
 *
 * @author Ivan
 */
public abstract class Pin {
    private Shape view;
    private SimpleDoubleProperty bindX;
    private SimpleDoubleProperty bindY;
    private LineMarker itsConnection;
    private Element owner;
    private Subsystem system;
    private EventHandler<MouseEvent> enterMouse,exitMouse,dragDetected;
    private EventHandler<MouseDragEvent> dragEnterHndl,dragExitHndl;
    private ChangeListener<Transform> transform;
    private double layoutX,layoutY;

//    protected double width=4,height=4;

    Pin(){}

    Pin(Element owner) {
        setOwner(owner);
        enterMouse = (MouseEvent me) -> {
            getView().setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
            getView().setCursor(Cursor.HAND);
        };
        exitMouse = (MouseEvent me) -> {
            getView().setEffect(null);
            getView().setCursor(Cursor.DEFAULT);
        };
        dragEnterHndl = (MouseDragEvent me) -> {
            if (Wire.activeWireConnect != null && getItsConnection() == null) {
                if (isProperInstance(Wire.activeWireConnect)) {
                    //System.out.println("Drag enter to ElectricPass. Will plug");
                    Wire.activeWireConnect.getWire().setEnd(this);
                    getView().toFront();
                }
            }

        };
        dragExitHndl = (MouseDragEvent me) -> {
            if (Wire.activeWireConnect != null)
                if (isProperInstance(Wire.activeWireConnect) &&
                        this.equals(Wire.activeWireConnect.getItsConnectedPin()) &&
                        Wire.activeWireConnect.getWire().getRank() != 1) {
                    //System.out.println("Drag exit from ElectricPass. Source: " + me.getGestureSource());
                    //if(!ElectricWire.activeWireConnect.getElemContact().equals(this))
                    switch (me.getButton()) {
                        case PRIMARY:
                            if (me.isPrimaryButtonDown()) {
//                                    this.wireCont=null;
                                Wire.activeWireConnect.unPlug();
                                getView().setOpacity(1);
                                toFront();
                            }
                            break;
                        case SECONDARY:
                            if (me.isSecondaryButtonDown()) {
//                                    this.wireCont=null;
                                Wire.activeWireConnect.unPlug();
                                getView().setOpacity(1);
                                toFront();
                            }
                            break;
                    }
                }

        };
        dragDetected = (MouseEvent me) -> {
            if (me.getButton() == MouseButton.PRIMARY) {
                if (getItsConnection() == null) {
                    //System.out.println("Drag from ElectricPass detected! ItsConnection is Null");
                    Wire w = createWire(this, me.getSceneX(), me.getSceneY());
                    //RaschetKz.wireList.add(w);
                    getView().startFullDrag();
                    w.setStaticEventFilters(getView());
                } else {
                    //System.out.println("Drag from ElectricPass detected! ItsConnection not Null");
                    LineMarker lm = getItsConnection();
                    lm.unPlug();
                    getView().startFullDrag();
                    lm.getWire().setStaticEventFilters(getView());
                }
                me.consume();
            }
        };

        transform = (aza, oldVal, newVal) -> {
            double x=(getView().getLayoutBounds().getMaxX()+getView().getLayoutBounds().getMinX())/2;
            double y=(getView().getLayoutBounds().getMaxY()+getView().getLayoutBounds().getMinY())/2;
            Point2D point = newVal.transform(x, y);
            point = getSystem().getDrawBoard().sceneToLocal(point);

            getBindX().set(point.getX());
            getBindY().set(point.getY());
        };
    }

    Pin(Element elem, double x, double y){
        this(elem);
        layoutX=x;
        layoutY=y;
    }

    /**
     * Удаляет следы
     */
    public void clear(){
        this.owner=null;
        if(itsConnection!=null){
            itsConnection.unPlug(); //??????????
            //itsConnection.setIsPlugged(false);  // TODO check this
        }
        this.getView().setOpacity(1);
    }

    public void delete(){
        clear();
        getView().localToSceneTransformProperty().removeListener(transform);
    }

    /**
     * If isReal true, add EC pointer to WC and bind CenterProp.
     * If false just bind CenterProp.
     * @param contactr wireCont
     */
    public void setWirePointer(LineMarker contactr){
        this.itsConnection=contactr;
        this.getView().setOpacity(0);
    }

    public Shape getView() {
        return view;
    }

    public void setView(Shape view) {
        this.view = view;
        getView().setLayoutX(layoutX);
        getView().setLayoutY(layoutY);
        getView().addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, dragEnterHndl);
        getView().addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED_TARGET, dragExitHndl);
        getView().addEventHandler(MouseDragEvent.DRAG_DETECTED,dragDetected);
        getView().addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
            e.consume();
        });
        getView().addEventHandler(MouseDragEvent.MOUSE_DRAGGED, e->{
            e.consume();
        });
        getView().setOnMouseEntered(enterMouse);
        getView().setOnMouseExited(exitMouse);
        //------

        setBindX(new SimpleDoubleProperty());
        setBindY(new SimpleDoubleProperty());
        getView().localToSceneTransformProperty().addListener(transform);
        getView().setFill(Paint.valueOf("#ffffff"));
        getView().setStrokeWidth(2);
        getView().setStroke(Paint.valueOf("#000000"));
        getView().setCursor(Cursor.HAND);
    }

    public SimpleDoubleProperty getBindX() {
        return bindX;
    }

    public void setBindX(SimpleDoubleProperty bindX) {
        this.bindX = bindX;
    }

    public SimpleDoubleProperty getBindY() {
        return bindY;
    }

    public void setBindY(SimpleDoubleProperty bindY) {
        this.bindY = bindY;
    }

    public LineMarker getItsConnection() {
        return itsConnection;
    }

    public void setItsConnection(LineMarker itsConnection) {
        this.itsConnection = itsConnection;
    }

    public Element getOwner() {
        return owner;
    }

    public void setOwner(Element owner) {
        this.owner = owner;
        system=owner.getItsSystem();
    }

    public void setSystem(Subsystem system) {
        this.system = system;
    }

    public void toFront(){
        getOwner().toFront();
        getView().toFront();
    }

    abstract protected boolean isProperInstance(LineMarker lm);

    abstract protected Wire createWire(Pin pin,double x,double y);

    abstract public Wire createWire(Subsystem sys,Pin pin1,Pin pin2);

    public Subsystem getSystem() {
        return system;
    }
}
