/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements;

import ElementBase.ElemPin;
import ElementBase.ShemeElement;

/**
 *
 * @author Ivan
 */
public class Resistor extends ShemeElement {
    
    public Resistor(){
        super();
        addElemCont(new ElemPin(this, 12, 5));
        addElemCont(new ElemPin(this, 12, 60));
        
        this.parameters.add(new Parameter("Сопротивление", 10.0));
        name="Резистор";
    }
    public Resistor(boolean Catalog){
        super(Catalog);
        
        this.parameters.add(new Parameter("Сопротивление", 10.0));
        name="Резистор";
    }

    @Override
    public String[] getStringFunction() {
        String R=this.parameters.get(0).getStringValue();
        String[] str={
            "i.2=(p.2-p.1)/"+R,
            "i.1+i.2=0"};
        return str;
    }
    
}
