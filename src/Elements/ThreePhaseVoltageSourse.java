/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements;

import static java.lang.StrictMath.PI;
import ElementBase.ElemPin;
import ElementBase.ShemeElement;

/**
 *
 * @author Иван
 */
public class ThreePhaseVoltageSourse extends ShemeElement {
    
    public ThreePhaseVoltageSourse(){
        super();
//        Dymamic=false;     
        addElemCont(new ElemPin(this, 7, 4));//A
        addElemCont(new ElemPin(this, 26, 4));//B
        addElemCont(new ElemPin(this, 43, 4));//C
        addElemCont(new ElemPin(this, 26, 66));//N
        this.parameters.add(new Parameter("Амплитуда", 10.0));
        this.parameters.add(new Parameter("Частота", 50.0));
        this.parameters.add(new Parameter("Фаза", 0.0));
        name="Источник напряжения\nтрехфазный";
    }
    
    public ThreePhaseVoltageSourse(boolean Catalog){
        super(Catalog);
        name="Источник напряжения\nтрехфазный";
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
}
