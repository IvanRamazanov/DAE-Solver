/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import java.util.List;

import Elements.Environment.Subsystem.Subsystem;
import raschetkz.RaschetKz;

/**
 *
 * @author Ivan
 */
public abstract class MathElement extends Element{

    public MathElement(Subsystem sys){
        super(sys);

        maxX=viewPane.getBoundsInLocal().getMaxX();

        mathContOffset=15;
    }

    public MathElement(boolean catalog) {
        super(catalog);
    }

    @Override
    public void delete(){
        getInputs().forEach(elemCont->{
            elemCont.delete();
        });
        getInputs().clear();

        getOutputs().forEach(elemCont->{
            elemCont.delete();
        });
        getOutputs().clear();

        RaschetKz.elementList.remove(this);
        getItsSystem().getDrawBoard().getChildren().remove(this.getView());
    }

    abstract protected List<Double> getValue(int outIndex);

    @Override
    public void init(){
        // check dimensions
//        presentIn=new ArrayList<>();
//        for(MathInPin mip:getInputs()){
//            List<Double> in=mip.getValue();
//            presentIn.add(in);
//        }
    }

    /**
     * @return the inputs
     */
    public List<MathInPin> getInputs() {
        return mathInputs;
    }

    /**
     * @return the outputs
     */
    public List<MathOutPin> getOutputs() {
        return mathOutputs;
    }
}
