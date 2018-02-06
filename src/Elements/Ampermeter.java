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
public class Ampermeter extends ShemeElement {
    public Ampermeter(){
        super();
        addElemCont(new ElemPin(this, 12, 4));
        addElemCont(new ElemPin(this, 12, 60));
        addMathContact('o');
    }
    public Ampermeter(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String[] str={"p.1-p.2=0","O.1=i.1","i.1+i.2=0"};
        return str;
    }

    @Override
    protected void setParams() {
        setName("Ampermeter");
    }
}

