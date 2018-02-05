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
public class ControlledVoltage extends ShemeElement{
    public ControlledVoltage(){
        super();
        addElemCont(new ElemPin(this, 12, 5));
        addElemCont(new ElemPin(this, 12, 60));
        addMathContact('i');
    }
    
    public ControlledVoltage(boolean catalog){
        super(catalog);
    }
    

    @Override
    public String[] getStringFunction() {
        String[] str={"p.1-p.2=I.1","i.1+i.2=0"};
        return str;
    }
    
    @Override
    protected void setParams(){
        setName("Depended voltage\nsource");
    }
}
