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

    Parameter gainValue;
    
    public Gain(){
        super();
        gainValue=new Parameter("Gain", 2);
        parameters.add(gainValue);
        addMathContact('i');
        addMathContact('o');
        name="Gain";
    }
    
    public Gain(boolean flag){
        super(flag);
        name="Gain";
    }
    
    @Override
    protected List<Double> getValue(int outIndex){
        List<Double> in=getInputs().get(0).getValue();
        List<Double> out=new ArrayList();
        for(int i=0;i<in.size();i++){
            out.add(gainValue.getDoubleValue()*in.get(i));
        }
        return out;
    }
    
}
