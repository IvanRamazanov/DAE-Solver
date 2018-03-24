/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Electric.Basic.Resistor;

import ElementBase.ElectricPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;

/**
 *
 * @author Ivan
 */
public class Resistor extends SchemeElement {

    public Resistor(Subsystem sys){
        super(sys);
        addElectricCont(new ElectricPin(this, 12, 5));
        addElectricCont(new ElectricPin(this, 12, 60));
    }

    public Resistor(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String R=this.parameters.get(0).toString();
        String[] str={
                "i.2=(p.2-p.1)/"+R,
                "i.1+i.2=0"};
        return str;
    }

    @Override
    protected void setParams(){
        this.parameters.add(new ScalarParameter("Resistance", 10.0));
        setName("Resistance");
    }

    @Override
    protected String getDescription(){
        return "This block represents a resistance.";
    }
}

