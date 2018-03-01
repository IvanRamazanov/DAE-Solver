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
package Connections;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
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
import javafx.scene.transform.Transform;

/**
 *
 * @author Ivan
 */
public abstract class Wire{
    private String wireColor;
    private List<CrossToCrossLine> ContContList =new ArrayList<>();
    private List<List<Cross>> dotList=new ArrayList<>();

    public List<CrossToCrossLine> getContContList() {
        return ContContList;
    }

    public void setContContList(List<CrossToCrossLine> contContList) {
        ContContList = contContList;
    }

    public List<List<Cross>> getDotList() {
        return dotList;
    }

    public void setDotList(List<List<Cross>> dotList) {
        this.dotList = dotList;
    }

    /**
     *
     * @param wc1 Strat marker of first WireMarker
     * @param wc2 Strat marker of second WireMarker
     */
    protected void addContToCont(Cross wc1,Cross wc2){
        CrossToCrossLine contToContLine = new CrossToCrossLine(this, wc1, wc2);
        contToContLine.setColor(getWireColor());
        contToContLine.activate();
        getContContList().add(contToContLine);
    }

    protected CrossToCrossLine addContToCont(double sx,double sy,double ex,double ey){
        CrossToCrossLine contToContLine = new CrossToCrossLine(this,sx,sy,ex,ey);
        contToContLine.setColor(getWireColor());
        contToContLine.activate();
        getContContList().add(contToContLine);
        return contToContLine;
    }

    /**
     * Binds all crosses in dotList to first Cross in each line
     */
    protected void bindCrosses(){
        for(List<Cross> line:getDotList()){
            Cross major=line.get(0);
            major.setVisible(true);
            for(int i=1;i<line.size();i++){
                line.get(i).bindToCross(major);
            }
        }
    }

    protected String getWireColor(){
        return wireColor;
    }

    protected void setWireColor(String color){
        wireColor=color;
    }

    abstract public void delete();

}

