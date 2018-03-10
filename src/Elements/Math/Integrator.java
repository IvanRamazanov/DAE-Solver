/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Math;

import ElementBase.DynamMathElem;
import Elements.Environment.Subsystem;
import MathPack.WorkSpace;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class Integrator extends DynamMathElem{
    public Integrator(Subsystem sys){
        super(sys);
        addMathContact('i');
        addMathContact('o');
    }

    public Integrator(boolean flag){
        super(flag);
    }

    @Override
    protected void setParams(){
        setName("Integrator");
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        List<Double> out=new ArrayList<>();
        for(WorkSpace.Variable v:wsX)
            out.add(v.getValue());
        return out;
    }

    @Override
    protected String getDescription(){
        return "This block represents an ideal integration unit.";
    }

    @Override
    public void evalDerivatives(){
        List<Double> res=getInputs().get(0).getValue();
        for(int i=0;i<res.size();i++){
            wsDX.get(i).set(res.get(i));
        }
    }
}

