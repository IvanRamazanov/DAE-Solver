package Elements.Electric.ThreePhase.Sources.VoltageSource;

import ElementBase.ElectricPin;
import ElementBase.SchemeElement;
import ElementBase.ThreePhasePin;
import Elements.Environment.Subsystem.Subsystem;

import static java.lang.StrictMath.PI;

public class VoltageSource extends SchemeElement{
    ScalarParameter A,f,phi;

    public VoltageSource(Subsystem sys){
        super(sys);

        addElectricCont(new ElectricPin(this,26,66));
        addThreePhaseCont(new ThreePhasePin(this,26,4));
    }

    public VoltageSource(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{  "p.2="+A+"*"+"sin("+(2*PI)+"*"+f+"*time+"+phi+")+p.1",
                "p.3="+A+"*"+"sin("+(2*PI)+"*"+f+"*time+"+phi+"-"+(2*PI/3)+")+p.1",
                "p.4="+A+"*"+"sin("+(2*PI)+"*"+f+"*time+"+phi+"+"+(2*PI/3)+")+p.1","i.1+i.2+i.3+i.4=0"};
    }

    @Override
    protected String getDescription() {
        return "Three phase voltage source";
    }

    @Override
    protected void setParams() {
        getParameters().add(A=new ScalarParameter("Amplitude",220));
        getParameters().add(f=new ScalarParameter("frequency",50));
        getParameters().add(phi=new ScalarParameter("phase shift, rad",0));

        setName("Voltage source");
    }
}
