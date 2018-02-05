/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements;

import static java.lang.StrictMath.PI;
import ElementBase.ElemPin;
import ElementBase.ShemeElement;

/**
 *
 * @author Ivan
 */
public class VariableVoltage extends ShemeElement {
    public VariableVoltage(){
        super();
//        Dymamic=false;
        addElemCont(new ElemPin(this, 12, 5));
        addElemCont(new ElemPin(this, 12, 60));
    }
    
    public VariableVoltage(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String A=this.parameters.get(0).getStringValue();
        String fq=this.parameters.get(1).getStringValue();
        String phi=this.parameters.get(2).getStringValue();
        String[] str={"p.1-p.2="+A+"*"+"sin("+(2*PI)+"*"+fq+"*time+"+phi+")","i.1+i.2=0"};
        return str;
    }
    
    @Override
    protected void setParams(){
        this.parameters.add(new Parameter("Amplitude", 10.0));
        this.parameters.add(new Parameter("Frequency", 50.0));
        this.parameters.add(new Parameter("Phase", 0.0));
        setName("AC voltage source");
    }
}
