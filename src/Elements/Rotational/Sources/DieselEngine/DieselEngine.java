package Elements.Rotational.Sources.DieselEngine;

import ElementBase.MechPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;

import java.util.List;

public class DieselEngine extends SchemeElement{
    ScalarParameter Pn,wn,Tde;

    public DieselEngine(Subsystem sys){
        super(sys);
        addMechCont(new MechPin(this,40,30));
        addMechCont(new MechPin(this,4,30));
        addMathContact('i'); // fuel consumption
    }

    public DieselEngine(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        double tn=Pn.getValue()*1000/(wn.getValue()*2*Math.PI/60);
        return new String[]{
                "d.X.1=(I.1*"+tn+"-X.1)/"+Tde,
                "X.1=T.1",
                "T.1+T.2=0"
        };
    }

    @Override
    protected String getDescription() {
        return "Mid-speed turbo diesel engine";
    }

    @Override
    protected void setParams() {
        Pn=new ScalarParameter("Nominal power, kW",50);
        wn=new ScalarParameter("Rated speed, rpm",1500);
        Tde=new ScalarParameter("Engine time constant, 1/sec",20);
        getParameters().addAll(List.of(Pn,wn,Tde));

        getInitials().add(new InitParam("Initial torque",0));

        setName("Diesel engine");
    }
}
