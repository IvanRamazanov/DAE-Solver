/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Connections.MathWire.MathMarker;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import raschetkz.RaschetKz;

/**
 *
 * @author Ivan
 */
public abstract class MathElement extends Element{
    protected List<MathInPin> inputs;
    protected List<MathOutPin> outputs;
    public static MathMarker itsMathConnect;
    private final double contStep=15;
    private double maxX;
    
    public MathElement(){
        super();
        inputs=new ArrayList();
        outputs=new ArrayList();
        parameters=new ArrayList();
        maxX=viewPane.getBoundsInLocal().getMaxX();
    }
    
    public MathElement(boolean catalog) {
        super(catalog);
    }
    
    @Override
    protected void openDialogStage(){
        Stage subWind=new Stage();
        BorderPane root=new BorderPane();
        Scene scene=new Scene(root,300,200,Color.DARKCYAN);
        subWind.setScene(scene);
        
        VBox top=new VBox();
        for(Parameter p:getParameters()){
            top.getChildren().add(p.layout);
        }
        Tab params=new Tab("Параметры элемента");
        params.setContent(top);
        TabPane pane=new TabPane(params);
        pane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        root.setTop(pane);

        //---buttns
        Button btn=new Button("Ок");
        btn.setOnAction((ActionEvent ae)->{
            getParameters().forEach(data->{
                data.update();
            });
            subWind.close();
        });
        HBox bot=new HBox();
        bot.setAlignment(Pos.CENTER_RIGHT);
        bot.getChildren().add(btn);
        btn=new Button("Отмена");
        btn.setOnAction((ActionEvent ae)->{
            subWind.close();
        });
        bot.getChildren().add(btn);
        root.setBottom(bot);
        subWind.show();
    }
    
    final protected void addHideMathContact(char ch){
        if(ch=='i'){
            if(inputs==null) inputs=new ArrayList();
            MathInPin ic=new MathInPin();
            getInputs().add(ic);
        }else{
            if(outputs==null) outputs=new ArrayList();
            MathOutPin oc=new MathOutPin(this);
            getOutputs().add(oc);
        }
    }
    
    @Override
    public void delete(){
        //disconnect !!!!!
        this.inputs.forEach(pin->{
            pin.getItsConnection().unPlug();
        });
        RaschetKz.MathElemList.remove(this);
        RaschetKz.drawBoard.getChildren().remove(this.viewPane);
    }
    
    /**
     * 
     * @param ch 'i' or 'o'
     */
    final protected void addMathContact(char ch){
        if(ch=='i'){
            int num=getInputs().size();
            MathInPin ic=new MathInPin(0,num*contStep+10);
            getInputs().add(ic);
            viewPane.getChildren().add(ic.view);
        }else{
            int num=getOutputs().size();
            MathOutPin oc=new MathOutPin(this,maxX,num*contStep+10);
            getOutputs().add(oc);
            viewPane.getChildren().add(oc.view);
        }
    }
    
    abstract protected List<Double> getValue(int outIndex);
    
    @Override
    protected void init(){
        // check dimensions
    };

    /**
     * @return the parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * @return the inputs
     */
    public List<MathInPin> getInputs() {
        return inputs;
    }

    /**
     * @return the outputs
     */
    public List<MathOutPin> getOutputs() {
        return outputs;
    }
}