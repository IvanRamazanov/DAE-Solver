/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Math;

import ElementBase.MathElement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class Gain extends MathElement{

    ScalarParameter gainValue;

    public Gain(){
        super();
        addMathContact('i');
        addMathContact('o');
    }

    public Gain(boolean flag){
        super(flag);
    }

    @Override
    protected List<Double> getValue(int outIndex){
        List<Double> in=getInputs().get(0).getValue();
        List<Double> out=new ArrayList();
        for(int i=0;i<in.size();i++){
            out.add(gainValue.getValue()*in.get(i));
        }
        return out;
    }

    @Override
    protected void setParams(){
        gainValue=new ScalarParameter("Gain", 2);
        parameters.add(gainValue);
        setName("Gain");
    }

    @Override
    protected String getDescription(){
        return "This block represents an amplifier.\n" +
                "Output = Gain * Input.";
    }
}
