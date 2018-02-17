/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements;

import ElementBase.ElemMechPin;
import ElementBase.ElemPin;
import ElementBase.SchemeElement;

/**
 *
 * @author Ivan
 */
public class DPTPM extends SchemeElement {
    private Parameter Rya,Lya,J0,Cm,Cw,F;

    public DPTPM(){
        super();
//        Dymamic=true;
        addElemCont(new ElemPin(this, 31, 4));
        addElemCont(new ElemPin(this, 31, 66));
        addMechCont(new ElemMechPin(this,40,40));
        addMechCont(new ElemMechPin(this,4,40));
    }

    public DPTPM(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String rya=Rya.toString(),
                lya=Lya.toString(),
                J=J0.toString(),
                cm=Cm.toString(),
                cw=Cw.toString(),
                f=F.toString();
        return new String[]{"d.X.1=(p.1-p.2-" + cw + "*X.2*" + f + "-i.1*" + rya + ")/" + lya,
                "X.1=i.1",
                "X.2=w.1-w.2",
                "-T.1=T.2+i.1*" + f + "*" + cm,
                "d.X.2=(i.1*" + f + "*" + cm + "-T.1)/" + J,
                "i.1+i.2=0"};
    }

    @Override
    protected void setParams(){
        Rya=new Parameter("Rotor reluctance", 0.6);
        this.parameters.add(Rya);
        Lya=new Parameter("Rotor inductance", 0.012);
        this.parameters.add(Lya);
        J0=new Parameter("Inertia", 1);
        this.parameters.add(J0);
        Cm=new Parameter("Cm", 1.8);
        this.parameters.add(Cm);
        Cw=new Parameter("Cw", 1.8);
        this.parameters.add(Cw);
        F=new Parameter("Flux", 1);
        this.parameters.add(F);

        this.initials.add(new InitParam("Current", 0));
        this.initials.add(new InitParam("Angular velocity", 0));
        setName("DC motor with\npermanent magnets");
    }

    @Override
    protected String getDescription(){
        return "This block represents a DC motor with permanent magnets on stator." +
                "Input: mechanical torque in N*m.\n" +
                "Output: angular velocity in rad/sec.";
    }
}
