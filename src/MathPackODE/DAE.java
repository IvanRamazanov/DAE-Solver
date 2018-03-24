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
package MathPackODE;

import Connections.MathWire;
import ElementBase.*;
import MathPack.StringFunctionSystem;
import MathPack.StringGraph;
import MathPack.WorkSpace;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Ivan
 */
public class DAE {
    private List<StringGraph>   innerFuncSystem,
            algSystem,
            newtonFunc;
    private List<List<StringGraph>> Jacob,invJacob,outSystem;
    private WorkSpace vars;
    private List<MathOutPin> outs;
    //private List<MathInPin> inps;
    private List<Double> logtime;
    private List<List<Double>> logdata;
    private List<OutputElement> mathOuts;
    private List<DynamMathElem> dynMaths;
    private List<Updatable> updatableElements;
    private boolean couchyFlag;
    private int symbDiffRank;

    public DAE(){
        algSystem=new ArrayList();
        outSystem=new ArrayList();
        vars=new WorkSpace();
        outs=new ArrayList();
        //inps=new ArrayList();
        logtime=new ArrayList();
        logdata=new ArrayList();

        updatableElements=new ArrayList<>();
    }

    public WorkSpace.Variable addVariable(String name,Double value){
        return getVars().add(name, value);
    }

    public WorkSpace.Variable setVariable(String name,StringGraph value){
        return getVars().set(name, value);
    }

    public void initXes(List<Double> x0, HashMap<String,StringGraph> map){
        for(int i=0;i<x0.size();i++){
            double x=x0.get(i);
            String name="X."+(i+1);
            WorkSpace.Variable link=getVars().add(name,x);

            for(StringGraph sg:getAlgSystem()){
                sg.linkVariableToWorkSpace(name,link);
            }

            for(List<StringGraph> sgl:getOutSystem()){
                for(StringGraph sg:sgl)
                    sg.linkVariableToWorkSpace(name,link);
            }

            for (Map.Entry<String, StringGraph> entry : map.entrySet()) {
                entry.getValue().linkVariableToWorkSpace(name, link);
            }


            name="d.X."+(i+1);
            link=getVars().add(name,x);

            for(StringGraph sg:getAlgSystem()){
                sg.linkVariableToWorkSpace(name,link);
            }

            for(List<StringGraph> sgl:getOutSystem()){
                for(StringGraph sg:sgl)
                    sg.linkVariableToWorkSpace(name,link);
            }
        }
    }

    /**
     * @return the algSystem
     */
    public List<StringGraph> getAlgSystem() {
        return algSystem;
    }

    /**
     * @param algSystem the algSystem to set
     */
    public void setAlgSystem(List<StringGraph> algSystem) {
        this.algSystem = algSystem;
    }

    /**
     * @return the outSystem
     */
    public List<List<StringGraph>> getOutSystem() {
        return outSystem;
    }

    /**
     * @param outSystem the outSystem to set
     */
    public void setOutSystem(List<List<StringGraph>> outSystem) {
        this.outSystem = outSystem;
    }

    public void addAlgFunc(StringGraph right){
        algSystem.add(right);
    }

    public void setCouchyFlag(boolean val){
        couchyFlag=val;
    }

    public void initOutputs(StringFunctionSystem sfs){
        List<MathOutPin> oldOuts=sfs.getOutputs();
        for(int i=0;i<oldOuts.size();i++){
            DaeToMatOut out=new DaeToMatOut(sfs.getOutFuncs().get(i));
            outs.add(out);
            if(oldOuts.get(i).getItsConnection()!=null){  // check if this out pin connect to smthg (ex. to Scope)
                //oldOuts.get(i).getSource().setSource(out);
                ((MathWire)oldOuts.get(i).getItsConnection().getWire()).setSourcePointer(out);
            }
        }
    }

    public void updateOutputs(){
        //x=x+dx
//        for(int i=0;i<dX.size();i++){
//            String name="X."+(i+1);
//            double value=vars.get(name)+dX.get(i)*raschetkz.RaschetKz.dt;
//            vars.setValue(name, value); //Euler
////            data.get(i).add(xes.get(i));
//
//        }
    }

    protected List<Double> getValue(int outIndex) {
        return outs.get(outIndex).getValue();
    }

//    /**
//     * @param outs the outs to set
//     * @param funcs
//     */
//    public void setOuts(List<MathOutPin> outs,List<StringGraph> funcs) {
//        for(int i=0;i<outs.size();i++){
//            DaeToMatOut out=new DaeToMatOut(funcs.get(i));
//            this.outs.add(out);
//            outs.get(i).getItsConnection().setSource(out);
//        }
//    }

    /**
     * @return the inps
     */
    public List<MathInPin> getInps() {
        return vars.getInputs();
    }

