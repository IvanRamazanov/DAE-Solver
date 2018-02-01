/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Math;

import ElementBase.MathElement;
import MathPackODE.Solver;
import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.sin;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Ivan
 */

public class Sinus extends MathElement{
    private Parameter apmlitude,freq,phase;
    
    public Sinus(){
        super();
        addMathContact('o');
        apmlitude=new Parameter("Aplitude",1);
        parameters.add(apmlitude);
        freq=new Parameter("Frequency",1);
        parameters.add(freq);
        phase=new Parameter("Phase lag",0);
        parameters.add(phase);
        name="Синус";
    }
    
    public Sinus(boolean flag){
        super(flag);
        name="Синус";
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        double ampl=apmlitude.getDoubleValue(),
                f=freq.getDoubleValue(),
                ph=phase.getDoubleValue();
        List<Double> out=new ArrayList();
        out.add(ampl*sin(2*PI*f*Solver.time+ph));
        return out;
    }
    
}
