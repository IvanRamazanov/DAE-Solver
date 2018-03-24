/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Electric.Machines.DPTnV;

import ElementBase.ElectricPin;
import ElementBase.MechPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;

/**
 *
 * @author Ivan
 */
public class DPTnV extends SchemeElement {
    private ScalarParameter Rya,Lya,J0,Cm,Cw,F,Rf,Lf;

    public DPTnV(Subsystem sys){
        super(sys);
//        Dymamic=true;
        addElectricCont(new ElectricPin(this, 31, 4));
        addElectricCont(new ElectricPin(this, 31, 66));
        addElectricCont(new ElectricPin(this, 11, 4));
        addElectricCont(new ElectricPin(this, 11, 66));

        addMechCont(new MechPin(this,41,35));
        addMechCont(new MechPin(this,4,35));
    }

    public DPTnV(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String rya=Rya.toString(),
                lya=Lya.toString(),
                J=J0.toString(),
                cm=Cm.toString(),
                cw=Cw.toString(),
                f=F.toString(),
                rf=Rf.toString(),
                lf=Lf.toString();
        return new String[]{"d.X.1=(p.1-p.2-" + cw + "*X.2*X.3*" + lf + "-X.1*" + rya + ")/" + lya,
                "d.X.2=(X.1*X.3*" + lf + "*" + cm + "-T.1)/" + J,
                "d.X.3=(p.3-p.4-" + rf + "*X.3)/" + lf,
                "X.1=i.1",
                "X.3=i.3",
                "X.2=w.1-w.2",
                "T.1+T.2=0",
                "i.1+i.2=0", "i.3+i.4=0"};
    }

    @Override
    protected void setParams(){
        Rya=new ScalarParameter("Rotor reluctance", 0.1);
        this.parameters.add(Rya);
        Lya=new ScalarParameter("Rotor inductance", 0.001);
        this.parameters.add(Lya);
        J0=new ScalarParameter("Inertia", 10);
        this.parameters.add(J0);
        Cm=new ScalarParameter("Cm", 10);
        this.parameters.add(Cm);
        Cw=new ScalarParameter("Cw", 10);
        this.parameters.add(Cw);
        F=new ScalarParameter("Flux", 1);
        this.parameters.add(F);
        Rf=new ScalarParameter("Stator reluctance", 0.1);
        this.parameters.add(Rf);
        Lf=new ScalarParameter("Stator inductance", 0.1);
        this.parameters.add(Lf);

        this.initials.add(new InitParam("Rotor current", 0));
        this.initials.add(new InitParam("Anrular velocity", 0));
        this.initials.add(new InitParam("Stator current", 0));
        setName("DC motor");
    }

    @Override
    protected String getDescription(){
        return "This block represents a DC motor with external excitation winding.\n" +
                "Input: mechanical torque in N*m.\n" +
                "Output: angular velocity in rad/sec.";
    }
}
