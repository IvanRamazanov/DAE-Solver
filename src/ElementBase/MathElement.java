/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Elements.Environment.Subsystem.Subsystem;

import java.util.List;

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

//    @Override
//    public void delete(){
//        getInputs().forEach(elemCont->{
//            elemCont.delete();
//        });
//        getInputs().clear();
//
//        getOutputs().forEach(elemCont->{
//            elemCont.delete();
//        });
//        getOutputs().clear();
//
//        getItsSystem().getElementList().remove(this);
//        getItsSystem().getDrawBoard().getChildren().remove(this.getView());
//    }

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
}
