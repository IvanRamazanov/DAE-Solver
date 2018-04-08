package Elements.Electric.ThreePhase.Basics.Load;

import ElementBase.ElectricPin;
import ElementBase.SchemeElement;
import ElementBase.ThreePhasePin;
import Elements.Environment.Subsystem.Subsystem;

public class Load extends SchemeElement{
    VectorParameter R;

    public Load(Subsystem sys){
        super(sys);

        addElectricCont(new ElectricPin(this,25,66));  //reference p.1
        addThreePhaseCont(new ThreePhasePin(this,25,5)); // p.2-p.4
    }

    public Load(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        double[] v=R.getValue();
        String r1=Double.toString(v[0]),
                r2=Double.toString(v[1]),
                r3=Double.toString(v[2]);
        return new String[]{
            "p.2-p.1="+r1+"*i.2",
                "p.3-p.1="+r2+"*i.3",
                "p.4-p.1="+r3+"*i.4",
                "i.1+i.2+i.3+i.4=0"
        };
    }

    @Override
    protected String getDescription() {
        return "Star connected active load";
    }

    @Override
    protected void setParams() {
        R=new VectorParameter("Resistance","[10 10 10]");
        getParameters().add(R);

        setName("Three-phase Load");
    }
}
