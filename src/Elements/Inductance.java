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
public class Inductance extends ShemeElement{
    
    public Inductance(){
        super();
        addElemCont(new ElemPin(this, 12, 5));
        addElemCont(new ElemPin(this, 12, 60));
        
        this.parameters.add(new Parameter("Индуктивность", 0.01));
        this.parameters.add(new Parameter("Паразитное сопротивление", 1e7));
        
        this.initials.add(new InitParam("Ток", 0));
        name="Катушка";
    }
    public Inductance(boolean Catalog){
        super(Catalog);
        
        this.parameters.add(new Parameter("Индуктивность", 0.01));
        this.parameters.add(new Parameter("Паразитное сопротивление", 1e7));
        
        this.initials.add(new InitParam("Ток", 0));
        name="Катушка";
    }

    @Override
    public String[] getStringFunction() {
        String L=this.parameters.get(0).getStringValue(),
                Rp=this.parameters.get(1).getStringValue();
        
        String[] str={  "p.1-p.2=d.X.1*"+L,
                        "X.1=i.1",
                        "i.1+i.2=0"};
        return str;
    }
}
