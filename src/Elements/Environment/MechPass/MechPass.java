package Elements.Environment.MechPass;

import ElementBase.MechPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;

public class MechPass extends SchemeElement {
    private MechPin outside;
    private MechPin inner;

    public MechPass(Subsystem sys){
        super(sys);

        outside=new MechPin( sys,0, Subsystem.getPinOffset() + sys.getLeftPinCnt() * Subsystem.getPinStep());
        getMechContactList().add(outside);
        getOutside().setSystem(sys.getItsSystem());
        sys.getViewPane().getChildren().add(outside.getView());
        //add view to subsystem Pane
        addMechCont(inner=new MechPin(this,30,15)); // local pin
        getView().setLayoutY(sys.getLeftPinCnt() *30);

        sys.setLeftPinCnt(sys.getLeftPinCnt() + 1);
    }

    public MechPass(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{
                "w.1-w.2=0",
                "T.1+T.2=0"
        };
    }

    @Override
    public void init() {

    }

    @Override
    public void delete() {

    }

    @Override
    protected String getDescription() {
        return "Passes values from and in sybsystems";
    }

    @Override
    protected void setParams() {
        setName("MechOut");
    }

    public MechPin getOutside() {
        return outside;
    }

    public void setOutside(MechPin outside) {
        this.outside = outside;
    }

    public MechPin getInner() {
        return inner;
    }

    public void setInner(MechPin inner) {
        this.inner = inner;
    }
}
