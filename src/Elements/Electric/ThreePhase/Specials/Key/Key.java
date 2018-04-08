package Elements.Electric.ThreePhase.Specials.Key;

import ElementBase.SchemeElement;
import ElementBase.ThreePhasePin;
import Elements.Environment.Subsystem.Subsystem;

public class Key extends SchemeElement{
    ScalarParameter ron,roff;

    public Key(Subsystem sub){
        super(sub);

        addThreePhaseCont(new ThreePhasePin(this,25,5));
        addThreePhaseCont(new ThreePhasePin(this,25,66));

        addMathContact('i');
    }

    public Key(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        String ron=Double.toString(this.ron.getValue()*1e-3),
            roff=Double.toString(this.roff.getValue()*1e6);
        return new String[]{
                "p.1-p.4=i.1*if(I.1,"+ron+","+roff+")",
                "p.2-p.5=i.2*if(I.1,"+ron+","+roff+")",
                "p.3-p.6=i.3*if(I.1,"+ron+","+roff+")",
                "i.1+i.4=0",
                "i.2+i.5=0",
                "i.3+i.6=0"
        };
    }

    @Override
    protected String getDescription() {
        return "Contactor";
    }

    @Override
    protected void setParams() {
        getParameters().add(ron=new ScalarParameter("R_on, mOhm",0.1));
        getParameters().add(roff=new ScalarParameter("R_off, MOhm",5));

        setName("Contactor");
    }
}
