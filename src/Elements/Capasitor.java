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
    }

    public Capasitor(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String C=this.parameters.get(0).getStringValue(),
                Rp=this.parameters.get(1).getStringValue();
        String[] str={"i.1=d.X.1*"+C,"X.1=p.1-p.2",
                "i.1+i.2=0"
        };
        return str;
    }

    @Override
    protected void setParams(){
        this.parameters.add(new Parameter("Capasitance", 0.01));
        this.initials.add(new InitParam("Voltage", 0));
        setName("Capacitor");
    }
}

