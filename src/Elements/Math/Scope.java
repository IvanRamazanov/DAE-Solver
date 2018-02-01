/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Math;

import ElementBase.OutputElement;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    
    public Scope(){
        super();
//        data.add(new ArrayList());
        addMathContact('i');
        MenuItem export=new MenuItem("Export");
        export.setOnAction(e->{
            FileChooser filechoose=new FileChooser();
            filechoose.getExtensionFilters().add(new FileChooser.ExtensionFilter("text", "*.txt"));
            filechoose.setTitle("Сохранить как...");
            File file=filechoose.showSaveDialog(null);
            if(file!=null){
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    for(int i=0;i<data.get(0).size();i++){
                        bw.write(time.get(i).toString()+" "+data.get(0).get(i).toString());
                        bw.newLine();
                    }
                }catch(IOException ea){}
            }
        });
        cm.getItems().add(export);
        name="Скопе";
    }
    public Scope(boolean catalog){
        super(catalog);
        name="Скопе";
    }
    
    void plot(){
        if(plotStage==null){
            plotStage=new Stage();
            plotStage.setOnCloseRequest(e->{plotStage=null;});
        }
        plotStage.show();
        plotStage.toFront();
        //if(!data.isEmpty())
            plotter.plot(plotStage, data, time, minVal, maxVal);
    }
    
    @Override
    public void init(){
        maxVal=-1*Double.MAX_VALUE;
        minVal=Double.MAX_VALUE;
        time.clear();
//        for(List<Double> l:data){
//            l.clear();
//        }
        List<Double> val=getInputs().get(0).getValue();
        data.clear();
        for(Double d:val){
            data.add(new ArrayList());
        }
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
    
    
}
