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
public class ShortCircuit extends ShemeElement {
    Parameter t;
    public ShortCircuit(){
        super();
        addElemCont(new ElemPin(this, 12, 5));
        addElemCont(new ElemPin(this, 12, 60));
        t=new Parameter("Время КЗ, с", 1.0);
        this.parameters.add(t);
        name="Короткое замыкание";
    }
    
    public ShortCircuit(boolean Catalog){
        super(Catalog);
        name="Короткое замыкание";
    }

    @Override
    public String[] getStringFunction() {
        String tt=this.t.getStringValue();
        String[] str={"p.1=i.1*if(gr(time,"+tt+"),0.001,10000000)+p.2",
            "i.1+i.2=0"};
        return str;
    }
}