    public void initJacobian(int jacobType){
        String varsLayout="[ ";
        for(WorkSpace.Variable var:getVars().getVarList()) {
            if(var.getClass().getSimpleName().equals("Variable")) {
                String str=var.getName();
                if (!str.startsWith("X.")) {
                    varsLayout += str + " ";
                }
            }
        }
        varsLayout+="]";

        Jacob=new ArrayList();
        int i=0;
        for(StringGraph func:algSystem){
            getJacob().add(new ArrayList());
            for(WorkSpace.Variable var:getVars().getVarList()){
                //if(var.startsWith("Cur.")||var.startsWith("Pot.")||var.startsWith("d.X.")){
                if(var.getClass().getSimpleName().equals("Variable"))
                    if(!var.getName().startsWith("X.")){
                        getJacob().get(i).add(func.getDiffer(var.getName()));
                    }
            }
            i++;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\NetBeansLogs\\MyLog.txt",true))) {
            bw.newLine();
            bw.write("Jacobian: "+varsLayout+" out of "+getVars().getVarNameList().toString());
            bw.newLine();
            for(List<StringGraph> row:getJacob()){
                for(StringGraph item:row){
                    bw.write(item.toString()+"     ");
                }
                bw.newLine();
            }
        } catch (IOException e) {}
        if(jacobType==2) //jacob only
            return;
        if(!Jacob.isEmpty()){

            invJacob=MathPack.MatrixEqu.invMatrSymb(getJacob());
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\NetBeansLogs\\MyLog.txt",true))) {
                bw.newLine();
                bw.write("inv Jacobian:");
                bw.newLine();
                for(List<StringGraph> row:getJacob()){
                    for(StringGraph item:row){
                        bw.write(item.toString()+"     ");
                    }
                    bw.newLine();
                }
            } catch (IOException e) {}
            if(jacobType==1) //inv jacob only
                return;
            newtonFunc=MathPack.MatrixEqu.mulMatxToRow_s(invJacob, algSystem);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\NetBeansLogs\\MyLog.txt",true))) {
                bw.newLine();
                bw.write("Newton's method func:");
                bw.newLine();
                for(StringGraph row:getNewtonFunc()){
                    bw.write(row.toString());
                    bw.newLine();
                }
            } catch (IOException e) {}
        }
    }

    /**
     * @param inps the inps to set
     */
    public void setInps(List<MathInPin> inps) {
        ArrayList<MathInPin> inputs = new ArrayList<>();
        for(MathInPin ic:inps) inputs.add(ic);
        vars.setInputs(inputs);
    }

    public void layout(){
        File file=new File("C:\\NetBeansLogs\\XesLog.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file,true))){
            for(int i=0;i<getLogtime().size();i++){
                bw.write(getLogtime().get(i).toString()+" ");
                for(List<Double> val:getLogdata()){
                    bw.write(val.get(i).toString()+" ");
                }
                bw.newLine();
            }
        } catch (IOException e) { System.err.println(e.getMessage()); }
    }

    /**
     * @return the vars
     */
    public WorkSpace getVars() {
        return vars;
    }

    /**
     * @return the mathOuts
     */
    public List<OutputElement> getMathOuts() {
        return mathOuts;
    }

    /**
     * @param mathOuts the mathOuts to set
     */
    public void setMathOuts(List<OutputElement> mathOuts) {
        this.mathOuts = mathOuts;
    }

    /**
     * @return the dynMaths
     */
    public List<DynamMathElem> getDynMaths() {
        return dynMaths;
    }

    /**
     * @param dynMaths the dynMaths to set
     */
    public void setDynMaths(List<DynamMathElem> dynMaths) {
        this.dynMaths = dynMaths;
    }

    /**
     * @return the Jacob
     */
    public List<List<StringGraph>> getJacob() {
        return Jacob;
    }

    /**
     * @return the invJacob
     */
    public List<List<StringGraph>> getInvJacob() {
        return invJacob;
    }

    /**
     * @return the newtonFunc
     */
    public List<StringGraph> getNewtonFunc() {
        return newtonFunc;
    }

    /**
     * @return the logtime
     */
    public List<Double> getLogtime() {
        return logtime;
    }

    /**
     * @return the logdata
     */
    public List<List<Double>> getLogdata() {
        return logdata;
    }

    public int getSymbDiffRank() {
        return symbDiffRank;
    }

    public void setSymbDiffRank(int symbDiffRank) {
        this.symbDiffRank = symbDiffRank;
    }

    public List<Updatable> getUpdatableElements() {
        return updatableElements;
    }

    public void setUpdatableElements(List<Updatable> updatableElements) {
        this.updatableElements =new ArrayList<>();
        this.updatableElements.addAll(updatableElements);
    }

    class DaeToMatOut extends MathOutPin{
        private List<StringGraph> function;

        DaeToMatOut(List<StringGraph> f){
            function=f;
            //replace reference in matConnect

        }

        @Override
        public List<Double> getValue(){
            List<Double> out=new ArrayList();
            for(StringGraph sg:function)
                out.add(sg.evaluate(getVars()));
            return out; //??
        }
    }
}

