package Elements.Rotational;

import ElementBase.ElemMechPin;
import ElementBase.SchemeElement;

public class Inertia extends SchemeElement{
    Parameter P;

    public Inertia(){
        super();
        addMechCont(new ElemMechPin(this,17,2));
        addMechCont(new ElemMechPin(this,17,52));
    }

    public Inertia(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{"T.1=d.X.1*"+P.toString(),
        "X.1=w.1-w.2","T.1+T.2=0"};
    }

    @Override
    protected String getDescription() {
        return "Represents inertia";
    }

    @Override
    protected void setParams() {
        P=new Parameter("Inertia moment",1);
        getParameters().add(P);
        getInitials().add(new InitParam("Initial speed",0));

        setName("Inertia");
    }
}
