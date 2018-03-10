/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Math;

import ElementBase.MathElement;
import ElementBase.OutputElement;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Elements.Environment.Subsystem;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import raschetkz.Painter;


/**
 *
 * @author Ivan
 */
public class Scope extends OutputElement{
    private Painter plotter=new Painter();
    private double maxVal,minVal;
    private Stage plotStage;
    private List<List<Double>> data;
    private List<Double> time;

    public Scope(Subsystem sys){
        super(sys);
        addMathContact('i');

        data=new ArrayList<>();
        time=new ArrayList<>();
        MenuItem export=new MenuItem("Export");
        export.setOnAction(e->{
            FileChooser filechoose=new FileChooser();
            filechoose.getExtensionFilters().add(new FileChooser.ExtensionFilter("text", "*.txt"));
            filechoose.setTitle("Сохранить как...");
            File file=filechoose.showSaveDialog(null);
            if(file!=null){
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write("time ");
                    for(int i=0;i<data.size();i++)
                        bw.write("in"+(i+1)+" ");
                    bw.newLine();
                    for(int i=0;i<data.get(0).size();i++){
                        bw.write(time.get(i).toString()+" ");
                        for(int j=0;j<data.size();j++)
                            bw.write(data.get(j).get(i).toString()+" ");
                        bw.newLine();
                    }
                }catch(IOException ea){}
            }
        });
        cm.getItems().add(export);
    }
    public Scope(boolean catalog){
        super(catalog);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        return null;
    }

    void plot(){
        if(plotStage==null){
            plotStage=new Stage();
            plotStage.setOnCloseRequest(e->{plotStage=null;});
        }
        plotStage.show();
        plotStage.toFront();
        plotStage.setTitle(getName());
        //if(!data.isEmpty())
        plotter.plot(plotStage, data, time, minVal, maxVal);
    }

    @Override
    public void init(){
        super.init();
        maxVal=-1*Double.MAX_VALUE;
        minVal=Double.MAX_VALUE;
        time.clear();
        List<Double> val=getInputs().get(0).getValue();
        data.clear();
        int i=0;
        for(Double d:val){
            data.add(new ArrayList());
            data.get(i).add(d);
            i++;
        }
        time.add(0.0);
    }

    @Override
    public void updateData(double t){
        List<Double> val=getInputs().get(0).getValue();
        for(int i=0;i<val.size();i++){
            this.data.get(i).add(val.get(i));
            if(val.get(i)>maxVal)
                maxVal=val.get(i);
            if(val.get(i)<minVal)
                minVal=val.get(i);
        }
        this.time.add(t);
    }

    @Override
    protected void openDialogStage(){
        plot();
    }

    @Override
    protected void setParams(){
        setName("Scope");
    }

    @Override
    protected String getDescription(){
        return "";
    }
}

