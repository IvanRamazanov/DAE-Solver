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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class Mux extends MathElement{

    Parameter inpValue;
    
    public Mux(){
        super();
        inpValue=new Parameter("Кол-во входов", 2);
        parameters.add(inpValue);
        addMathContact('i');
        addMathContact('i');
        addMathContact('o');
        
        name="Мукс";
    }
    
    public Mux(boolean flag){
        super(flag);
        
        inpValue=new Parameter("Кол-во входов", 2);
        parameters.add(inpValue);
        name="Мукс";
    }
    
    @Override
    protected List<Double> getValue(int outIndex) {
        List<Double> out=new ArrayList();
        out.addAll(inputs.get(0).getValue());
        out.addAll(inputs.get(1).getValue());
        return out;
    }
    
}
