/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Electric.Sources.VariableVoltage;

import ElementBase.ElectricPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;

import static java.lang.StrictMath.PI;

/**
 *
 * @author Ivan
 */
public class VariableVoltage extends SchemeElement {
    public VariableVoltage(Subsystem sys){
        super(sys);
//        Dymamic=false;
        addElectricCont(new ElectricPin(this, 12, 5));
        addElectricCont(new ElectricPin(this, 12, 60));
    }

    public VariableVoltage(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String A=this.parameters.get(0).toString();
        String fq=this.parameters.get(1).toString();
        String phi=this.parameters.get(2).toString();
        String[] str={"p.1-p.2="+A+"*"+"sin("+(2*PI)+"*"+fq+"*time+"+phi+")","i.1+i.2=0"};
        return str;
    }

    @Override
    protected void setParams(){
        this.parameters.add(new ScalarParameter("Amplitude", 10.0));
        this.parameters.add(new ScalarParameter("Frequency", 50.0));
        this.parameters.add(new ScalarParameter("Phase", 0.0));
        setName("AC voltage source");
    }

    @Override
    protected String getDescription(){
        return "This block represents a alternate voltage source.";
    }
}

