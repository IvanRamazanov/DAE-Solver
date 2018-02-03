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
public class VoltageSourse extends ShemeElement {
    public VoltageSourse(){
        super();
        addElemCont(new ElemPin(this, 12, 5));
        addElemCont(new ElemPin(this, 12, 60));
        
        this.parameters.add(new Parameter("Напряжение", 15.0));
        this.parameters.add(new Parameter("Паразитное сопротивление", 1e-7));
        name="Источник постоянного\nнапряжения";
    }
    
    public VoltageSourse(boolean catalog){
        super(catalog);
        
        this.parameters.add(new Parameter("Напряжение", 15.0));
        this.parameters.add(new Parameter("Паразитное сопротивление", 1e-7));
        name="Источник постоянного\nнапряжения";
    }
    
//    @Override
//    public void catElemCreation() {
//        raschetkz.RaschetKz.ElementList.add(new VoltageSourse());
//    }

//    @Override
//    public List<ShemeElement> expandElement(List<Wire> nodes, boolean byGOST) {
//        // add sub elems to "elements" and subbr to "nodes"
//        List<ShemeElement> output=new ArrayList();
//        output.add(this);
//        return output;
//    }

    @Override
    public String[] getStringFunction() {
        String A=this.parameters.get(0).getStringValue(),
                Rp=this.parameters.get(0).getStringValue();
        
        String[] str={  
                    "p.1-p.2="+A,
                    "i.1+i.2=0"
                };
        return str;
    }
}
