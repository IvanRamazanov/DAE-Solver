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
public class ElectricalRefference extends ShemeElement{

    public ElectricalRefference(){
        super();
        addElemCont(new ElemPin(this, 12, 5));
    }
    
    public ElectricalRefference(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String[] str={"p.1=0"};
        return str;
    }
    
    @Override
    protected void setParams(){
        setName("Electical refference");
    }
}
