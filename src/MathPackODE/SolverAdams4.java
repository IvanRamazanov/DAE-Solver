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
import ElementBase.MathInPin;
import ElementBase.OutputElement;
import MathPack.StringGraph;
import MathPack.WorkSpace;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Иван
 */
public class SolverAdams4 extends Solver{
    private double[][] ds;
    private int rank=4,matrStep,melemStep,maxMElem;
    boolean fullfill;
    List<List<List<Double>>> dx; 
    
    public SolverAdams4(){
        
    }

    @Override
    public void simpleUpdate(List<Double> x,List<Double> dx){
        // стого говоря один хуй писать в x
        List<List<Double>> m=this.dx.get(melemStep);
        if(fullfill){
            for(int i=0;i<x.size();i++){
                double val=x.get(i)+dt*(55.0/24.0*dx.get(i)-59.0/24.0*m.get(i).get((matrStep+3)%rank)
                        +37.0/24.0*m.get(i).get((matrStep+2)%rank)-3.0/8.0*m.get(i).get((matrStep+1)%rank));
                
                m.get(i).set(matrStep%rank,dx.get(i));
                x.set(i, val);
            }
        }else{
            for(int i=0;i<dx.size();i++){
                m.get(i).set(matrStep,dx.get(i));
                x.set(i, x.get(i)+m.get(i).get(matrStep)*dt);
            }
        }
        melemStep=(melemStep+1)%maxMElem;
    }
    
    @Override
    public void evalNextStep(){
//        for(int i=0;i<diffSystem.size();i++){
//            ds[i]=diffSystem.get(i).evaluate(time,vars,inps);
////            dx[i]=vars.get("d.X."+(i+1));
//        }
//        //update dX in each elem
//        for(int i=0;i<mathDynamics.size();i++){
//            mathDynamics.get(i).evalDerivatives(time);
//        }
//        // Euler method itself
//        for(int i=0;i<diffSystem.size();i++){
//            vars.setValue("X."+(i+1), ds[i]*dt+vars.get("X."+(i+1)));
//        }
//        //Update X(n-1)
//        for(DynamMathElem delem:mathDynamics){
//            delem.updateOutputs(this);      // и сюда)))0
//        }
//        evalSysState(time); //Newton update 
//        for(OutputElement elem:mathOuts){
//            elem.updateData(time);
//        }
        
        
        if(fullfill){
            for(int i=0;i<diffRank;i++){
                ds[i][matrStep]=vars.get("d.X."+(i+1));
            }
            //update dX in each math elem
            for(int i=0;i<mathDynamics.size();i++){
                mathDynamics.get(i).evalDerivatives(time);
            }
            for(int i=0;i<diffRank;i++){
                double val=vars.get("X."+(i+1))+dt*(55.0/24.0*ds[i][matrStep]-59.0/24.0*this.ds[i][(matrStep+3)%rank]
                        +37.0/24.0*this.ds[i][(matrStep+2)%rank]-3.0/8.0*this.ds[i][(matrStep+1)%rank]);
                vars.setValue("X."+(i+1), val);
            }
            //Update X(n-1)
            for(DynamMathElem delem:mathDynamics){
                delem.updateOutputs(this);      // и сюда)))0
            }
            matrStep=(matrStep+1)%rank;
        }else{
            for(int i=0;i<diffRank;i++){
                ds[i][matrStep]=vars.get("d.X."+(i+1));
            }
            //update dX in each math elem
            for(int i=0;i<mathDynamics.size();i++){
                mathDynamics.get(i).evalDerivatives(time);
            }
            for(int i=0;i<diffRank;i++){
                vars.setValue("X."+(i+1), ds[i][matrStep]*dt+vars.get("X."+(i+1)));
            }
            //Update X(n-1)
            for(DynamMathElem delem:mathDynamics){
                delem.updateOutputs(this);      // и сюда)))0
            }
            if(++matrStep==rank-1) fullfill=true;
        }
        evalSysState(); //Newton update 
        for(OutputElement elem:mathOuts){
            elem.updateData(time);
        }
    }
    
    @Override
    protected void selfInit(){
        matrStep=0;
        ds=new double[diffRank][rank];
        dx=new ArrayList();
        melemStep=0;
        maxMElem=mathDynamics.size();
        mathDynamics.forEach(el->{
            List<List<Double>> m=new ArrayList(el.getRank());
            for(int i=0;i<el.getRank();i++){
                m.add(new ArrayList(rank));
                for(int j=0;j<rank;j++){
                    m.get(i).add(0.0);
                }
            }
            dx.add(m);
        });
    }
}
