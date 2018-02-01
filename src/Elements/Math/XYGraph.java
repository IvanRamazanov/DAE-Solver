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
import java.util.ArrayList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author Иван
 */
public class XYGraph extends OutputElement{
    public XYGraph(){
        super();
        data.add(new ArrayList());
        data.add(new ArrayList());
        addMathContact('i');
        addMathContact('i');
        name="ХУскопе";
    }
    
    public XYGraph(boolean inp){
        super(inp);
        name="ХУскопе";
    }

    @Override
    public void updateData(double t) {
//        double x=getInputs().get(0).getValue();
//        double y=getInputs().get(1).getValue();
//        data.get(0).add(x);
//        data.get(1).add(y);
    }
    
    @Override
    protected void openDialogStage(){
        plot();
    }
    
    void plot(){
        Stage plotStage=new Stage();
        BorderPane root=new BorderPane();
        Scene plotScene=new Scene(root,300,250);
        plotScene.getStylesheets().add("raschetkz/mod.css");
        
        NumberAxis x=new NumberAxis();
        x.setLabel("Время, с");
        NumberAxis y=new NumberAxis();
        LineChart<Number,Number> plot=new LineChart<>(x,y);
        root.setCenter(plot);
        XYChart.Series points=new XYChart.Series<>();
        for(int i=0;i<data.size();i++){
            points.getData().add(new XYChart.Data<>(data.get(0).get(i), data.get(1).get(i)));
        }
        
        plot.getData().add(points);
        plot.setCreateSymbols(false);
        plot.setLegendVisible(false);
        plotStage.setScene(plotScene);
        plotStage.show();
    }
    
    @Override
    public void init(){
        this.data.clear();
    }

}
