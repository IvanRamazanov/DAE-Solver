/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements;

import ElementBase.ElemPin;
import ElementBase.SchemeElement;

/**
 *
 * @author Ivan
 */
public class ShortCircuit extends SchemeElement {
    private Parameter t,ron;
    public ShortCircuit(){
        super();
        addElemCont(new ElemPin(this, 12, 5));
        addElemCont(new ElemPin(this, 12, 60));
    }

    public ShortCircuit(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String tt=this.t.getStringValue(),rron=ron.getStringValue();
        return new String[]{"p.1=i.1*if(gr(time," + tt + "),"+rron+",10000000)+p.2",
                "i.1+i.2=0"};
    }

    @Override
    protected void setParams(){
        t=new Parameter("Trigger time, sec", 1.0);
        this.parameters.add(t);
        ron=new Parameter("ON resistance, ohm", 0.001);
        setName("Short circuit");
    }

    @Override
    protected String getDescription(){
        return "This block represents a short circuit switch.\n" +
                "At given point of time, this block will have Ron resistance.";
    }
}

