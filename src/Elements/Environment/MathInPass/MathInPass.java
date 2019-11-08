package Elements.Environment.MathInPass;

import Connections.LineMarker;
import ElementBase.*;
import Elements.Environment.Subsystem.Subsystem;

import java.util.List;

public class MathInPass extends MathElement implements Pass {
    private MathInPin outside;
    private MathOutPin inner;

    public MathInPass(Subsystem sys){
        super(sys);

        outside=new MathInPin( this,0, Subsystem.getPinOffset() + sys.getLeftPinCnt() * Subsystem.getPinStep());
        getInputs().add(outside);
        outside.setSystem(sys.getItsSystem());
        sys.getViewPane().getChildren().add(outside.getView());
        //add view to subsystem Pane
        inner=new MathOutPin(this,30,15); // local pin
        getOutputs().add(inner);
        viewPane.getChildren().add(inner.getView());
        getView().setLayoutY(sys.getLeftPinCnt() *30);

        sys.setLeftPinCnt(sys.getLeftPinCnt() + 1);
    }

    public MathInPass(boolean val){
        super(val);
    }

    @Override
    public void init() {

    }

    @Override
    public void delete() {
        getItsSystem().getViewPane().getChildren().remove(outside.getView());
        getItsSystem().setLeftPinCnt(getItsSystem().getLeftPinCnt()-1);
        super.delete();
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        return outside.getValue();
    }

    @Override
    protected String getDescription() {
        return "Passes values from and in sybsystems";
    }

    @Override
    protected void setParams() {
        setName("MathIn");
    }

    public MathInPin getOutside() {
        return outside;
    }

    public MathOutPin getInner() {
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
