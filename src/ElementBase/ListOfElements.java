/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Elements.Math.*;
import java.util.ArrayList;
import java.util.List;

import Elements.Rotational.*;
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
        list.add(new Categorie("Mechanics"));
    }
    public List<Categorie> getCategories() {
        return(list);
    }

    public void setElemPane(TilePane elems) {
        this.elemLayout=elems;
    }

    private void addToCategorie(String type,Categorie cat){
        switch (type){
            case "Basics":
                cat.elements.add(new Resistor(true));
                cat.elements.add(new Inductance(true));
                cat.elements.add(new Capasitor(true));
                cat.elements.add(new ElectricalReference(true));
                break;
            case "Electric sources":
                cat.elements.add(new VoltageSource(true));
                cat.elements.add(new VariableVoltage(true));
                cat.elements.add(new ThreePhaseVoltageSource(true));
                cat.elements.add(new ControlledVoltage(true));
                cat.elements.add(new CurrentSource(true));
                break;
            case "Special elements":
                cat.elements.add(new ShortCircuit(true));
                cat.elements.add(new IdealKey(true));
                cat.elements.add(new CircuitBreaker(true));
                break;
            case "Electrical machines":
                cat.elements.add(new DPTPM(true));
                cat.elements.add(new DPTnV(true));
                cat.elements.add(new InductionMotor(true));
                cat.elements.add(new SDPM(true));
                break;
            case "Measurments":
                cat.elements.add(new Voltmeter(true));
                cat.elements.add(new Ampermeter(true));
                break;
            case "Maths":
                cat.elements.add(new Scope(true));
                cat.elements.add(new Gain(true));
                cat.elements.add(new Sinus(true));
                cat.elements.add(new Ramp(true));
                cat.elements.add(new Saturation(true));
                cat.elements.add(new Integrator(true));
                cat.elements.add(new Constant(true));
                cat.elements.add(new Step(true));
                cat.elements.add(new Sum(true));
                cat.elements.add(new XYGraph(true));
                cat.elements.add(new Mux(true));
                cat.elements.add(new KZsensor(true));
                cat.elements.add(new SimulationTime(true));
                break;
            case "Semiconductors":
                cat.elements.add(new Diode(true));
                cat.elements.add(new NPNtrans(true));
                cat.elements.add(new PNPtrans(true));
                break;
            case "Mechanics":
                cat.elements.add(new TorqueSource(true));
                cat.elements.add(new Inertia(true));
                cat.elements.add(new SpeedSensor(true));
                cat.elements.add(new RotationalFriction(true));
                break;
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

