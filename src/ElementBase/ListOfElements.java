/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Elements.Math.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Button;
import Elements.*;
import javafx.scene.layout.TilePane;


/**
 *
 * @author Ivan
 */
public class ListOfElements {
    TilePane elemLayout;
    List<Categorie> list=new ArrayList<>();
    
    public ListOfElements(){
        list.add(new Categorie("Базовые элементы"));
        list.add(new Categorie("Источники"));
        list.add(new Categorie("Специальные элементы"));
        list.add(new Categorie("Semiconductor"));
        list.add(new Categorie("Электропривода"));
        list.add(new Categorie("Измерения"));
        list.add(new Categorie("Simulink"));
    }
    public List<Categorie> getCategories() {
        return(list);
    }

    public void setElemPane(TilePane elems) {
        this.elemLayout=elems;
    }
    
    private void addToCategorie(String type,Categorie cat){
        if("Базовые элементы".equals(type)){
            cat.elements.add(new Resistor(true));
            cat.elements.add(new Inductance(true));
            cat.elements.add(new Capasitor(true));
            cat.elements.add(new ElectricalRefference(true));
        }
        if("Источники".equals(type)){
            cat.elements.add(new VoltageSourse(true));
            cat.elements.add(new VariableVoltage(true));
            cat.elements.add(new ThreePhaseVoltageSourse(true));
            cat.elements.add(new ControlledVoltage(true));
            cat.elements.add(new CurrentSource(true));
        }
        if("Специальные элементы".equals(type)){
            cat.elements.add(new ShortCircuit(true));
            cat.elements.add(new IdealKey(true));
            cat.elements.add(new CircuitBreaker(true));
        }
        if("Электропривода".equals(type)){
            cat.elements.add(new DPTPM(true));
            cat.elements.add(new DPTnV(true));
            cat.elements.add(new InductionMotor(true));
            cat.elements.add(new SDPM(true));
        }
        if("Измерения".equals(type)){
            cat.elements.add(new Voltmeter(true));
            cat.elements.add(new Ampermeter(true));
        }
        if("Simulink".equals(type)){
            cat.elements.add(new Scope(true));
            cat.elements.add(new Gain(true));
            cat.elements.add(new Sinus(true));
            cat.elements.add(new Ramp(true));
            cat.elements.add(new Sarturation(true));
            cat.elements.add(new Integrator(true));
            cat.elements.add(new Constant(true));
            cat.elements.add(new Step(true));
            cat.elements.add(new Sum(true));
            cat.elements.add(new XYGraph(true));
            cat.elements.add(new Mux(true));
            cat.elements.add(new KZsensor(true));
            cat.elements.add(new SimulationTime(true));
        }
        if("Semiconductor".equals(type)){
            cat.elements.add(new Diode(true));
            cat.elements.add(new NPNtrans(true));
            cat.elements.add(new PNPtrans(true));
        }
    }
    
    class Categorie extends Button{
        String type;
        List<Element> elements=new ArrayList<>();
        
        Categorie(String name){
            this.type=name;
            this.setText(name);
            this.setPrefSize(100, 30);
            this.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
            addToCategorie(name,this);
//            this.setOnAction((ActionEvent ae)->{
//                elemLayout.getChildren().clear();
//                elements.forEach(data -> {
//                    elemLayout.getChildren().add(data.getView()); 
//                });
//            });
            this.focusedProperty().addListener((asd,old,newval)->{
                elemLayout.getChildren().clear();
                elements.forEach(data -> {
                    elemLayout.getChildren().add(data.getView()); 
                });
            });
                
        }
            
    }
}
