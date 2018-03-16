package Elements.Environment.ElectricPass;

import ElementBase.ElectricPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;

public class ElectricPass extends SchemeElement {
    private ElectricPin outside;
    private ElectricPin inner;

    public ElectricPass(Subsystem sys){
        super(sys);

        setOutside(new ElectricPin( this,0, sys.getPinOffset()+sys.getLeftPinCnt()* sys.getPinStep()));
        getElemContactList().add(getOutside());
        getOutside().setSystem(sys.getItsSystem());
        sys.getViewPane().getChildren().add(getOutside().getView());
        //add view to subsystem Pane
        addElemCont(inner=new ElectricPin(this,30,15)); // local pin
        getView().setLayoutY(sys.getLeftPinCnt() *30);

        sys.setLeftPinCnt(sys.getLeftPinCnt()+1);
    }

    public ElectricPass(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        return new String[]{
                "p.1-p.2=0",
                "i.1+i.2=0"
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
        ScalarParameter position=new ScalarParameter("Position 0 - left; 1 - right",0);
//            position.setChangeListener((t,o,n)->{
//                if(Integer.parseInt(n)==1){
//                    outside.getView().setLayoutX(getItsSystem().viewPane.getWidth());
//                }
//            });
        getParameters().add(position);
        setName("ElectroPin");
    }

    public ElectricPin getOutside() {
        return outside;
    }

    public void setOutside(ElectricPin outside) {
        this.outside = outside;
    }

    public ElectricPin getInner() {
        return inner;
    }

    public void setInner(ElectricPin inner) {
        this.inner = inner;
    }
}
