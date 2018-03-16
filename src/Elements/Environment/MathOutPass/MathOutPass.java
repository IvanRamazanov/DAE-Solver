package Elements.Environment.MathOutPass;

import ElementBase.MathElement;
import ElementBase.MathInPin;
import ElementBase.MathOutPin;
import Elements.Environment.Subsystem.Subsystem;

import java.util.List;

public class MathOutPass extends MathElement {
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

    public MathOutPin getOutside() {
        return outside;
    }

    public void setOutside(MathOutPin outside) {
        this.outside = outside;
    }

    public MathInPin getInner() {
        return inner;
    }

    public void setInner(MathInPin inner) {
        this.inner = inner;
    }
}
