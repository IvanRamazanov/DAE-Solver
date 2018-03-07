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
public class Inductance extends SchemeElement {

    public Inductance(){
        super();
        addElemCont(new ElemPin(this, 12, 5));
        addElemCont(new ElemPin(this, 12, 60));
    }
    public Inductance(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String L=this.parameters.get(0).toString();

        String[] str={  "p.1-p.2=d.X.1*"+L,
                "X.1=i.1",
                "i.1+i.2=0"};
        return str;
    }

    @Override
    protected void setParams(){
        this.parameters.add(new ScalarParameter("Inductance", 0.01));

        this.initials.add(new InitParam("Current", 0));
        setName("Inductance");
    }

    @Override
    protected String getDescription(){
        return "This block represents an inductivity.";
    }
}

