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
package MathPack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class WorkSpace {
    private ArrayList<Double> varValues;
    private List<String> varnames;

    public WorkSpace(){
        varValues=new ArrayList();
        varnames=new ArrayList();
    }

    public double get(String name){
        return varValues.get(varnames.indexOf(name));
    }

    public double get(int indx){
//        try{
//            return getVarList().get(name);
//        }catch(Exception ex){
//            System.err.println("No such variable: "+name);
//            return 0.0;
//        }
        return varValues.get(indx);
    }

    public List<String> getVarNameList(){
//        List<String> out=new ArrayList();
//        out.addAll(varList.keySet());
//        return out;
        return varnames;
    }

    public String getName(int i){
        return varnames.get(i);
    }

    public void setValue(int indx,double value){
//        if(getVarList().containsKey(name)){
//            getVarList().replace(name, value);
//        }else{
//            System.err.println("No such variable!: "+name);
//        }
        varValues.set(indx, value);
    }

    public void setValue(String name,double value){
        varValues.set(varnames.indexOf(name), value);
    }

    public void add(String name,Double value){
        if(varnames.contains(name)){
            //System.err.println("Variable "+name+" is alredy contains in workspace!");
        }else{
            getVarList().add(value);
            varnames.add(name);
        }
    }

    /**
     * @return the varList
     */
    public ArrayList<Double> getVarList() {
        return varValues;
    }
}

