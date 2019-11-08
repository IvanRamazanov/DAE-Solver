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
    ScalarParameter R;

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
        return new String[]{
                "i.2=(p.2-p.1)/"+R,
                "i.1+i.2=0"
        };
    }

    @Override
    protected void setParams(){
        this.parameters.add(R=new ScalarParameter("Resistance", 10.0));
        setName("Resistance");
    }

    @Override
    protected String getDescription(){
        return "This block represents a resistance.";
    }
}

