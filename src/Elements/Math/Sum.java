/*
 * The MIT License
 *
 * Copyright 2017 ramazanov_im.
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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ramazanov_im
 */
public class Sum extends MathElement{

    Parameter gains;

    public Sum(){
        super();
        addMathContact('i');
        addMathContact('i');
        addMathContact('o');
    }

    public Sum(boolean flag){
        super(flag);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        int rank=getInputs().get(0).getValue().size();
        List<Double> sum=new ArrayList(getInputs().get(0).getValue().size());
        for(int i=0;i<rank;i++){
            double val=0;
//            for(int j=0;j<getInputs().size();j++) {
//                val += getInputs().get(j).getValue().get(i);
//            }
            val+=getInputs().get(0).getValue().get(i);
            double adder=1;
            if(gains.getDoubleValue()==0)
                adder=-1;
            val+=getInputs().get(1).getValue().get(i)*adder;
            sum.add(val);
        }
        return sum;
    }

    @Override
    protected void setParams(){
        gains=new Parameter("1 - '+', 0 - '-'", 0);
        getParameters().add(gains);

        setName("Sum");
    }

    private class StrParam extends Parameter{
        StrParam(String name,String initVal){

        }
    }

    @Override
    protected String getDescription(){
        return "This block calculates sum of Inputs.";
    }
}

