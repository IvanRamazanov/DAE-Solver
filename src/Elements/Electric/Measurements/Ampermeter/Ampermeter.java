/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Electric.Measurements.Ampermeter;

import ElementBase.ElectricPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;

/**
 *
 * @author Ivan
 */
public class Ampermeter extends SchemeElement {
    public Ampermeter(Subsystem sys){
        super(sys);
        addElectricCont(new ElectricPin(this, 12, 4));
        addElectricCont(new ElectricPin(this, 12, 60));
        addMathContact('o');
    }
    public Ampermeter(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String[] str={"p.1-p.2=0","O.1=i.1","i.1+i.2=0"};
        return str;
    }

    @Override
    protected void setParams() {
        setName("Ampermeter");
    }

    @Override
    protected String getDescription(){
        return "This block represents an ampermeter.\n" +
                "Output: current in amperes";
    }
}

