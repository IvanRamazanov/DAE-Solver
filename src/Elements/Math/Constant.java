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
package Elements.Math;

import ElementBase.MathElement;
import Elements.Environment.Subsystem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class Constant extends MathElement{
    private ScalarParameter apmlitude;

    public Constant(Subsystem sys){
        super(sys);
        addMathContact('o');
    }

    public Constant(boolean flag){
        super(flag);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        List<Double> out=new ArrayList();
        double[][] arr=apmlitude.getDoubleValue();
        for(double[] line:arr)
            out.add(line[0]);
        return out;
    }

    @Override
    protected void setParams(){
        apmlitude=new ScalarParameter("Aplitude",1);
        parameters.add(apmlitude);
        setName("Constant");
    }

    @Override
    protected String getDescription(){
        return "This block represents a constant value.";
    }
}

