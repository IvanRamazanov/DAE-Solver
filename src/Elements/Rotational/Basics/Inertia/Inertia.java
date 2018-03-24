package Elements.Rotational.Basics.Inertia;

import ElementBase.MechPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;

public class Inertia extends SchemeElement{
    ScalarParameter P;

    public Inertia(Subsystem sys){
        super(sys);
        addMechCont(new MechPin(this,17,2));
        addMechCont(new MechPin(this,17,52));
    }

    public Inertia(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{"T.2=d.X.1*"+P.toString(),
        "X.1=w.1-w.2","T.1+T.2=0"};
    }

    @Override
    protected String getDescription() {
        return "Represents inertia";
    }

    @Override
    protected void setParams() {
        P=new ScalarParameter("Inertia moment",1);
        getParameters().add(P);
        getInitials().add(new InitParam("Initial speed",0));

        setName("Inertia");
    }
}
