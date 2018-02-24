/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Math;

import ElementBase.DynamMathElem;

import java.util.List;

/**
 *
 * @author Ivan
 */
public class Integrator extends DynamMathElem{
    public Integrator(){
        super();
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
        return X_old;
    }

    @Override
    protected String getDescription(){
        return "This block represents an ideal integration unit.";
    }
}

