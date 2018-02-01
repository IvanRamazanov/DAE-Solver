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
package MathPackODE;

import ElementBase.DynamMathElem;
import ElementBase.OutputElement;
import java.util.List;

/**
 *
 * @author Иван
 */
public class SolverEuler extends Solver{
    
    
    public SolverEuler(){
        
    }
    
    @Override
    public void simpleUpdate(List<Double> x,List<Double> dx){
        // стого говоря один хуй писать в x
        for(int i=0;i<x.size();i++){
            x.set(i, x.get(i)+dx.get(i)*dt);
        }
    }
    
    @Override
    public void evalNextStep(){
        //estimate p.1... i.1...(Newton...)
//        for(int i=0;i<diffSystem.size();i++){
//            ds[i]=diffSystem.get(i).evaluate(vars,inps);
////            dx[i]=vars.get("d.X."+(i+1));
//        }
        //update dX in each elem
        for(int i=0;i<mathDynamics.size();i++){
            mathDynamics.get(i).evalDerivatives(time);
        }
        evalSysState(); // Eval current dX
        // Euler method itself
        for(int i=0;i<diffRank;i++){
            vars.setValue("X."+(i+1), vars.get("d.X."+(i+1))*dt+vars.get("X."+(i+1)));
        }
        //Update X(n-1)
        for(DynamMathElem delem:mathDynamics){
            delem.updateOutputs(this);      // и сюда)))0
        }
        
        for(OutputElement elem:mathOuts){
            elem.updateData(time);
        }
    }
}
