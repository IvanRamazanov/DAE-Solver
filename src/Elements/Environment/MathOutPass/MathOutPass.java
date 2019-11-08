package Elements.Environment.MathOutPass;

import Connections.LineMarker;
import ElementBase.*;
import Elements.Environment.Subsystem.Subsystem;

import java.util.List;

public class MathOutPass extends MathElement implements Pass{
    private MathOutPin outside;
    private MathInPin inner;

    public MathOutPass(Subsystem sys){
        super(sys);

        outside=new MathOutPin( this,sys.getViewPane().getBoundsInLocal().getMaxX(),
                Subsystem.getPinOffset() +sys.getRightPinCnt()* Subsystem.getPinStep());
        getOutputs().add(outside);
        outside.setSystem(sys.getItsSystem());
        sys.getViewPane().getChildren().add(outside.getView());
        //add view to subsystem Pane
        inner=new MathInPin(this,0,15); // local pin
        getInputs().add(inner);
        viewPane.getChildren().add(inner.getView());
        getView().setLayoutX(sys.getWindowWidth()-50);
        getView().setLayoutY(sys.getRightPinCnt()*30);

        sys.setRightPinCnt(sys.getRightPinCnt()+1);
    }

    public MathOutPass(boolean val){
        super(val);
    }

    @Override
    public void init() {

    }

    @Override
    public void delete() {
        getItsSystem().getViewPane().getChildren().remove(outside.getView());
        getItsSystem().setRightPinCnt(getItsSystem().getRightPinCnt()-1);
        super.delete();
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        return inner.getValue();
    }

    @Override
    protected String getDescription() {
        return "Passes values from and in sybsystems";
    }

    @Override
    protected void setParams() {
        setName("MathOut");
    }

    public Pin getOutside() {
        return outside;
    }

    public Pin getInner() {
        return inner;
    }

    public void setPass(Subsystem oldSys, LineMarker lm) {
//        RaschetKz.elementList.add(this);

        //reconnect
        Pin oldPin=lm.getItsConnectedPin();
        lm.bindElemContact(getInner());
        oldPin.createWire(oldSys,oldPin,getOutside());
    }
}
