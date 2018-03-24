package Elements.Environment.ThreePhasePass;

import Connections.LineMarker;
import ElementBase.Pass;
import ElementBase.Pin;
import ElementBase.SchemeElement;
import ElementBase.ThreePhasePin;
import Elements.Environment.Subsystem.Subsystem;
import raschetkz.RaschetKz;

public class ThreePhasePass extends SchemeElement implements Pass {
    private ThreePhasePin outside;
    private ThreePhasePin inner;

    public ThreePhasePass(Subsystem sys){
        super(sys);

        outside=new ThreePhasePin( this,0, Subsystem.getPinOffset() + sys.getLeftPinCnt() * Subsystem.getPinStep());
        getThreePhaseContacts().add(outside);
        getOutside().setSystem(sys.getItsSystem());
        sys.getViewPane().getChildren().add(getOutside().getView());
        //add view to subsystem Pane
        inner=new ThreePhasePin(this,30,15);
        addThreePhaseCont(inner); // local pin
        getView().setLayoutY(sys.getLeftPinCnt() *30);

        sys.setLeftPinCnt(sys.getLeftPinCnt() + 1);
    }

    public ThreePhasePass(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{
                "p.1-p.4=0",
                "p.2-p.5=0",
                "p.3-p.6=0",
                "i.1+i.4=0",
                "i.2+i.5=0",
                "i.3+i.6=0"
        };
    }

    public Pin getOutside() {
        return outside;
    }

    public Pin getInner() {
        return inner;
    }

    @Override
    public void delete(){
//        getItsSystem().getPasses().remove(this);
//        getItsSystem().getThreePhaseContacts().remove(getOutside());
//        outside.delete();
        super.delete();
    }

    @Override
    protected String getDescription() {
        return "Passes values from and in sybsystems";
    }

    @Override
    protected void setParams() {
        setName("ThreePhaseConn");
    }

    public void setPass(Subsystem oldSys, LineMarker lm) {
//        RaschetKz.elementList.add(this);

        //reconnect
        Pin oldPin=lm.getItsConnectedPin();
        lm.bindElemContact(getInner());
        oldPin.createWire(oldSys,oldPin,getOutside());
    }
}
