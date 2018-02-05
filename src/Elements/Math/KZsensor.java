/*
 * The MIT License
 *
 * Copyright 2017 Иван.
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
package Elements.Math;

import ElementBase.OutputElement;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author Иван
 */
public class KZsensor extends OutputElement{
    Double maxIVal,maxItime;
    public KZsensor(){
        super();
        addMathContact('i');
    }
    public KZsensor(boolean catalog){
        super(catalog);
    }

    @Override
    public void init() {
        maxIVal=Double.MIN_VALUE;
    }

    @Override
    public void updateData(double t) {
        List<Double> val=getInputs().get(0).getValue();
        if(val.get(0)>maxIVal)  {
            maxIVal=val.get(0);
            maxItime=t;
        }
    }
    
    @Override
    protected void openDialogStage(){
        Stage plotStage=new Stage();
        GridPane root=new GridPane();
        root.setHgap(5);
        root.setVgap(5);
        Scene plotScene=new Scene(root,300,250);
        plotScene.getStylesheets().add("raschetkz/mod.css");
        plotStage.setScene(plotScene);
        
        if(maxItime!=null){
            Label txt=new Label("Максимальное значение тока (А):");
            root.add(txt, 0, 0);
            txt=new Label(maxIVal.toString());
            root.add(txt, 1, 0);
            txt=new Label("В момент времени (сек):");
            root.add(txt, 0, 1);
            txt=new Label(maxItime.toString());
            root.add(txt, 1, 1);
        }else{
            Label txt=new Label("No results. Start simulation");
            root.add(txt, 0, 0);
        }
        plotStage.show();
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected void setParams(){
        setName("Current peak\nanalizer");
    }
}
