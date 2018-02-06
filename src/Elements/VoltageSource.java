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
public class VoltageSource extends ShemeElement {
    public VoltageSource(){
        super();
        addElemCont(new ElemPin(this, 12, 5));
        addElemCont(new ElemPin(this, 12, 60));
    }

    public VoltageSource(boolean catalog){
        super(catalog);
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

    @Override
    protected void setParams(){
        this.parameters.add(new Parameter("Voltage", 15.0));
        setName("DC voltage source");
    }
}

