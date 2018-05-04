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
package MathPackODE.Solvers;

import MathPackODE.Solver;

/**
 *
 * @author Иван
 */
public class Adams4 extends Solver {
    private double[][] ds;
    private int rank=4,matrStep;
    boolean fullfill;

    public Adams4(){

    }

    @Override
    public void evalNextStep(){
        if(fullfill){
            for(int i=0;i<diffRank;i++){
                ds[i][matrStep]=dXvector.get(i).getValue();
            }
            for(int i=0;i<diffRank;i++){
                double val=Xvector.get(i).getValue()+dt*(55.0/24.0*ds[i][matrStep]-59.0/24.0*this.ds[i][(matrStep+3)%rank]
                        +37.0/24.0*this.ds[i][(matrStep+2)%rank]-3.0/8.0*this.ds[i][(matrStep+1)%rank]);
                Xvector.get(i).set(val);
            }
            matrStep=(matrStep+1)%rank;
        }else{
            for(int i=0;i<diffRank;i++){
                ds[i][matrStep]=dXvector.get(i).getValue();
            }
            for(int i=0;i<diffRank;i++){
                Xvector.get(i).set(ds[i][matrStep]*dt+Xvector.get(i).getValue());
            }

            if(++matrStep==rank-1) fullfill=true;
        }
        time.set(time.getValue()+dt);
        evalSysState(); //Newton update
    }

    @Override
    protected void selfInit(){
        matrStep=0;
        ds=new double[diffRank][rank];
    }
}

