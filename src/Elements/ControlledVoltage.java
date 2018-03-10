/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements;


import ElementBase.ElectricPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem;


/**
 *
 * @author Ivan
 */
public class ControlledVoltage extends SchemeElement {
    public ControlledVoltage(Subsystem sys){
        super(sys);
        addElemCont(new ElectricPin(this, 12, 5));
        addElemCont(new ElectricPin(this, 12, 60));
        addMathContact('i');
    }

    public ControlledVoltage(boolean catalog){
        super(catalog);
    }


    @Override
    public String[] getStringFunction() {
        String[] str={"p.1-p.2=I.1","i.1+i.2=0"};
        return str;
    }

    @Override
    protected void setParams(){
        setName("Depended voltage\nsource");
    }

    @Override
    protected String getDescription(){
        return "This block represents a controlled voltage source.\n" +
                "An output value in volts is equal to input port value.";
    }
}

