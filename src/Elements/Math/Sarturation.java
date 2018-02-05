/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Math;

import ElementBase.MathElement;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class Sarturation extends MathElement {

    Parameter levelUp;
    Parameter levelDown;
    
    public Sarturation(){
        super();
        addMathContact('i');
        addMathContact('o');
    }
    
    public Sarturation(boolean flag){
        super(flag);
        
        
    }
    
    @Override
    protected List<Double> getValue(int outIndex) {
        double up=levelUp.getDoubleValue();
        double down=levelDown.getDoubleValue();
        List<Double> out=getInputs().get(0).getValue();
        for(int i=0;i<out.size();i++){
            if(out.get(i)>up){
                out.set(i, up);
            }else if(out.get(i)<down){
                out.set(i,down);
            }
        }
        return out;
    }
    
    @Override
    protected void setParams(){
        levelUp=new Parameter("Upper limit", 1);
        levelDown=new Parameter("Lower limit", -1);
        parameters.add(levelUp);
        parameters.add(levelDown);
        setName("Sarturation");
    }
}
