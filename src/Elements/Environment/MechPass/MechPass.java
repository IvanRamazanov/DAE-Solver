package Elements.Environment.MechPass;

import Connections.LineMarker;
import ElementBase.MechPin;
import ElementBase.Pass;
import ElementBase.Pin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;
import raschetkz.RaschetKz;

public class MechPass extends SchemeElement implements Pass {
    private MechPin outside;
    private MechPin inner;

    public MechPass(Subsystem sys){
        super(sys);

        setOutside(new MechPin( this,0, Subsystem.getPinOffset() + sys.getLeftPinCnt() * Subsystem.getPinStep()));
        getMechContactList().add((MechPin) getOutside());
        getOutside().setSystem(sys.getItsSystem());
        sys.getViewPane().getChildren().add(getOutside().getView());
        //add view to subsystem Pane
        setInner(new MechPin(this,30,15));
        addMechCont((MechPin) getInner()); // local pin
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

    public Pin getOutside() {
        return outside;
    }

    public void setOutside(MechPin outside) {
        this.outside = outside;
    }

    public Pin getInner() {
        return inner;
    }

    public void setInner(MechPin inner) {
        this.inner = inner;
    }

    @Override
    public void delete(){
//        getItsSystem().getPasses().remove(this);
//        getItsSystem().getMechContactList().remove(getOutside());
//        outside.delete();
        super.delete();
    }

    @Override
    protected String getDescription() {
        return "Passes values from and in sybsystems";
    }

    @Override
    protected void setParams() {
        setName("MechOut");
    }

    public void setPass(Subsystem oldSys, LineMarker lm) {
//        RaschetKz.elementList.add(this);

        //reconnect
        Pin oldPin=lm.getItsConnectedPin();
        lm.bindElemContact(getInner());
        oldPin.createWire(oldSys,oldPin,getOutside());
    }
}
