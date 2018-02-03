/*
 * The MIT License
 *
 * Copyright 2017 Иван.
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
package Elements;

import ElementBase.ElemPin;
import ElementBase.MathElement;
import ElementBase.ShemeElement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Иван
 */
public class CircuitBreaker extends ShemeElement{
    Logic itsLogic;
    List<Parameter> params=this.parameters;
    
    public CircuitBreaker(){
        super();
        addElemCont(new ElemPin(this, 12, 5));
        addElemCont(new ElemPin(this, 12, 60));
        addHiddenMathContact('i');
        addHiddenMathContact('o');
        itsLogic=new Logic(false);
        linkLogic(itsLogic);
        
        this.parameters.add(new Parameter("Max curr, A", 15.0));
        name="Автомат";
    }
    
    public CircuitBreaker(boolean catalog){
        super(catalog);
        
        this.parameters.add(new Parameter("Max curr, A", 15.0));
        name="Автомат";
    }
    
    private void linkLogic(Logic logic){
        this.mathInputs.get(0).setSource(logic.getOutputs().get(0));
        this.mathOutputs.get(0).setSource(logic.getInputs().get(0));
        logic.getInputs().get(0).setSource(this.mathOutputs.get(0));
        logic.getOutputs().get(0).setSource(this.mathInputs.get(0));
    }

    @Override
    public String[] getStringFunction() {
        String[] out={"p.1-p.2=i.1*if(I.1,10000,0.001)","O.1=i.1","i.1+i.2=0"};
        return out;
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("delete Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void init(){
        this.itsLogic.init();
    }
    
    
    public class Logic extends MathElement{
        boolean flag=false;
        
        public Logic(boolean adf){
            super(adf);
            addHideMathContact('i');
            addHideMathContact('o');
//            raschetkz.RaschetKz.MathElemList.add(this);
        }

        @Override
        protected List<Double> getValue(int outIndex) {
            if(!flag){
                double val=inputs.get(0).getValue().get(0);
                List<Double> out=new ArrayList();
                if(Math.abs(val)>params.get(0).getDoubleValue())  {
                    flag=true;
                    out.add(1.0);
                    return out;
                }
                else{
                    out.add(0.0);
                    return out;
                }
            }else{
                List<Double> out=new ArrayList();
                out.add(1.0);
                return out;
            }
        }

        @Override
        protected void init(){
            flag=false;
        }
        
    }
}
