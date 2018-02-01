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
public class DPTPM extends ShemeElement{
    Parameter Rya,Lya,J0,Cm,Cw,F;
    
    public DPTPM(){
        super();
//        Dymamic=true;
        addElemCont(new ElemPin(this, 31, 4));
        addElemCont(new ElemPin(this, 31, 66));
        Rya=new Parameter("Сопротивление якоря", 0.1);
        this.parameters.add(Rya);
        Lya=new Parameter("Индуктивность якоря", 0.001);
        this.parameters.add(Lya);
        J0=new Parameter("Момент инерции приведенный к якорю", 10);
        this.parameters.add(J0); 
        Cm=new Parameter("Cm", 10);
        this.parameters.add(Cm);
        Cw=new Parameter("Cw", 10);
        this.parameters.add(Cw); 
        F=new Parameter("Ф", 1);
        this.parameters.add(F);
        this.initials.add(new InitParam("Ток", 0));
        this.initials.add(new InitParam("Скорость", 0));
        addMathContact('i');
        addMathContact('o');
        name="ДПТПМ";
    }
    
    public DPTPM(boolean Catalog){
        super(Catalog);
        name="ДПТПМ";
    }

    @Override
    public String[] getStringFunction() {
        String rya=Rya.getStringValue(),
                lya=Lya.getStringValue(),
                J=J0.getStringValue(),
                cm=Cm.getStringValue(),
                cw=Cw.getStringValue(),
                f=F.getStringValue();
        String[] str={"d.X.1=(p.1-p.2-"+cw+"*X.2*"+f+"-i.1*"+rya+")/"+lya,
            "X.1=i.1",
            "d.X.2=(i.1*"+f+"*"+cm+"-I.1)/"+J,
            "O.1=X.2","i.1+i.2=0"};
        return str;
    }
}