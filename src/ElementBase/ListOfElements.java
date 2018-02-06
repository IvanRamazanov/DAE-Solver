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
        list.add(new Categorie("Basics"));
        list.add(new Categorie("Electric sources"));
        list.add(new Categorie("Special elements"));
        list.add(new Categorie("Semiconductors"));
        list.add(new Categorie("Electrical machines"));
        list.add(new Categorie("Measurments"));
        list.add(new Categorie("Maths"));
    }
    public List<Categorie> getCategories() {
        return(list);
    }

    public void setElemPane(TilePane elems) {
        this.elemLayout=elems;
    }

    private void addToCategorie(String type,Categorie cat){
        if("Basics".equals(type)){
            cat.elements.add(new Resistor(true));
            cat.elements.add(new Inductance(true));
            cat.elements.add(new Capasitor(true));
            cat.elements.add(new ElectricalRefference(true));
        }
        if("Electric sources".equals(type)){
            cat.elements.add(new VoltageSource(true));
            cat.elements.add(new VariableVoltage(true));
            cat.elements.add(new ThreePhaseVoltageSource(true));
            cat.elements.add(new ControlledVoltage(true));
            cat.elements.add(new CurrentSource(true));
        }
        if("Special elements".equals(type)){
            cat.elements.add(new ShortCircuit(true));
            cat.elements.add(new IdealKey(true));
            cat.elements.add(new CircuitBreaker(true));
        }
        if("Electrical machines".equals(type)){
            cat.elements.add(new DPTPM(true));
            cat.elements.add(new DPTnV(true));
            cat.elements.add(new InductionMotor(true));
            cat.elements.add(new SDPM(true));
        }
        if("Measurments".equals(type)){
            cat.elements.add(new Voltmeter(true));
            cat.elements.add(new Ampermeter(true));
        }
        if("Maths".equals(type)){
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
        if("Semiconductors".equals(type)){
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
            this.setPrefSize(150, 30);
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

