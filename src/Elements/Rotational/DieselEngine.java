package Elements.Rotational;

import ElementBase.ElemMechPin;
import ElementBase.SchemeElement;

import java.util.List;

public class DieselEngine extends SchemeElement{
    Parameter Pn,wn,Tde;

    public DieselEngine(){
        super();
        addMechCont(new ElemMechPin(this,40,30));
        addMechCont(new ElemMechPin(this,4,30));
        addMathContact('i'); // fuel consumption
    }

    public DieselEngine(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        double tn=Pn.getDoubleValue()*1000/(wn.getDoubleValue()*2*Math.PI/60);
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
        Pn=new Parameter("Nominal power, kW",50);
        wn=new Parameter("Rated speed, rpm",1500);
        Tde=new Parameter("Engine time constant, 1/sec",20);
        getParameters().addAll(List.of(Pn,wn,Tde));

        getInitials().add(new InitParam("Initial torque",0));

        setName("Diesel engine");
    }
}
