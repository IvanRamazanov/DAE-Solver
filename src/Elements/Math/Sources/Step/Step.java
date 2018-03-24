/*
 * The MIT License
 *
 * Copyright 2017 Ivan.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package Elements.Math.Sources.Step;

import ElementBase.MathElement;
import Elements.Environment.Subsystem.Subsystem;
import MathPackODE.Solver;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class Step extends MathElement{
    private ScalarParameter moment,amplitudeOff,amplitudeOn;

    public Step(Subsystem sys){
        super(sys);
        addMathContact('o');
    }

    public Step(boolean flag){
        super(flag);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        double t=Solver.time,
                t0=moment.getValue();
        List<Double> out=new ArrayList();
        if(t>t0) out.add(amplitudeOn.getValue());
        else  out.add(amplitudeOff.getValue());
        return out;
    }

    @Override
    protected void setParams(){
        amplitudeOn=new ScalarParameter("level on",1);
        parameters.add(amplitudeOn);
        amplitudeOff=new ScalarParameter("level off",0);
        parameters.add(amplitudeOff);
        moment=new ScalarParameter("Switch time",0.5);
        parameters.add(moment);
        setName("Step");
    }

    @Override
    protected String getDescription(){
        return "This block represents a step function output.\n" +
                "It changes value from 'level off' to 'level on' at 'Switch time' moment of time.";
    }
}

