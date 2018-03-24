/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Electric.Measurements.Voltmeter;

import ElementBase.ElectricPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;

/**
 *
 * @author Ivan
 */
public class Voltmeter extends SchemeElement {
    public Voltmeter(Subsystem sys){
        super(sys);
        addElectricCont(new ElectricPin(this, 12, 4));
        addElectricCont(new ElectricPin(this, 12, 60));
        addMathContact('o');
    }
    public Voltmeter(boolean Catalog){
        super(Catalog);

    }

    @Override
    public String[] getStringFunction() {
        String[] str={"i.1=0","O.1=p.1-p.2","i.1+i.2=0"};
        return str;
    }

    @Override
    protected void setParams(){
        setName("Voltmeter");
    }

    @Override
    protected String getDescription(){
        return "This block represents a voltmeter.\n" +
                "Output in volts.";
    }
}
