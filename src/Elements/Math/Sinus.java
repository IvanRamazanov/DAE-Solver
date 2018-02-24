/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Elements.Math;

import ElementBase.MathElement;
import MathPackODE.Solver;
import static java.lang.StrictMath.sin;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Ivan
 */

public class Sinus extends MathElement{
    private Parameter apmlitude,freq,phase;
    private final double pi=Math.PI;

    public Sinus(){
        super();
        addMathContact('o');
    }

    public Sinus(boolean flag){
        super(flag);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        double ampl=apmlitude.getDoubleValue(),
                f=freq.getDoubleValue(),
                ph=phase.getDoubleValue();
        List<Double> out=new ArrayList();
        out.add(ampl*sin(2*pi*f*Solver.time+ph/180*pi));
        return out;
    }

    @Override
    protected void setParams(){
        apmlitude=new Parameter("Amplitude",1);
        parameters.add(apmlitude);
        freq=new Parameter("Frequency, Hz",1);
        parameters.add(freq);
        phase=new Parameter("Phase shift, deg",0);
        parameters.add(phase);
        setName("Sinus wave");
    }

    @Override
    protected String getDescription(){
        return "This block represents a sinus wave source.";
    }
}

