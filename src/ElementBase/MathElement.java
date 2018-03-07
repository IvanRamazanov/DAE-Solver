/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Connections.MathMarker;

import java.util.ArrayList;
import java.util.List;

import raschetkz.RaschetKz;

/**
 *
 * @author Ivan
 */
public abstract class MathElement extends Element{
    //protected List<List<Double>> presentIn;
//    private final double contStep=15;
//    private double maxX;

    public MathElement(){
        super();

        maxX=viewPane.getBoundsInLocal().getMaxX();

        mathContOffset=15;
    }

    public MathElement(boolean catalog) {
        super(catalog);
    }

//    final protected void addHiddenMathContact(char ch){
//        if(ch=='i'){
//            //if(mathInputs==null) inputs=new ArrayList();
//            MathInPin ic=new MathInPin();
//            getInputs().add(ic);
//        }else{
//            //if(outputs==null) outputs=new ArrayList();
//            MathOutPin oc=new MathOutPin(this);
//            getOutputs().add(oc);
//        }
//    }

    @Override
    public void delete(){
        //disconnect !!!!!
        this.getInputs().forEach(pin->{
            if(pin.getItsConnection()!=null)
                pin.getItsConnection().unPlug();
        });
        RaschetKz.elementList.remove(this);
        RaschetKz.drawBoard.getChildren().remove(this.getView());
    }

//    /**
//     *
//     * @param ch 'i' or 'o'
//     */
//    final protected Pin addMathContact(char ch){
//        Pin p=super.addMathContact(ch);
//        if(ch=='o')
//            p.setOwner(this);
//        return p;
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

//    /**
//     * @return the parameters
//     */
//    public List<Parameter> getParameters() {
//        return parameters;
//    }

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
