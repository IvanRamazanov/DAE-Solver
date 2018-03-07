/*
 * The MIT License
 *
 * Copyright 2018 Ivan.
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
package Elements.Math;

import ElementBase.MathElement;
import MathPackODE.Solver;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class Ramp extends MathElement{
    ScalarParameter slope,ton;

    public Ramp(){
        super();
        addMathContact('o');
    }

    public Ramp(boolean flag){
        super(flag);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        double ampl=slope.getValue(),
                t=ton.getValue();

        List<Double> out=new ArrayList();
        if(Solver.time<t){
            out.add(0.0);
        }else{
            out.add((Solver.time-t)*ampl);
        }

        return out;
    }

    @Override
    protected void setParams(){
        slope=new ScalarParameter("Slope",1);
        parameters.add(slope);
        ton=new ScalarParameter("ON time",0);
        parameters.add(ton);
        setName("Ramp");
    }

    @Override
    protected String getDescription(){
        return "This block represents a linear output, that triggers at given time moment.";
    }
}

