/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements;

import static java.lang.StrictMath.PI;
import ElementBase.ElemPin;
import ElementBase.SchemeElement;

/**
 *
 * @author Ivan
 */
public class ThreePhaseVoltageSource extends SchemeElement {

    public ThreePhaseVoltageSource(){
        super();
//        Dymamic=false;
        addElemCont(new ElemPin(this, 7, 4));//A
        addElemCont(new ElemPin(this, 26, 4));//B
        addElemCont(new ElemPin(this, 43, 4));//C
        addElemCont(new ElemPin(this, 26, 66));//N
    }

    public ThreePhaseVoltageSource(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String A=this.parameters.get(0).getStringValue();
        String fq=this.parameters.get(1).getStringValue();
        String phi=this.parameters.get(2).getStringValue();
        String[] str={  "p.1="+A+"*"+"sin("+(2*PI)+"*"+fq+"*time+"+phi+")+p.4",
                "p.2="+A+"*"+"sin("+(2*PI)+"*"+fq+"*time+"+phi+"-"+(2*PI/3)+")+p.4",
                "p.3="+A+"*"+"sin("+(2*PI)+"*"+fq+"*time+"+phi+"+"+(2*PI/3)+")+p.4","i.1+i.2+i.3+i.4=0"};
        return str;
    }

    @Override
    protected void setParams(){
        this.parameters.add(new Parameter("Amplitude", 10.0));
        this.parameters.add(new Parameter("Frequency", 50.0));
        this.parameters.add(new Parameter("Phase", 0.0));
        setName("Three-phase voltage\nsource");
    }

    @Override
    protected String getDescription(){
        return "This block represents a three-phase voltage source.";
    }
}

