/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements;

import ElementBase.ElectricPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem;

/**
 *
 * @author Ivan
 */
public class VoltageSource extends SchemeElement {
    public VoltageSource(Subsystem sys){
        super(sys);
        addElemCont(new ElectricPin(this, 12, 5));
        addElemCont(new ElectricPin(this, 12, 60));
    }

    public VoltageSource(boolean catalog){
        super(catalog);
    }

//    @Override
//    public void catElemCreation() {
//        raschetkz.RaschetKz.ElementList.add(new VoltageSourse());
//    }

//    @Override
//    public List<SchemeElement> expandElement(List<Wire> nodes, boolean byGOST) {
//        // add sub elems to "elements" and subbr to "nodes"
//        List<SchemeElement> output=new ArrayList();
//        output.add(this);
//        return output;
//    }

    @Override
    public String[] getStringFunction() {
        String A=this.parameters.get(0).toString();

        String[] str={
                "p.1-p.2="+A,
                "i.1+i.2=0"
        };
        return str;
    }

    @Override
    protected void setParams(){
        this.parameters.add(new ScalarParameter("Voltage", 15.0));
        setName("DC voltage source");
    }

    @Override
    protected String getDescription(){
        return "This block represents a constant voltage source.";
    }
}

