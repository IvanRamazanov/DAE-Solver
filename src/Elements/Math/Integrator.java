/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Math;

import ElementBase.DynamMathElem;

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
}

