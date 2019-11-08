/*
  To change this license header, choose License Headers in Project Properties.
  To change this template file, choose Tools | Templates
  and open the template in the editor.
 */
package ElementBase;

import Elements.Environment.Subsystem.Subsystem;

import java.util.List;

/**
 * Number of functions equals NumOfContacts-1. Not include obvious current dependencies.
 * @author Ivan
 */
public abstract class SchemeElement extends Element{

    public SchemeElement(Subsystem sys){
        super(sys);
        maxX=viewPane.getBoundsInLocal().getMaxX();
        mathContOffset=viewPane.getBoundsInLocal().getMaxY()/2;

    }

    public SchemeElement(boolean catalog){
        super(catalog);
    }

    public abstract String[] getStringFunction();

    @Override
    public void init(){}

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


