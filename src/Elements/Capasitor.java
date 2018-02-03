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
public class Capasitor extends ShemeElement {
    public Capasitor(){
        super();
        addElemCont(new ElemPin(this, 12, 5));
        addElemCont(new ElemPin(this, 12, 60));
        
        this.parameters.add(new Parameter("Емкость", 0.01));
        this.parameters.add(new Parameter("Паразитное сопротивление", 1e-7));
        this.initials.add(new InitParam("Напряжение", 0));
        name="Емкость";
    }
    public Capasitor(boolean Catalog){
        super(Catalog);
        this.parameters.add(new Parameter("Емкость", 0.01));
        this.parameters.add(new Parameter("Паразитное сопротивление", 1e-7));
        this.initials.add(new InitParam("Напряжение", 0));
        name="Емкость";
    }

    @Override
    public String[] getStringFunction() {
        String C=this.parameters.get(0).getStringValue(),
                Rp=this.parameters.get(1).getStringValue();
//        String[] str={"p.1=X.1+p.2","p.2=p.1-X.1",
//            "i.1=d.X.1*"+C,"d.X.1=i.1/"+C,"i.1+i.2=0"
//        };
        String[] str={"i.1=d.X.1*"+C,"X.1=p.1-p.2", // X.1 - charge
            "i.1+i.2=0"
        };
        return str;
    }
}
