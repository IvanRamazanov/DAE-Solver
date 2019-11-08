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

import ElementBase.MathInPin;

import java.util.ArrayList;

/**
 *
 * @author Ivan
 */
public class WorkSpace {
    private ArrayList<Variable> variableList;
//    private ArrayList<MathInPin> inputs;

    public WorkSpace(){
        variableList=new ArrayList<>();

    }

    public double get(int indx){
        return variableList.get(indx).getValue();
    }

    public static boolean isRealVariable(String name){
        return !name.startsWith("I.") && !name.startsWith("time");
    }

    public ArrayList<String> getVarNameList(){
        ArrayList<String> out=new ArrayList();
        for(int i=0;i<variableList.size();i++)
            out.add(variableList.get(i).name);
        return out;
    }

    public String getName(int i){
        return variableList.get(i).name;
    }

    public Variable add(String name,Double value){
        Variable newVar=get(name);
        if(newVar==null){
            newVar=new Variable(name,value);
            getVarList().add(newVar);
        }
        return newVar;
    }

    public Variable set(String name, StringGraph value){
        Variable newVar;
        if((newVar=get(name))!=null){
            getVarList().remove(newVar);
            newVar=new StringFuncVar(name,value,this);
            getVarList().add(newVar);
        }else{
            newVar=new StringFuncVar(name,value,this);
            getVarList().add(newVar);
        }
        return newVar;
    }

    public Variable addMathIn(String name,MathInPin value){
        Variable newVar=get(name);
        if(newVar==null){
            newVar=new MathInpVar(name,value);
            getVarList().add(newVar);
        }
        return newVar;
    }

    /**
     * @return the varList
     */
    public ArrayList<Variable> getVarList() {
        return variableList;
    }

    private boolean contains(String name){
        for(Variable var:variableList){
            if(var.name.equals(name))
                return true;
        }
        return false;
    }

    public Variable get(String name){
        for(Variable var:variableList){
            if(var.name.equals(name))
                return var;
        }
        return null;
    }

//    public ArrayList<MathInPin> getInputs() {
//        return inputs;
//    }
//
//    public void setInputs(ArrayList<MathInPin> inputs) {
//        this.inputs = inputs;
//    }

    public class Variable{
        private String name;
        private double value;
        private String type;
        private int index;

        protected Variable(){}

        private Variable(String name,double value){
            this.name=name;
            this.value=value;

            index=name.lastIndexOf(".");
            if(index!=-1)
                index=Integer.valueOf(name.substring(index+1))-1;
        }

        public void set(double val){
            value=val;
        }

        public double getValue(){
            return value;
        }

        public String getName() {
            return name;
        }

        public int getIndex(){
            return index;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public class StringFuncVar extends Variable{
        StringGraph value;
        WorkSpace ws;

        private StringFuncVar(String name,StringGraph value, WorkSpace ws){
            this.setName(name);
            this.value=value;
            this.ws=ws;

            setIndex(Integer.valueOf(name.substring(name.lastIndexOf(".")+1))-1);
        }

        @Override
        public double getValue(){
            return value.evaluate(ws);
        }
    }

    public class MathInpVar extends Variable{
        private MathInPin source;
        //private int index;

        public MathInpVar(String name,MathInPin val){
            source=val;

            int tmp=name.indexOf('[');
            if(tmp==-1){
                setIndex(0);
            }else{
                String str=name.substring(tmp+1,name.lastIndexOf(']'));
                setIndex(Integer.parseInt(str) - 1);
            }

            this.setName(name);
//            index=name.indexOf('.');
//            index=Integer.parseInt(name.substring(index+1))-1;
        }

        @Override
        public double getValue(){
            return source.getValue().get(getIndex()); // I.n[index]
        }
    }
}

