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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Shape;

/**
 *
 * @author Ivan
 */
public class Pin {
    private Shape view;
    private SimpleDoubleProperty bindX;
    private SimpleDoubleProperty bindY;
    private LineMarker itsConnection;
    private Element owner;



//    /**
//     * Удаляет следы
//     */
//    void clear(){
////        this.owner=null;
//        if(itsConnection!=null)
//            itsConnection.setIsPlugged(false);
//    }

    /**
     * Удаляет следы
     */
    public void clear(){
        this.owner=null;
        if(itsConnection!=null){
            itsConnection.unPlug(); //??????????
            itsConnection.setIsPlugged(false);
        }
        this.getView().setOpacity(1);
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
    }
}
