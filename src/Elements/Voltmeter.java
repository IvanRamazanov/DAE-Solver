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
public class Voltmeter extends ShemeElement{
    public Voltmeter(){
        super();
        addElemCont(new ElemPin(this, 12, 4));
        addElemCont(new ElemPin(this, 12, 60));
        addMathContact('o');
        this.parameters.add(new Parameter("Множитель", 1.0));
        name="Вольтметер";
    }
    public Voltmeter(boolean Catalog){
        super(Catalog);
        name="Вольтметер";
    }

    @Override
    public String[] getStringFunction() {
        String[] str={"i.1=0","O.1=p.1-p.2","i.1+i.2=0"};
        return str;
    }
}
