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
 * @author Иван
 */
public class DPTnV extends ShemeElement {
    Parameter Rya,Lya,J0,Cm,Cw,F,Rf,Lf;
    
    public DPTnV(){
        super();
//        Dymamic=true;
        addElemCont(new ElemPin(this, 31, 4));
        addElemCont(new ElemPin(this, 31, 66));
        addElemCont(new ElemPin(this, 11, 4));
        addElemCont(new ElemPin(this, 11, 66));
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
        Rf=new Parameter("Сопротивление возбуждения", 0.1);
        this.parameters.add(Rf);
        Lf=new Parameter("Индуктивность возбуждения", 0.1);
        this.parameters.add(Lf);
        this.initials.add(new InitParam("Ток якоря", 0));
        this.initials.add(new InitParam("Скорость", 0));
        this.initials.add(new InitParam("Ток возбуждения", 0));
        this.addMathContact('o');
        this.addMathContact('i');
        name="ДПТНВ";
    }
    
    public DPTnV(boolean Catalog){
        super(Catalog);
        name="ДПТНВ";
    }

    @Override
    public String[] getStringFunction() {
        String rya=Rya.getStringValue(),
                lya=Lya.getStringValue(),
                J=J0.getStringValue(),
                cm=Cm.getStringValue(),
                cw=Cw.getStringValue(),
                f=F.getStringValue(),
                rf=Rf.getStringValue(),
                lf=Lf.getStringValue();
        String[] str={"d.X.1=(p.1-p.2-"+cw+"*X.2*X.3*"+lf+"-X.1*"+rya+")/"+lya,
                      "d.X.2=(X.1*X.3*"+lf+"*"+cm+"-I.1)/"+J,
                      "d.X.3=(p.3-p.4-"+rf+"*X.3)/"+lf,
                      "X.1=i.1",
                      "X.3=i.3",
                      "O.1=X.2","i.1+i.2=0","i.3+i.4=0"}; //I.1==1.1
        return str;
    }
    
}