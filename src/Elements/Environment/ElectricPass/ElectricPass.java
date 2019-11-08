package Elements.Environment.ElectricPass;

import Connections.LineMarker;
import ElementBase.ElectricPin;
import ElementBase.Pass;
import ElementBase.Pin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;

public class ElectricPass extends SchemeElement implements Pass {
    private Pin outside;
    private Pin inner;

    public ElectricPass(Subsystem sys){
        super(sys);

        setOutside(new ElectricPin( this,0, sys.getPinOffset()+sys.getLeftPinCnt()* sys.getPinStep()));
        getElemContactList().add((ElectricPin) getOutside());
        getOutside().setSystem(sys.getItsSystem());
        sys.getViewPane().getChildren().add(getOutside().getView());
        //add view to subsystem Pane
        setInner(new ElectricPin(this,30,15));
        addElectricCont((ElectricPin) getInner()); // local pin
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

    public Pin getOutside() {
        return outside;
    }

    public void setOutside(Pin outside) {
        this.outside = outside;
    }

    public Pin getInner() {
        return inner;
    }

    public void setInner(Pin inner) {
        this.inner = inner;
    }

    @Override
    public void delete(){
        getItsSystem().getViewPane().getChildren().remove(outside.getView());
        getItsSystem().setLeftPinCnt(getItsSystem().getLeftPinCnt()-1);
        super.delete();
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

    public void setPass(Subsystem oldSys, LineMarker lm) {
//        RaschetKz.elementList.add(this);

        //reconnect
        Pin oldPin=lm.getItsConnectedPin();
        lm.bindElemContact(getInner());
        oldPin.createWire(oldSys,oldPin,getOutside());
    }
}
