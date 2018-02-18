/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPack;

import ElementBase.MathInPin;
import ElementBase.MathOutPin;
import ElementBase.SchemeElement;
import ElementBase.Element.InitParam;
import static MathPack.MatrixEqu.rank;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Ivan
 */
public class StringFunctionSystem {
    List<LeftPart> leftSides,rightWithDiff;
    List<StringGraph> rightSides,outputFuncs;
    List<MathInPin> inputs;
    List<MathOutPin> outputs;
    List<Integer> xPryor;
    List<Double> initials=new ArrayList();
    private static int electricVariableCount =0,mechVariableCount=0,stateVarCnt=0,inputCnt=0,outputCnt=0;
    private int varCntTemp,inpCntTmp,outCntTmp;

    private static String logFile="C:\\NetBeansLogs\\MyLog.txt";
    private static boolean LOG_FLAG=true;

    public StringFunctionSystem(SchemeElement element){
        initials=new ArrayList();
        xPryor=new ArrayList();
        outputFuncs=new ArrayList();
        rightWithDiff=new ArrayList();
        String[] list=element.getStringFunction();
        for(InitParam init:element.getInitials()){
            initials.add(init.getDoubleValue());
            rightWithDiff.add(null);
            if(init.getPriority()) xPryor.add(1);
            else xPryor.add(0);
        }
        leftSides=new ArrayList();
        rightSides=new ArrayList();
        inputs=new ArrayList();
        inputs.addAll(element.getInputs());
        outputs=new ArrayList();
        outputs.addAll(element.getOutputs());
        varCntTemp=0;
        inpCntTmp=0;
        outCntTmp=0;
        for(String str:list){
            str=numerateVars(str);
            int k;
            for(k=0;k<str.length();k++){
                if(str.charAt(k)=='='){
                    break;
                }
            }
            if(str.substring(0, k).startsWith("O.")){
                outputFuncs.add(new StringGraph(str.substring(k+1, str.length())));
            }else if(str.substring(0, k).startsWith("X.")){
                int index=str.substring(0, k).lastIndexOf('.');
                index=Integer.parseInt(str.substring(index+1, k))-1;
                rightWithDiff.set(index-stateVarCnt,new LeftPart(str.substring(k+1, str.length())));
            }else{
                leftSides.add(new LeftPart(str.substring(0, k)));
                rightSides.add(new StringGraph(str.substring(k+1, str.length())));
            }
        }
        electricVariableCount +=element.getElemContactList().size();    // НЕ ФАКТ, ДЛЯ МЕХАНИКИ
        mechVariableCount += element.getMechContactList().size();
        inputCnt+=inpCntTmp;
        outputCnt+=outCntTmp;
        stateVarCnt+=varCntTemp;
    }



    public StringFunctionSystem(List<StringFunctionSystem> funcList){
        this.xPryor=new ArrayList();
        this.initials=new ArrayList();
        this.leftSides=new ArrayList();
        this.rightSides=new ArrayList();
        this.outputFuncs=new ArrayList();
        this.inputs=new ArrayList();
        this.outputs=new ArrayList();
        this.rightWithDiff=new ArrayList();

        for(StringFunctionSystem func:funcList){
            this.xPryor.addAll(func.xPryor);
            this.leftSides.addAll(func.leftSides);
            this.rightSides.addAll(func.rightSides);
            this.initials.addAll(func.initials);
            this.inputs.addAll(func.inputs);
            this.outputFuncs.addAll(func.outputFuncs);
            this.outputs.addAll(func.outputs);
            this.rightWithDiff.addAll(func.rightWithDiff);
        }
    }

    public static DAE getNumODE(List<StringFunctionSystem> list,int[][] potM,int[][] currM,int[][] speedM,int[][] torqM){
        List<List<Integer>> pots=new ArrayList();
        List<List<Integer>> currs=new ArrayList();
        List<List<Integer>> speeds=new ArrayList();
        List<List<Integer>> torqs=new ArrayList();

        List<StringGraph> potRight=new ArrayList();
        List<StringGraph> curRight=new ArrayList();
        List<StringGraph> speRight=new ArrayList();
        List<StringGraph> torRight=new ArrayList();

        DAE output=new DAE();

        //init matrx
        int i=0;
        for(int[] row:potM){
            pots.add(new ArrayList());
            for(int j:row){
                pots.get(i).add(j);
            }
            potRight.add(new StringGraph(0));
            i++;
        }
        i=0;
        for(int[] row:currM){
            currs.add(new ArrayList());
            for(int j:row){
                currs.get(i).add(j);
            }
            curRight.add(new StringGraph(0));
            i++;
        }
        i=0;
        for(int[] row:speedM){
            speeds.add(new ArrayList());
            for(int j:row){
                speeds.get(i).add(j);
            }
            speRight.add(new StringGraph(0));
            i++;
        }
        i=0;
        for(int[] row:torqM){
            torqs.add(new ArrayList());
            for(int j:row){
                torqs.get(i).add(j);
            }
            torRight.add(new StringGraph(0));
            i++;
        }
        //---end of init---

        //gather funcs
        StringFunctionSystem system=new StringFunctionSystem(list);
        output.initOutputs(system);
        output.setInps(system.getInputs());

        for(i=0;i<system.leftSides.size();i++){
            LeftPart lp=system.leftSides.get(i);
            StringGraph rp=system.rightSides.get(i);
            boolean flag=true;
            if(lp.containInstance("i.")){
                if(rp.isInvariant()){
                    List<Integer> row=new ArrayList();
                    for(int j=0;j<currs.get(0).size();j++){
                        row.add(0);
                    }
                    for(int j=0;j<lp.getNumOfVars();j++){
                        row.set(lp.getIndex(j),lp.getValue(j));
                    }
                    currs.add(row);
                    curRight.add(system.rightSides.get(i));
                    system.rightSides.remove(i);
                    system.leftSides.remove(i);
                    i--;
                }else{
                    rp.sub(new StringGraph(lp));
                }
                flag=false;
            }
            if(lp.containInstance("p.")){
                if(rp.isInvariant()){
                    List<Integer> row=new ArrayList();
                    for(int j=0;j<pots.get(0).size();j++){
                        row.add(0);
                    }
                    for(int j=0;j<lp.getNumOfVars();j++){
                        row.set(lp.getIndex(j),lp.getValue(j));
                    }
                    pots.add(row);
                    potRight.add(system.rightSides.get(i));
                    system.rightSides.remove(i);
                    system.leftSides.remove(i);
                    i--;
                }else{
                    rp.sub(new StringGraph(lp));
                }
                flag=false;
            }
            if(lp.containInstance("w.")){
                if(rp.isInvariant()){
                    List<Integer> row=new ArrayList();
                    for(int j=0;j<speeds.get(0).size();j++){
                        row.add(0);
                    }
                    for(int j=0;j<lp.getNumOfVars();j++){
                        row.set(lp.getIndex(j),lp.getValue(j));
                    }
                    speeds.add(row);
                    speRight.add(system.rightSides.get(i));
                    system.rightSides.remove(i);
                    system.leftSides.remove(i);
                    i--;
                }else{
                    rp.sub(new StringGraph(lp));
                }
                flag=false;
            }
            if(lp.containInstance("T.")){
                if(rp.isInvariant()){
                    List<Integer> row=new ArrayList();
                    for(int j=0;j<torqs.get(0).size();j++){
                        row.add(0);
                    }
                    for(int j=0;j<lp.getNumOfVars();j++){
                        row.set(lp.getIndex(j),lp.getValue(j));
                    }
                    torqs.add(row);
                    torRight.add(system.rightSides.get(i));
                    system.rightSides.remove(i);
                    system.leftSides.remove(i);
                    i--;
                }else{
                    rp.sub(new StringGraph(lp));
                }
                flag=false;
            }
            if(flag){
                rp.sub(new StringGraph(lp));
            }
        }


        if(LOG_FLAG) try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(logFile)))) {
            bw.write("Initial data");
            bw.newLine();
            int cols;
            if(pots.isEmpty())
                cols=0;
            else
                cols=pots.get(0).size();
            bw.write("Потенциалы: "+pots.size()+" out of "+cols);
            bw.newLine();
            for(int x=0;x<pots.size();x++){
                bw.write(pots.get(x).toString()+"="+potRight.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            if(currs.isEmpty())
                cols=0;
            else
                cols=currs.get(0).size();
            bw.write("Токи: "+currs.size()+" out of "+cols);
            bw.newLine();
            for(int x=0;x<currs.size();x++){
                bw.write(currs.get(x).toString()+"="+curRight.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            if(speeds.isEmpty())
                cols=0;
            else
                cols=speeds.get(0).size();
            bw.write("Скорости: "+speeds.size()+" out of "+cols);
            bw.newLine();
            for(int x=0;x<speeds.size();x++){
                bw.write(speeds.get(x).toString()+"="+speRight.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            if(torqs.isEmpty())
                cols=0;
            else
                cols=torqs.get(0).size();
            bw.write("Моменты: "+torqs.size()+" out of "+cols);
            bw.newLine();
            for(int x=0;x<torqs.size();x++){
                bw.write(torqs.get(x).toString()+"="+torRight.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            bw.write("Функции:");
            bw.newLine();
            for(int x=0;x<system.leftSides.size();x++){
                String left="0",
                        right=system.rightSides.get(x).toString();
                bw.write(left+" = "+right);
                bw.newLine();
            }
            bw.newLine();
            for(int x=0;x<system.outputFuncs.size();x++){
                String left="O."+(x+1),
                        right=system.outputFuncs.get(x).toString();
                bw.write(left+" = "+right);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        setVarsByKirhgof(pots,potRight,currs,curRight,speeds,speRight,torqs,torRight,system);
        List<StringGraph> Pmc;
        List<StringGraph> Pmp;
        List<StringGraph> Pmt;
        List<StringGraph> Pms;

        if(currs.isEmpty()) {
            Pmc = new ArrayList<>();
            Pmp = new ArrayList<>();
        } else {
            Pmc = MatrixEqu.mulDMatxToSRow(MatrixEqu.invMatr(MatrixEqu.int2dbl(currs)), curRight);
            Pmp= MatrixEqu.mulDMatxToSRow(MatrixEqu.invMatr(MatrixEqu.int2dbl(pots)), potRight);
        }
        if(torqs.isEmpty()){
            Pmt=new ArrayList<>();
            Pms=new ArrayList<>();
        } else {
            Pmt = MatrixEqu.mulDMatxToSRow(MatrixEqu.invMatr(MatrixEqu.int2dbl(torqs)), torRight);
            Pms= MatrixEqu.mulDMatxToSRow(MatrixEqu.invMatr(MatrixEqu.int2dbl(speeds)), speRight);
        }

        if(LOG_FLAG) try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
            bw.write("New data");
            bw.newLine();
            int cols;
            if(pots.isEmpty())
                cols=0;
            else
                cols=pots.get(0).size();
            bw.write("Потенциалы: "+pots.size()+" out of "+cols);
            bw.newLine();
            for(int x=0;x<pots.size();x++){
                bw.write(pots.get(x).toString()+"="+potRight.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            if(currs.isEmpty())
                cols=0;
            else
                cols=currs.get(0).size();
            bw.write("Токи: "+currs.size()+" out of "+cols);
            bw.newLine();
            for(int x=0;x<currs.size();x++){
                bw.write(currs.get(x).toString()+"="+curRight.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            if(speeds.isEmpty())
                cols=0;
            else
                cols=speeds.get(0).size();
            bw.write("Скорости: "+speeds.size()+" out of "+cols);
            bw.newLine();
            for(int x=0;x<speeds.size();x++){
                bw.write(speeds.get(x).toString()+"="+speRight.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            if(torqs.isEmpty())
                cols=0;
            else
                cols=torqs.get(0).size();
            bw.write("Моменты: "+torqs.size()+" out of "+cols);
            bw.newLine();
            for(int x=0;x<torqs.size();x++){
                bw.write(torqs.get(x).toString()+"="+torRight.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            bw.write("ODEs:");
            bw.newLine();
            for(int x=0;x<system.rightWithDiff.size();x++){
                String left="X."+(x+1), right;
                if(system.rightWithDiff.get(x)==null)
                    right="null"+"  priority: "+system.xPryor.get(x)+" initial value: "+system.initials.get(x);
                else
                    right=system.rightWithDiff.get(x).toString()+"  priority: "+system.xPryor.get(x)+" initial value: "+system.initials.get(x);
                bw.write(left+" = "+right);
                bw.newLine();
            }
            bw.newLine();
            bw.write("Pmp:");
            bw.newLine();
            for(int x=0;x<Pmp.size();x++){
                bw.write("p."+(x+1)+" = "+Pmp.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            bw.write("Pmc:");
            bw.newLine();
            for(int x=0;x<Pmc.size();x++){
                bw.write("i."+(x+1)+" = "+Pmc.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            bw.write("Pms:");
            bw.newLine();
            for(int x=0;x<Pms.size();x++){
                bw.write("w."+(x+1)+" = "+Pms.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            bw.write("Pmt:");
            bw.newLine();
            for(int x=0;x<Pmt.size();x++){
                bw.write("T."+(x+1)+" = "+Pmt.get(x).toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // Dependence check
        String bigMassage="";
        for(i=0;i<system.rightWithDiff.size();i++){
            LeftPart lp=system.rightWithDiff.get(i);
            if(lp!=null){
                StringGraph sg=new StringGraph(lp);
                // var replace
                for(int j=0;j<Pmp.size();j++) {
                    sg.replaceVariable("p." + (j + 1), Pmp.get(j));
                    sg.replaceVariable("i." + (j + 1), Pmc.get(j));
                }
                for(int j=0;j<Pms.size();j++){
                    sg.replaceVariable("w."+(j+1), Pms.get(j));
                    sg.replaceVariable("T."+(j+1), Pmt.get(j));
                }

                String message="\r\n\r\nVariable dependence: X."+(i+1)+"="+sg.toString();
                // eval new d.X.i
                StringGraph dsg=sg.getFullTimeDiffer();
                message+="\r\n\r\nNew dX."+(i+1)+"="+dsg.toString();
                for(int j=0;j<system.rightSides.size();j++){ //raplace old d.X.i
                    system.rightSides.get(j).replaceVariable("X."+(i+1), sg);
                    system.rightSides.get(j).replaceVariable("d.X."+(i+1), dsg);
                }
                System.out.println(message);
                bigMassage+=message;
            }
        }
        if(LOG_FLAG) try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
            bw.write(bigMassage);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        //vars replacement

        for(int k=0;k<system.rightSides.size();k++) {
            for (int j = 0; j < Pmp.size(); j++) {
                system.rightSides.get(k).replaceVariable("p." + (j + 1), Pmp.get(j));
                system.rightSides.get(k).replaceVariable("i." + (j + 1), Pmc.get(j));
            }
            for (int j=0;j<Pms.size();j++) {
                system.rightSides.get(k).replaceVariable("w." + (j + 1), Pms.get(j));
                system.rightSides.get(k).replaceVariable("T." + (j + 1), Pmt.get(j));
            }
        }
        for(int k=0;k<system.outputFuncs.size();k++){
            for(int j=0;j< Pmp.size();j++) {
                system.outputFuncs.get(k).replaceVariable("p." + (j + 1), Pmp.get(j));
                system.outputFuncs.get(k).replaceVariable("i." + (j + 1), Pmc.get(j));
            }
            for(int j=0;j<Pms.size();j++) {
                system.outputFuncs.get(k).replaceVariable("w." + (j + 1), Pms.get(j));
                system.outputFuncs.get(k).replaceVariable("T." + (j + 1), Pmt.get(j));
            }
        }

        //renumerate X.i and d.X.i
        i=0; //sum of deleted elems
        for(int j=0;j<system.rightWithDiff.size();j++){
            if(system.rightWithDiff.get(j)!=null){ //if depended X
                system.initials.remove(j-i);
                i++;
            }else{
                // replace
                if(i!=0){
                    for(int y=0;y<system.rightSides.size();y++){
                        system.rightSides.get(y).replaceVariable("X."+(j+1), new StringGraph("X."+(j-i+1)));
                        system.rightSides.get(y).replaceVariable("d.X."+(j+1), new StringGraph("d.X."+(j-i+1)));
                    }
                    for(int y=0;y<system.outputFuncs.size();y++){
                        system.outputFuncs.get(y).replaceVariable("X."+(j+1), new StringGraph("X."+(j-i+1)));
                    }
                }

            }
        }

        // layout
        if(LOG_FLAG) try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
            bw.newLine();
            bw.write("Замена переменных.");
            bw.newLine();
            bw.write("ODEs:");
            bw.newLine();
            for(int x=0;x<system.rightSides.size();x++){
                bw.write("0 = "+system.rightSides.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            bw.write("Xes:");
            bw.newLine();
            for(int x=0;x<system.initials.size();x++){
                String left="X."+(x+1),
                        right=" initial value: "+system.initials.get(x);
                bw.write(left+right);
                bw.newLine();
            }
            bw.newLine();
            bw.write("Outs:");
            bw.newLine();
            for(int x=0;x<system.outputFuncs.size();x++){
                bw.write("O."+(x+1)+" = "+system.outputFuncs.get(x).toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        List<StringGraph> potAlgSys=system.rightSides;
        output.setOutSystem(system.outputFuncs);
        output.setAlgSystem(potAlgSys);
        output.initXes(system.getInitsX());

        // initial guesses
        for(i=0;i<potAlgSys.size();i++){    //loop for ODEs
            StringGraph right=potAlgSys.get(i);
            Set rightSideVars=right.getVariableSet();
            for(Object obj:rightSideVars){
                String name=(String)obj;
                if(!name.startsWith("X.")&&!name.startsWith("I.")&&!name.startsWith("time")){
                    output.addVariable(name, 0.0);  //TODO maybe not 0.0
                }
            }
        }
        for(i=0;i<system.outputFuncs.size();i++){   //loop for Outs
            StringGraph right=system.outputFuncs.get(i);
            Set rightSideVars=right.getVariableSet();
            for(Object obj:rightSideVars){
                String name=(String)obj;
                if(!name.startsWith("X.")&&!name.startsWith("I.")&&!name.startsWith("time")){
                    output.addVariable(name, 0.0);
                }
            }
        }

        return output;
    }

    private static void addToWorkSpace(String varName,List<String> wrkspc,HashMap<String,StringGraph> answrs,DAE output){
        if(!wrkspc.contains(varName)){
            wrkspc.add(varName);
            StringGraph ans=answrs.get(varName);//varname mb X.1
            ans.sub(new Variable(varName));
            output.addAlgFunc(ans);
            Set rightSideVars=ans.getVariableSet();
            for(Object obj:rightSideVars){
                String name=(String)obj;
                if(name.startsWith("i.")||name.startsWith("p.")||name.startsWith("d.X."))
                    addToWorkSpace(name,wrkspc,answrs,output);
            }
        }
    }

    /**
     * Сводит функции к ODE и записывает их в list.
     * @param list список функций, содержащий функции тока и напряжения.
     * @param potM карта потенциалов
     * @param currM карта токов
     * @return true если система линейна, false если система нелинейна.
     */
    public static StringFunctionSystem MergeFunctions(List<StringFunctionSystem> list,int[][] potM,int[][] currM){

        List<List<Integer>> pots=new ArrayList();
        List<List<Integer>> currs=new ArrayList();
        List<StringGraph> potRight=new ArrayList();
        List<StringGraph> curRight=new ArrayList();

        //init matrx
        int i=0;
        for(int[] row:potM){
            pots.add(new ArrayList());
            for(int j:row){
                pots.get(i).add(j);
            }
            potRight.add(new StringGraph(0));
            i++;
        }
        i=0;
        for(int[] row:currM){
            currs.add(new ArrayList());
            for(int j:row){
                currs.get(i).add(j);
            }
            curRight.add(new StringGraph(0));
            i++;
        }
        //---end of init---

        //gather funcs
        StringFunctionSystem output=new StringFunctionSystem(list);

        //Layout
        if(LOG_FLAG) try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile))) {
            bw.write("Initial data");
            bw.newLine();
            bw.write("Потенциалы:");
            bw.newLine();
            for (int[] potM1 : potM) {
                bw.write(Arrays.toString(potM1));
                bw.newLine();
            }
            bw.newLine();
            bw.write("Токи:");
            bw.newLine();
            for (int[] currM1 : currM) {
                bw.write(Arrays.toString(currM1));
                bw.newLine();
            }
            bw.newLine();
            bw.write("Функции:");
            bw.newLine();
            for(int x=0;x<output.leftSides.size();x++){
                String left=output.leftSides.get(x).toString(),
                        right=output.rightSides.get(x).toString();
                bw.write(left+" = "+right);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        //---end of layout---


        //filtering
        int numOfVars=currM[0].length;
        int numOfPots=pots.size();
        for(LeftPart lp:output.leftSides){
            if(lp.containInstance("p.")){
                numOfPots++;
            }
        }
        int numOfCurs=currs.size();
        for(LeftPart lp:output.leftSides){
            if(lp.containInstance("i.")){
                numOfCurs++;
            }
        }

        if(numOfCurs<numOfVars){
            int iterCount=numOfVars-numOfCurs;
            int[] arr=new int[iterCount];

            for(i=0;i<iterCount;i++){

            }




            for(i=0;i<output.rightSides.size();i++){
                StringGraph right=output.rightSides.get(i);
                if(right.getCurrent(output.leftSides.get(i),numOfVars)) break;
            }
        }else if(numOfPots<numOfVars){
            for(i=0;i<output.rightSides.size();i++){
                StringGraph right=output.rightSides.get(i);
                if(right.getPotential(output.leftSides.get(i),numOfVars)) break;
            }
        }


        //Layout
        if(LOG_FLAG) try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
            bw.newLine();
            bw.write("After 'filtering' data");
            bw.newLine();
            bw.write("Функции:");
            bw.newLine();
            for(int x=0;x<output.leftSides.size();x++){
                String left=output.leftSides.get(x).toString(),
                        right=output.rightSides.get(x).toString();
                bw.write(left+" = "+right);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        //---end of layout---

        //translate funcs to pots/currs
        for(i=output.rightSides.size()-1;i>=0;i--){
            LeftPart left=output.leftSides.get(i);
            if(left.containInstance("p.")){
                List<Integer> row=new ArrayList();
                for(int j=0;j<numOfVars;j++){
                    row.add(0);
                }
                for(int j=0;j<left.getNumOfVars();j++){
                    row.set(left.getIndex(j),left.getValue(j));
                }
                pots.add(row);
                potRight.add(output.rightSides.get(i));
                output.leftSides.remove(i);
                output.rightSides.remove(i);
            }else if(left.containInstance("i.")){
                List<Integer> row=new ArrayList();
                for(int j=0;j<numOfVars;j++){
                    row.add(0);
                }
                for(int j=0;j<left.getNumOfVars();j++){
                    row.set(left.getIndex(j),left.getValue(j));
                }
                currs.add(row);
                curRight.add(output.rightSides.get(i));
                output.leftSides.remove(i);
                output.rightSides.remove(i);
            }
        }

        //Layout
        if(LOG_FLAG) try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
            bw.newLine();
            bw.write("Final data:");
            bw.newLine();
            bw.write("Потенциалы:");
            bw.newLine();
            for(int x=0;x<pots.size();x++){
                bw.write(pots.get(x).toString());
                bw.write("="+potRight.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            bw.write("Токи:");
            bw.newLine();
            for(int x=0;x<currs.size();x++){
                bw.write(currs.get(x).toString());
                bw.write("="+curRight.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            bw.write("Функции:");
            bw.newLine();
            for(int x=0;x<output.leftSides.size();x++){
                String left=output.leftSides.get(x).toString(),
                        right=output.rightSides.get(x).toString();
                bw.write(left+" = "+right);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        //---end of layout---
        if(LOG_FLAG) try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
            bw.newLine();
            bw.write("In progress...");
            bw.newLine();
        } catch (IOException e) {}
        List<StringGraph> answers=new ArrayList();
        List<String> vars=new ArrayList();

        boolean flag=true;
        while(flag){
            flag=false;
            for(i=0;i<pots.size();i++){
                List<Integer> row=pots.get(i);
                if(row==null) continue; //????
                int indx=MatrixEqu.getSingleInd(row);
                if(indx!=-1){
                    StringGraph right=potRight.get(i);
                    int add=row.get(indx);
                    right.multiplex(add);
                    flag=true;
                    //add 'var'=answ
                    String name="p."+Integer.toString(indx+1);
                    vars.add(name);
                    answers.add(new StringGraph(right));
                    if(LOG_FLAG) try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
                        bw.write(name+"="+right.toString());
                        bw.newLine();
                    } catch (IOException e) {}
                    //remove solo index row
                    pots.set(i, null);
                    //replacing
                    for(int j=0;j<pots.size();j++){
                        List<Integer> rrow=pots.get(j);
                        if(rrow!=null){
                            if(rrow.get(indx)!=0&&MatrixEqu.getSingleInd(rrow)==-1){
                                add=rrow.get(indx)*-1;
                                StringGraph temp=StringGraph.mul(right,add);
                                potRight.get(j).add(temp);
                                rrow.set(indx, 0);
                            }
                        }
                    }
//                    for(int j=0;j<numOfVars;j++){
//                        curRight.get(j).replaceVariable(name, new StringGraph(right));
//                        potRight.get(j).replaceVariable(name, new StringGraph(right));
//                    }
                    for(int j=0;j<answers.size();j++){
                        StringGraph ans=answers.get(j);
                        ans.replaceVariable(name, new StringGraph(right));
                    }//replacing ends
                }//single row case end
            }//pots end

            //currs
            for(i=0;i<currs.size();i++){
                List<Integer> row=currs.get(i);
                if(row==null) continue; //????
                int indx=MatrixEqu.getSingleInd(row);
                if(indx!=-1){
                    StringGraph right=curRight.get(i);
                    int add=row.get(indx);
                    right.multiplex(add);
                    flag=true;
                    //add 'var'=answ
                    String name="i."+Integer.toString(indx+1);
                    vars.add(name);
                    answers.add(new StringGraph(right));
                    if(LOG_FLAG) try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
                        bw.write(name+"="+right.toString());
                        bw.newLine();
                    } catch (IOException e) {}
                    //remove solo index row
                    currs.set(i, null);

                    //replacing
                    for(int j=0;j<currs.size();j++){
                        List<Integer> rrow=currs.get(j);
                        if(rrow!=null){
                            if(rrow.get(indx)!=0&&MatrixEqu.getSingleInd(rrow)==-1){
                                add=rrow.get(indx)*-1;
                                StringGraph temp=StringGraph.mul(right,add);
                                curRight.get(j).add(temp);
                                rrow.set(indx,0);
                            }
                        }
                    }
//                    for(int j=0;j<numOfVars;j++){
//                        curRight.get(j).replaceVariable(name, new StringGraph(right));
//                        potRight.get(j).replaceVariable(name, new StringGraph(right));
//                    }
                    for(int j=0;j<answers.size();j++){
                        StringGraph ans=answers.get(j);
                        ans.replaceVariable(name, new StringGraph(right));
                    }
                    //replacing ends
                }
            }//currs end
        }//end of magic

        //insert answers into dX and outs
//        for(i=vars.size()-1;i>=0;i--){
//            String name=vars.get(i);
//            StringGraph rep=answers.get(i);
//            if(checkVars(rep)){
//                for(int j=0;j<vars.size();j++){
//                    answers.get(j).replaceVariable(name, rep);
//                }
//                vars.remove(i);
//                answers.remove(i);
//            }
//        }
        for(i=0;i<vars.size();i++){
            String name=vars.get(i);
            StringGraph rep=answers.get(i);
            for(int j=0;j<vars.size();j++){
                answers.get(j).replaceVariable(name, rep);
                if(answers.get(j).contains(vars.get(j))){
                    answers.get(j).getVariable(vars.get(j),1);
                }
            }
        }

        for(i=0;i<vars.size();i++){
            String name=vars.get(i);
            for(int j=0;j<output.rightSides.size();j++){
                if(output.rightSides.get(j).contains(name)){
                    output.rightSides.get(j).replaceVariable(name, answers.get(i));
                    output.rightSides.get(j).simplify();
                }
            }
            for(int j=0;j<output.outputFuncs.size();j++){
                if(output.outputFuncs.get(j).contains(name)){
                    output.outputFuncs.get(j).replaceVariable(name, answers.get(i));
                    output.outputFuncs.get(j).simplify();
                }
            }
        }

        //Layout
        if(LOG_FLAG) try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
            bw.newLine();
            bw.write("Finish:");
            bw.newLine();
            bw.write("Потенциалы:");
            bw.newLine();
            for(int x=0;x<potM.length;x++){
                bw.write(Arrays.toString(potM[x]));
                bw.newLine();
            }
            bw.newLine();
            bw.write("Токи:");
            bw.newLine();
            for(int x=0;x<currM.length;x++){
                bw.write(Arrays.toString(currM[x]));
                bw.newLine();
            }
            bw.newLine();
            bw.write("answers:");
            bw.newLine();
            for(int j=0;j<vars.size();j++){
                String left=vars.get(j),
                        right=answers.get(j).toString();
                bw.write(left+" = "+right);
                bw.newLine();
            }
            bw.newLine();
            bw.write("Outs:");
            bw.newLine();
            for(int j=0;j<output.outputFuncs.size();j++){
                String left="O."+(j+1),
                        right=output.outputFuncs.get(j).toString();
                bw.write(left+" = "+right);
                bw.newLine();
            }
            bw.newLine();
            bw.write("ODE:");
            bw.newLine();
            for(int j=0;j<output.rightSides.size();j++){
                String left=output.leftSides.get(j).toString(),
                        right=output.rightSides.get(j).toString();
                bw.write(left+" = "+right);
                bw.newLine();
            }
        } catch (IOException e) {}




        //-------LESS OLDER MAGIC---------
//        List<LeftPart> potsLeft=new ArrayList();
//        List<LeftPart> currLeft=new ArrayList();
//        List<List<Integer>> pots=new ArrayList();
//        List<List<Integer>> currs=new ArrayList();
//
//        //init matrx
//        int i=0;
//        for(int[] row:potM){
//            pots.add(new ArrayList());
//            for(int j:row){
//                pots.get(i).add(j);
//            }
//            potsLeft.add(null);
//            i++;
//        }
//        i=0;
//        for(int[] row:currM){
//            currs.add(new ArrayList());
//            for(int j:row){
//                currs.get(i).add(j);
//            }
//            currLeft.add(null);
//            i++;
//        }
//        List<StringGraph> potRight=new ArrayList();
//        for(int j=0;j<pots.size();j++){
//            potRight.add(new StringGraph(0));
//        }
//        List<StringGraph> curRight=new ArrayList();
//        for(int j=0;j<currs.size();j++){
//            curRight.add(new StringGraph(0));
//        }
//        //---end of init---
//
//        //Layout
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile))) {
//            bw.write("Потенциалы:");
//            bw.newLine();
//            for(int x=0;x<potM.length;x++){
//                bw.write(Arrays.toString(potM[x]));
//                bw.newLine();
//            }
//            bw.newLine();
//            bw.write("Токи:");
//            bw.newLine();
//            for(int x=0;x<currM.length;x++){
//                bw.write(Arrays.toString(currM[x]));
//                bw.newLine();
//            }
//            bw.newLine();
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        }
//        //---end of layout---
//
//
//        //gather funcs
//        StringFunctionSystem output=new StringFunctionSystem(list);
//
//        List<String> vars=new ArrayList();
//        List<StringGraph> answers=new ArrayList();
//
//        boolean flag=true;
//        while(flag){
//            flag=false;
//            for(i=0;i<pots.size();i++){
//                List<Integer> row=pots.get(i);
//                if(row==null) continue;
//                int indx=MatrixEqu.getSingleInd(row);
//                if(indx!=-1){
//                    //add var and asw    'var'==answer
//                    vars.add("p."+Integer.toString(indx+1));
//                    int add=row.get(indx);
//                    StringGraph temp=potRight.get(i);
//                    temp.multiplex(add);
//                    answers.add(new StringGraph(temp));
//                    //remove solo index row
//                    pots.set(i, null);
//                    //Replace find var in other functions
//                    if(replacing("p."+(indx+1), temp, pots, potRight, currs, curRight, vars, answers, output)) flag=true;
//                }
//            }
//            for(i=0;i<currs.size();i++){
//                List<Integer> row=currs.get(i);
//                if(row==null) continue;
//                int indx=MatrixEqu.getSingleInd(row);
//                if(indx!=-1){
//                    //add var and asw    'var'==answer
//                    vars.add("i."+Integer.toString(indx+1));
//                    int add=row.get(indx);
//                    StringGraph temp=curRight.get(i);
//                    temp.multiplex(add);
//                    answers.add(new StringGraph(temp));
//                    currs.set(i, null);
//                    //Replace find var in other functions
//                    if(replacing("i."+(indx+1), temp, pots, potRight, currs, curRight, vars, answers, output)) flag=true;
//                }
//            }
//            for(i=0;i<output.leftSides.size();i++){
//                LeftPart left=output.leftSides.get(i);
//                if(left==null) continue;
//                StringGraph right=output.rightSides.get(i);
//                switch(left.getRank()){
//                    case 0:
//                        if(right.containInstance("i.")){
//                            for(int j=1;j<=electricVariableCount;j++){
//                                if(right.contains("i."+j)){
//                                    vars.add("i."+j);
//                                    right.getVariable("i."+j);
//                                    answers.add(new StringGraph(right));
//                                    // implement blacklist for improve speed
//                                    // Go srazu naxui replace!
//
//                                    output.leftSides.set(i,null);
//                                    flag=true;
//                                    break;
//                                }
//                            }
//                        }else if(right.containInstance("p.")){
//                            for(int j=1;j<=electricVariableCount;j++){
//                                if(right.contains("p."+j)){
//                                    vars.add("p."+j);
//                                    right.getVariable("p."+j);
//                                    answers.add(new StringGraph(right));
//                                    output.leftSides.set(i,null);
//                                    // Go srazu naxui replace!
//
//                                    // implement blacklist for improve speed
//
//                                    flag=true;
//                                    break;
//                                }
//                            }
//                        }else{/*??????*/}
//                        break;
//                    case 1:
//                        if(left.getName(0).contains("d.X.")) continue;
//                        String varName=left.getName(0);
//                        StringGraph tempG=StringGraph.mul(right,left.getValue(varName));
//                        output.leftSides.set(i, null);
//                        answers.add(tempG);
//                        vars.add(varName);
//                        if(replacing(varName, tempG, pots, potRight, currs, curRight, vars, answers, output)){
//                            flag=true;
//                        }
//                        //Repacing. If it happens
//
//
//                        break;
//                }
//            }
//            if(!flag){
//                for(i=0;i<vars.size();i++){
//                    StringGraph right=answers.get(i);
//                    if("0".equals(vars.get(i))){
//                        for(int j=1;j<=electricVariableCount;j++){
//                            if(right.contains("p."+j)){
//                                vars.set(i, "p."+j);
//                                right.getVariable("p."+j);
//                                if(replacing("p."+j, new StringGraph(right), pots, potRight, currs, curRight, vars, answers, output)) flag=true;
//                            }else if(right.contains("i."+j)){
//                                vars.set(i, "i."+j);
//                                right.getVariable("i."+j);
//                                if(replacing("i."+j, new StringGraph(right), pots, potRight, currs, curRight, vars, answers, output)) flag=true;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        // mejdusoboichik
//        for(i=0;i<vars.size();i++){
//            String var=vars.get(i);
//            for(int j=0;j<answers.size();j++){
//                if(i!=j&&answers.get(j).contains(var)){
//                    answers.get(j).replaceVariable(var, new StringGraph(answers.get(i)));
//                }
//            }
//        }
//        for(i=output.rightSides.size()-1;i>=0;i--){
//            if(output.leftSides.get(i)==null){
//                output.leftSides.remove(i);
//                output.rightSides.remove(i);
//            }else{
//                // mb lines w.o. dX
//                for(int k=0;k<vars.size();k++){
//                    String name=vars.get(k);
//                    if(output.rightSides.get(i).contains(name)){
//                        output.rightSides.get(i).replaceVariable(name, new StringGraph(answers.get(k)));
//                    }
//                }
//            }
//        }
//        //generate outs
//        for(i=0;i<output.outputFuncs.size();i++){
//            for(int k=0;k<vars.size();k++){
//                String name=vars.get(k);
//                if(output.outputFuncs.get(i).contains(name)){
//                    output.outputFuncs.get(i).replaceVariable(name, new StringGraph(answers.get(k)));
//                }
//            }
//        }
//
//        //Layout
//        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
//            bw.newLine();
//            bw.write("results:");
//            bw.newLine();
//            for(int j=0;j<vars.size();j++){
//                String left=vars.get(j),
//                        right=answers.get(j).toString();
//                bw.write(left+" = "+right);
//                bw.newLine();
//            }
//            bw.newLine();
//            bw.write("Outs:");
//            bw.newLine();
//            for(int j=0;j<output.outputFuncs.size();j++){
//                String left="O."+(j+1),
//                        right=output.outputFuncs.get(j).toString();
//                bw.write(left+" = "+right);
//                bw.newLine();
//            }
//            bw.newLine();
//            bw.write("ODE:");
//            bw.newLine();
//            for(int j=0;j<output.rightSides.size();j++){
//                String left=output.leftSides.get(j).getName(0),
//                        right=output.rightSides.get(j).toString();
//                bw.write(left+" = "+right);
//                bw.newLine();
//            }
//        } catch (IOException e) {}
        //-----end of less older magic----------

        //------OLD----magic----------------
//            boolean flag=true;
//            while(flag){
//                flag=false;
//
//                //Chekaem pots
//                for(int j=0;j<pots.size();j++){
//                    List<Integer> row=pots.get(j);
//                    if(row==null) continue;
//                    int indx=MatrixEqu.getSingleInd(row);
//                    if(indx!=-1){
//                        int add=row.get(indx);
//                        StringGraph temp=potRight.get(j);
//                        temp.multiplex(add);
//                        pots.set(j, null);
//                        potsLeft.set(j, new LeftPart("p."+(indx+1),indx+1));
//                        if(StringFunctionSystem.replaceVar("p."+(indx+1),temp,pots,potRight,currs,curRight,output,-1)){
//                            flag=true;
//                        }
//                    }
//                }
//
//                //Chekaem toki
//                for(int j=0;j<currs.size();j++){
//                    List<Integer> row=currs.get(j);
//                    if(row==null) continue;
//                    int indx=MatrixEqu.getSingleInd(row);
//                    if(indx!=-1){
//                        int add=row.get(indx);
//                        StringGraph tempG=curRight.get(j);
//                        tempG.multiplex(add);
//                        currs.set(j, null);
//                        currLeft.set(j, new LeftPart("i."+(indx+1),indx+1));
//                        if(StringFunctionSystem.replaceVar("i."+(indx+1),tempG,pots,potRight,currs,curRight,output,-1)){
//                            flag=true;
//                        }
//                    }
//                }
//
//                //Chekaem funcs
//                for(int j=0;j<output.leftSides.size();j++){
//                    LeftPart left=output.leftSides.get(j);
//                    if(left.getRank()==1){
//                        if(left.getName(0).contains("d.X.")) continue;
//                        String varName=left.getName(0);
//                        StringGraph tempG=StringGraph.mul(output.rightSides.get(j),left.getValue(varName));
//                        if(StringFunctionSystem.replaceVar(varName,tempG,pots,potRight,currs,curRight,output,j)){
//                            flag=true;
//                        }
//                    }
//                }
//            }
//            //poidee leftSides empty
//
//            //virazi d.X    ВЫРАЖАЙ ПО leftsides.size==0!!!!!!!!!!!!!!!
//            int numOfX=1;
//
//            try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
//                bw.write("Функции из матриц:");
//                bw.newLine();
//                for(int j=0;j<potsLeft.size();j++){
//                    String left;
//                    if(potsLeft.get(j)==null) left="0";
//                    else left=potsLeft.get(j).getName(0);
//                    bw.write(left+" = "+potRight.get(j).toString());
//                    bw.newLine();
//                }
//                for(int j=0;j<currLeft.size();j++){
//                    String left;
//                    if(currLeft.get(j)==null) left="0";
//                    else left=currLeft.get(j).getName(0);
//                    bw.write(left+" = "+curRight.get(j).toString());
//                    bw.newLine();
//                }
//                bw.newLine();
//                bw.write("Функции элементов:");
//                bw.newLine();
//                for(int H=0;H<output.leftSides.size();H++){
//                    String left;
//                    if(output.leftSides.get(H).names.isEmpty()){
//                        left="0";
//                    }else{
//                        left=output.leftSides.get(H).getName(0);
//                    }
//                    bw.write(left+" = "+output.rightSides.get(H).toString());
//                    bw.newLine();
//                }
//                bw.newLine();
//                bw.write("Функции выходов:");
//                bw.newLine();
//                for(int j=0;j<output.outputFuncs.size();j++){
//                    bw.write(output.outputFuncs.get(j).toString());
//                    bw.newLine();
//                }
//            } catch (IOException e) {}
//
//            if(output.isDynamic){
//                //gather d.X.i
//                List<StringGraph> rightSideTemp=new ArrayList();
//                for(int j=0;j<potsLeft.size();j++){
//                    LeftPart left=potsLeft.get(j);
//                    StringGraph right=potRight.get(j);
//                    if(left==null&&right.containInstance("d.X.")){
//                        rightSideTemp.add(right);
//                        numOfX++;
//                    }
//                }
//                for(int j=0;j<currLeft.size();j++){
//                    LeftPart left=currLeft.get(j);
//                    StringGraph right=curRight.get(j);
//                    if(left==null&&right.containInstance("d.X.")){
//                        rightSideTemp.add(right);
//                        numOfX++;
//                    }
//                }
//                for(int j=0;j<output.leftSides.size();j++){
//                    LeftPart left=output.leftSides.get(j);
//                    StringGraph right=output.rightSides.get(j);
//                    if(left.names.isEmpty()&&right.containInstance("d.X.")){
//                        rightSideTemp.add(right);
//                        numOfX++;
//                    }
//                }
//
//                //RESOLVE TRIANGLE SYSTEM
//                List<LeftPart> leftPartsTemp=new ArrayList();
//                List<List<String>> numOfDx=new ArrayList();
//                for(int j=0;j<rightSideTemp.size();j++){
//                    numOfDx.add(new ArrayList());
//                    leftPartsTemp.add(null);
//                    for(int k=1;k<numOfX;k++){
//                        if(rightSideTemp.get(j).contains("d.X."+k)){
//                            numOfDx.get(j).add("d.X."+k);
//                        }
//                    }
//                }
//                flag=true;
//                while(flag){
//                    flag=false;
//                    for(int j=0;j<numOfDx.size();j++){
//                        List<String> vars=numOfDx.get(j);
//                        if(vars.size()==1){         //Find alone dX/dt
//                            String name=vars.get(0);
//                            rightSideTemp.get(j).getVariable(name);  //CHECK FOR CONFLICTS
//                            leftPartsTemp.set(j,new LeftPart(name));
//                            for(int k=0;k<rightSideTemp.size();k++){    //replace finded dX/dt
//                                if(k!=j){
//                                    if(rightSideTemp.get(k).contains(name)){
//                                        rightSideTemp.get(k).replaceVariable(name, rightSideTemp.get(j));
//                                        numOfDx.get(k).remove(name);
//                                    }
//                                }
//                            }
//                            flag=true;
//                            vars.remove(0);
//                        }
//                    }
//                }
//                //Sort d.X.1 d.X.2 d.X.3....
//                output.leftSides.clear();
//                output.rightSides.clear();
//                for(int j=0;j<leftPartsTemp.size();j++){
//                    output.leftSides.add(null);
//                    output.rightSides.add(null);
//                }
//                for(int j=0;j<leftPartsTemp.size();j++){
//                    String name=leftPartsTemp.get(j).getName(0);
//                    int indx=name.lastIndexOf('.')+1;
//                    String nume=name.substring(indx);
//                    indx=Integer.parseInt(nume)-1;
//                    output.leftSides.set(indx, leftPartsTemp.get(j));
//                    output.rightSides.set(indx, rightSideTemp.get(j));
//                }

//                for(List<String> vals:numOfDx){
//                    if(vals.size()>0){
//    //                    raschetkz.RaschetKz.layoutString("Ti cho pes, tyt not minimal system");
//                    }
//                }
//
//            }
//
//
//
////            for(int j=output.leftSides.size()-1;j>=0;j--){
////                if(output.leftSides.get(j).names.isEmpty()){
////                    flag=true;
////                    numOfX++;
////                }else{
////                    output.rightSides.remove(j);
////                    output.leftSides.remove(j);
////                }
////            }
//
//
//
//            //LOG OUT
//            try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
//                bw.newLine();
//                bw.write("ODE:");
//                bw.newLine();
//                for(int j=0;j<output.rightSides.size();j++){
//                    bw.write(output.leftSides.get(j).getName(0)+" = "+output.rightSides.get(j).toString());
//                    bw.newLine();
//                }
//            } catch (IOException e) {}
//
////            //delete useless already done
////            int[] ii= new int[numOfX-1]; //for sorting
////            int iii=0;
////            for(int j=output.rightSides.size()-1;j>=0;j--){
////                for(int k=1;k<numOfX;k++){
////                    if(output.rightSides.get(j).contains("d.X."+k)){
////                        output.rightSides.get(j).getVariable("d.X."+k);  //CHECK FOR CONFLICTS
////                        output.leftSides.set(j,new LeftPart("d.X."+k));
////                        ii[iii]=k;
////                        iii++;
////                    }
////                }
////            }
//
//
////            for(int j=0;j<output.outputFuncs.size();j++){
////                System.err.println(output.outputFuncs.get(j).asString());
////            }
//
//            //Outputs
//            for(int j=0;j<output.outputFuncs.size();j++){
//                for(int k=1;k<numOfX;k++){
//                    if(output.outputFuncs.get(j).contains("d.X."+k)){
//                        output.outputFuncs.get(j).replaceVariable("d.X."+k, output.rightSides.get(k-1));
//                    }
//                }
//            }
//
//             try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
//                 bw.newLine();
//                 bw.write("Выходы:");
//                 bw.newLine();
//                 for(int j=0;j<output.outputFuncs.size();j++){
//                     bw.write(output.outputFuncs.get(j).toString());
//                     bw.newLine();
//                 }
//             } catch (IOException e) {}
        // ----------OLD-----END---------------

        return output;
    }

    public static void initVarCount(){
        electricVariableCount = 0;
        mechVariableCount = 0;
        stateVarCnt=0;
        inputCnt=0;
        outputCnt=0;
    }

    public List<MathOutPin> getOutputs(){
        return outputs;
    }

    public List<StringGraph> getOutFuncs(){
        return outputFuncs;
    }

    static private void setVarsByKirhgof(List<List<Integer>> pots,List<StringGraph> potRight,
                                         List<List<Integer>> currs,List<StringGraph> curRight,
                                         List<List<Integer>> speeds,List<StringGraph> speRight,
                                         List<List<Integer>> torqs,List<StringGraph> torRight,
                                         StringFunctionSystem sys){
        int nRowP=pots.size(),nCols,
                nRowC=currs.size(),
                nRowS=speeds.size(),nColsMech,
                nRowT=torqs.size(),
                nOfX=sys.rightWithDiff.size();

        if(nRowP==0)
            nCols=0;
        else
            nCols=pots.get(0).size();
        if(nRowS==0)
            nColsMech=0;
        else
            nColsMech=speeds.get(0).size();
        //pots part
        if(!sys.xPryor.isEmpty()){
            List<Integer> xLines,xMechLines;
            boolean potFailure=false,potExistence=false,
                    curFailure=false,curExistence=false,
                    speFailure=false,speExistence=false,
                    torFailure=false,torExistence=false;
            //init
            for(int i=0;i<nOfX;i++){
                if(sys.rightWithDiff.get(i)!=null)
                    if(sys.rightWithDiff.get(i).containInstance("p.")){
                        potFailure=potExistence=true;
                    }else if(sys.rightWithDiff.get(i).containInstance("i.")){
                        curFailure=curExistence=true;
                    }else if(sys.rightWithDiff.get(i).containInstance("w.")){
                        speFailure=speExistence=true;
                    }else if(sys.rightWithDiff.get(i).containInstance("T.")){
                        torFailure=torExistence=true;
                    }else
                        throw new Error("Hmmm, X.i=xzxz");
            }
            if(nRowP==nCols&&potExistence){ // check X.i=f(t)  // TODO not sure about this
                throw new Error("Something connected in parallel with voltage source!");
            }
            if(nRowC==nCols&&curExistence){
                throw new Error("Something connected in series with current source!");
            }
            if(nRowS==nColsMech&&speExistence){ // check X.i=f(t)
                throw new Error("Something connected in parallel with speed source!");
            }
            if(nRowT==nColsMech&&torExistence){
                throw new Error("Something connected in series with torque source!");
            }
            //?
            for(int prior=1;prior>-1;prior--){ // cycle by priorities
                for(int i=0;i<sys.rightWithDiff.size();i++){
                    LeftPart lp=sys.rightWithDiff.get(i);
                    if(lp!=null)
                        if(sys.xPryor.get(i)==prior){
                            xLines=new ArrayList(nCols);
                            for(int j=0;j<nCols;j++){
                                xLines.add(0);
                            }
                            xMechLines=new ArrayList(nColsMech);
                            for(int j=0;j<nColsMech;j++){
                                xMechLines.add(0);
                            }
                            if(lp.containInstance("p.")&&potExistence){
                                for(int j=0;j<lp.getNumOfVars();j++){
                                    xLines.set(lp.getIndex(j),lp.getValue(j));
                                }
                                pots.add(xLines);
                                if(rank(pots)==pots.size()){ //all good
                                    potRight.add(new StringGraph("X."+(i+1))); //is right index???????
                                    sys.rightWithDiff.set(i, null);
                                    potFailure=false;
                                    if(pots.size()==nCols)
                                        potExistence=false;
                                }else{ //depended var
                                    System.err.println("X."+(i+1)+" depends on something(pot)!");
                                    pots.remove(xLines);
                                }

                            }else if(lp.containInstance("i.")&&curExistence){
                                for(int j=0;j<lp.getNumOfVars();j++){
                                    xLines.set(lp.getIndex(j),lp.getValue(j));
                                }
                                currs.add(xLines);
                                if(rank(currs)==currs.size()){ //all good
                                    curRight.add(new StringGraph("X."+(i+1))); //is right index???????
                                    sys.rightWithDiff.set(i, null);
                                    curFailure=false;
                                    if(currs.size()==nCols)
                                        curExistence=false;
                                }else{ //depended var
                                    System.err.println("X."+(i+1)+" depends on something(cur)!");
                                    currs.remove(xLines);
                                }
                            }else if(lp.containInstance("w.")&&speExistence){
                                for(int j=0;j<lp.getNumOfVars();j++){
                                    xMechLines.set(lp.getIndex(j),lp.getValue(j));
                                }
                                speeds.add(xMechLines);
                                if(rank(speeds)==speeds.size()){ //all good
                                    speRight.add(new StringGraph("X."+(i+1))); //is right index???????
                                    sys.rightWithDiff.set(i, null);
                                    speFailure=false;
                                    if(speeds.size()==nColsMech)
                                        speExistence=false;
                                }else{ //depended var
                                    System.err.println("X."+(i+1)+" depends on something(speed)!");
                                    speeds.remove(xMechLines);
                                }
                            }else if(lp.containInstance("T.")&&torExistence){
                                for(int j=0;j<lp.getNumOfVars();j++){
                                    xMechLines.set(lp.getIndex(j),lp.getValue(j));
                                }
                                torqs.add(xMechLines);
                                if(rank(torqs)==torqs.size()){ //all good
                                    torRight.add(new StringGraph("X."+(i+1))); //is right index???????
                                    sys.rightWithDiff.set(i, null);
                                    torFailure=false;
                                    if(torqs.size()==nColsMech)
                                        torExistence=false;
                                }else{ //depended var
                                    System.err.println("X."+(i+1)+" depends on something(torq)!");
                                    torqs.remove(xMechLines);
                                }
                            }
                        }
                }
            }
            if(curFailure) throw new Error("Strange, all curs depends");
            if(potFailure) throw new Error("Strange, all pots depends");
            if(speFailure) throw new Error("Strange, all speeds depends");
            if(torFailure) throw new Error("Strange, all torques depends");
        }

        // test layout
        if(LOG_FLAG) try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile,true))) {
            bw.write("After Xes data");
            bw.newLine();
            int cols;
            if(pots.isEmpty())
                cols=0;
            else
                cols=pots.get(0).size();
            bw.write("Потенциалы: "+pots.size()+" out of "+cols);
            bw.newLine();
            for(int x=0;x<pots.size();x++){
                bw.write(pots.get(x).toString()+"="+potRight.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            if(currs.isEmpty())
                cols=0;
            else
                cols=currs.get(0).size();
            bw.write("Токи: "+currs.size()+" out of "+cols);
            bw.newLine();
            for(int x=0;x<currs.size();x++){
                bw.write(currs.get(x).toString()+"="+curRight.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            if(speeds.isEmpty())
                cols=0;
            else
                cols=speeds.get(0).size();
            bw.write("Speeds: "+speeds.size()+" out of "+cols);
            bw.newLine();
            for(int x=0;x<speeds.size();x++){
                bw.write(speeds.get(x).toString()+"="+speRight.get(x).toString());
                bw.newLine();
            }
            bw.newLine();
            if(torqs.isEmpty())
                cols=0;
            else
                cols=torqs.get(0).size();
            bw.write("Torques: "+torqs.size()+" out of "+cols);
            bw.newLine();
            for(int x=0;x<torqs.size();x++){
                bw.write(torqs.get(x).toString()+"="+torRight.get(x).toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        //casual
        setVarsByKirhgof(pots,potRight,currs,curRight,speeds,speRight,torqs,torRight);
    }

    static private void setVarsByKirhgof(List<List<Integer>> pots,List<StringGraph> potRight,
                                         List<List<Integer>> currs,List<StringGraph> curRight,
                                         List<List<Integer>> speeds,List<StringGraph> speRight,
                                         List<List<Integer>> torqs,List<StringGraph> torRight){
        List<List<Integer>> C=new ArrayList();
        boolean badCurs=true,badPots=true,
                badTorqs=true,badSpeeds=true;
        int numOfCurs=currs.size(),numOfVars;
        if(numOfCurs==0)
            numOfVars=0;
        else
            numOfVars=currs.get(0).size();
        //curs
        if(numOfVars!=numOfCurs){
            MathPack.Combinatorics.getCombinations(C, 0, numOfVars, numOfVars-numOfCurs);
            for(List<Integer> exesRows:C){      //try all combinations
                for(Integer i:exesRows){
                    List<Integer> row=new ArrayList();
                    for(int j=0;j<numOfVars;j++){
                        row.add(0);
                    }
                    row.set(i,1);
                    currs.add(row);
                }
//                if(MatrixEqu.det_i(currs)==0){
                if(rank(currs)!=currs.size()){
                    for(int i=0;i<exesRows.size();i++){
                        currs.remove(currs.size()-1);
                    }
                }else{
                    for(int i=0;i<exesRows.size();i++){
                        curRight.add(new StringGraph("Cur."+(i+1)));
                    }
                    badCurs=false;
                    break;
                }
            }
            if(badCurs)   throw new Error("AAAAAA, bad curr in Kirghoff");
        }
        //pots
        C=new ArrayList();
        int numOfPots=pots.size();
        if(numOfVars!=numOfPots){
            MathPack.Combinatorics.getCombinations(C, 0, numOfVars, numOfVars-numOfPots);
            for(List<Integer> exesRows:C){      //try all combinations
                for(Integer i:exesRows){
                    List<Integer> row=new ArrayList();
                    for(int j=0;j<numOfVars;j++){
                        row.add(0);
                    }
                    row.set(i,1);
                    pots.add(row);
                }
//                if(MatrixEqu.det_i(pots)==0){
                if(rank(pots)!=pots.size()){
                    for(int i=0;i<exesRows.size();i++){
                        pots.remove(pots.size()-1);
                    }
                }else{
                    for(int i=0;i<exesRows.size();i++){
                        potRight.add(new StringGraph("Pot."+(i+1)));
                    }
                    badPots=false;
                    break;
                }
            }
            if(badPots)   throw new Error("AAAAAA, bad pots in Kirghoff");
        }
        //speeds
        C=new ArrayList();
        int numOfSpeeds=speeds.size();
        if(numOfSpeeds==0)
            numOfVars=0;
        else
            numOfVars=speeds.get(0).size();
        if(numOfVars!=numOfSpeeds){
            MathPack.Combinatorics.getCombinations(C, 0, numOfVars, numOfVars-numOfSpeeds);
            for(List<Integer> exesRows:C){      //try all combinations
                for(Integer i:exesRows){
                    List<Integer> row=new ArrayList();
                    for(int j=0;j<numOfVars;j++){
                        row.add(0);
                    }
                    row.set(i,1);
                    speeds.add(row);
                }
//                if(MatrixEqu.det_i(speeds)==0){
                if(rank(speeds)!=speeds.size()){
                    for(int i=0;i<exesRows.size();i++){
                        speeds.remove(speeds.size()-1);
                    }
                }else{
                    for(int i=0;i<exesRows.size();i++){
                        speRight.add(new StringGraph("Spe."+(i+1)));
                    }
                    badSpeeds=false;
                    break;
                }
            }
            if(badSpeeds)   throw new Error("AAAAAA, bad speeds in Kirghoff");
        }
        //torqs
        C=new ArrayList();
        int numOfTorqs=torqs.size();
        if(numOfVars!=numOfTorqs){
            MathPack.Combinatorics.getCombinations(C, 0, numOfVars, numOfVars-numOfTorqs);
            for(List<Integer> exesRows:C){      //try all combinations
                for(Integer i:exesRows){
                    List<Integer> row=new ArrayList();
                    for(int j=0;j<numOfVars;j++){
                        row.add(0);
                    }
                    row.set(i,1);
                    torqs.add(row);
                }
//                if(MatrixEqu.det_i(torqs)==0){
                if(rank(torqs)!=torqs.size()){
                    for(int i=0;i<exesRows.size();i++){
                        torqs.remove(torqs.size()-1);
                    }
                }else{
                    for(int i=0;i<exesRows.size();i++){
                        torRight.add(new StringGraph("Tor."+(i+1)));
                    }
                    badTorqs=false;
                    break;
                }
            }
            if(badTorqs)   throw new Error("AAAAAA, bad torques in Kirghoff");
        }
    }

    public List<MathInPin> getInputs(){
        return inputs;
    }

    public List<Double> getInitsX(){
        return initials;
    }

    public List<Integer> getXpryority(){
        return xPryor;
    }

    /**
     * Check for inp=f(X,t)
     * @param input
     * @return true if inp=f(X,t)
     */
    private static boolean checkVars(StringGraph input){
//        return !(input.containInstance("i.")||input.containInstance("p.")||input.containInstance("d.X."));
        return !(input.containInstance("i.")||input.containInstance("p."));
    }

    /**
     *
     * @param input
     * @return
     */
    private String numerateVars(String input){
        //-----phi and curr----------
        int ind,length,startIndx=0,maxX=-1,maxInp=-1,maxOut=-1;
        length=input.length();
        for(int i=0;i<length;i++){
            char c=input.charAt(i);
            switch(c){
                case 'p':
                    if(input.charAt(++i)=='.'){
                        startIndx=++i;
                        String temp="";
                        for(;i<length;i++){
                            c=input.charAt(i);
                            if(StringGraph.isOperand(input,i)||c=='='||c==','||c==')'){
                                ind=Integer.parseInt(temp)+ electricVariableCount;
                                input=input.substring(0, startIndx)+Integer.toString(ind)+input.substring(i);
                                temp="";
                                break;
                            }else{
                                temp+=c;
                            }
                        }
                        if(!temp.isEmpty()){
                            ind=Integer.parseInt(temp)+ electricVariableCount;
                            input=input.substring(0, startIndx)+Integer.toString(ind);
                        }
                    }
                    break;
                case 'i':
                    if(input.charAt(++i)=='.'){
                        startIndx=++i;
                        String temp="";
                        for(;i<length;i++){
                            c=input.charAt(i);
                            if(StringGraph.isOperand(input,i)||c=='='||c==','||c==')'){
                                ind=Integer.parseInt(temp)+ electricVariableCount;
                                input=input.substring(0, startIndx)+Integer.toString(ind)+input.substring(i);
                                temp="";
                                break;
                            }else{
                                temp+=c;
                            }
                        }
                        if(!temp.isEmpty()){
                            ind=Integer.parseInt(temp)+ electricVariableCount;
                            input=input.substring(0, startIndx)+Integer.toString(ind);
                        }
                    }
                    break;
                case 'w':
                    if(input.charAt(++i)=='.'){
                        startIndx=++i;
                        String temp="";
                        for(;i<length;i++){
                            c=input.charAt(i);
                            if(StringGraph.isOperand(input,i)||c=='='||c==','||c==')'){
                                ind=Integer.parseInt(temp)+ mechVariableCount;
                                input=input.substring(0, startIndx)+Integer.toString(ind)+input.substring(i);
                                temp="";
                                break;
                            }else{
                                temp+=c;
                            }
                        }
                        if(!temp.isEmpty()){
                            ind=Integer.parseInt(temp)+ mechVariableCount;
                            input=input.substring(0, startIndx)+Integer.toString(ind);
                        }
                    }
                    break;
                case 'T':
                    if(input.charAt(++i)=='.'){
                        startIndx=++i;
                        String temp="";
                        for(;i<length;i++){
                            c=input.charAt(i);
                            if(StringGraph.isOperand(input,i)||c=='='||c==','||c==')'){
                                ind=Integer.parseInt(temp)+ mechVariableCount;
                                input=input.substring(0, startIndx)+Integer.toString(ind)+input.substring(i);
                                temp="";
                                break;
                            }else{
                                temp+=c;
                            }
                        }
                        if(!temp.isEmpty()){
                            ind=Integer.parseInt(temp)+ mechVariableCount;
                            input=input.substring(0, startIndx)+Integer.toString(ind);
                        }
                    }
                    break;
                case 'X':
                    if(input.charAt(++i)=='.'){
                        startIndx=++i;
                        String temp="";
                        for(;i<length;i++){
                            c=input.charAt(i);
                            if(StringGraph.isOperand(input,i)||c=='='||c==','||c==')'){
                                ind=Integer.parseInt(temp);
                                if(ind>maxX) maxX=ind;
                                ind+=stateVarCnt;
                                input=input.substring(0, startIndx)+Integer.toString(ind)+input.substring(i);
                                temp="";

                                break;
                            }else{
                                temp+=c;
                            }
                        }
                        if(!temp.isEmpty()){
                            ind=Integer.parseInt(temp);
                            if(ind>maxX) maxX=ind;
                            ind+=stateVarCnt;
                            input=input.substring(0, startIndx)+Integer.toString(ind);

                        }
                    }
                    break;
                case 'O':
                    if(input.charAt(++i)=='.'){
                        startIndx=++i;
                        String temp="";
                        for(;i<length;i++){
                            c=input.charAt(i);
                            if(StringGraph.isOperand(input,i)||c=='='||c==','||c==')'){
                                ind=Integer.parseInt(temp);
                                if(ind>maxOut) maxOut=ind;
                                ind+=outputCnt;
                                input=input.substring(0, startIndx)+Integer.toString(ind)+input.substring(i);
                                temp="";
                                break;
                            }else{
                                temp+=c;
                            }
                        }
                        if(!temp.isEmpty()){
                            ind=Integer.parseInt(temp);
                            if(ind>maxOut) maxOut=ind;
                            ind+=outputCnt;
                            input=input.substring(0, startIndx)+Integer.toString(ind);

                        }
                    }
                    break;
                case 'I':
                    if(input.charAt(++i)=='.'){
                        startIndx=++i;
                        String temp="";
                        for(;i<length;i++){
                            c=input.charAt(i);
                            if(StringGraph.isOperand(input,i)||c=='='||c==','||c==')'){
                                ind=Integer.parseInt(temp);
                                if(ind>maxInp) maxInp=ind;
                                ind+=inputCnt;
                                input=input.substring(0, startIndx)+Integer.toString(ind)+input.substring(i);
                                temp="";
                                break;
                            }else{
                                temp+=c;
                            }
                        }
                        if(!temp.isEmpty()){
                            ind=Integer.parseInt(temp);
                            if(ind>maxInp) maxInp=ind;
                            ind+=inputCnt;
                            input=input.substring(0, startIndx)+Integer.toString(ind);

                        }
                    }
                    break;
            }
            length=input.length();
        }
        if(maxX>varCntTemp) varCntTemp=maxX;
        if(maxOut>outCntTmp) outCntTmp=maxOut;
        if(maxInp>inpCntTmp) inpCntTmp=maxInp;
        return input;
    }

    /**
     * New implementation. For huge nonlinear ODEs.
     * @param varName
     * @param replacement
     * @param potM
     * @param potRight
     * @param currM
     * @param curRight
     * @param answVarName
     * @param answers
     * @param gathered
     * @return
     */
    private static boolean replacing(String varName,
                                     StringGraph replacement,
                                     List<List<Integer>> potM,
                                     List<StringGraph> potRight,
                                     List<List<Integer>> currM,
                                     List<StringGraph> curRight,
                                     List<String> answVarName,
                                     List<StringGraph> answers,
                                     StringFunctionSystem gathered)
    {
        boolean output=false;
        int varIndex=Integer.parseInt(varName.substring(varName.lastIndexOf('.')+1))-1;
        if(varName.charAt(0)=='p'){
            //Replace variable in pot Matrix
            for(int i=0;i<potM.size();i++){     //check rows
                List<Integer> row=potM.get(i);
                if(row==null) continue;
                if(row.get(varIndex)!=0){
                    if(MatrixEqu.getSingleInd(row)!=-1){ //if alone index
                        int add=row.get(varIndex)*-1;
                        StringGraph replace=StringGraph.mul(replacement,add);
                        replace.add(potRight.get(i));
                        answVarName.add("0");
                        answers.add(replace);
                        potM.set(i, null);
                        output=true;
                    }else{
                        int add=row.get(varIndex)*-1;
                        StringGraph replace=StringGraph.mul(replacement,add);
                        potRight.get(i).add(replace);
                        row.set(varIndex, 0);
                        output=true;
                        // TRY TO IMPLEMENT RECURCIVE!!
                    }
                }
            }
        }else{
            //Replace variable in curr Matrix
            for(int i=0;i<currM.size();i++){     //check rows
                List<Integer> row=currM.get(i);
                if(row==null) continue;
                if(row.get(varIndex)!=0){
                    if(MatrixEqu.getSingleInd(row)!=-1){ //if alone index
                        int add=row.get(varIndex)*-1;
                        StringGraph replace=StringGraph.mul(replacement,add);
                        replace.add(curRight.get(i));
                        answVarName.add("0");
                        answers.add(replace);
                        currM.set(i, null);
                        output=true;
                    }else{
                        int add=row.get(varIndex)*-1;
                        StringGraph replace=StringGraph.mul(replacement,add);
                        curRight.get(i).add(replace);
                        row.set(varIndex, 0);
                        output=true;
                        // TRY TO IMPLEMENT RECURCIVE!!
                    }
                }
            }
        }
        for(int i=0;i<gathered.leftSides.size();i++){
            LeftPart left=gathered.leftSides.get(i);
            StringGraph right=gathered.rightSides.get(i);
            if(left==null) continue;
            if(left.getNumOfVars()==2&&left.contain(varName)){
                int add=left.getValue(varName)*-1;
                left.remove(varName);
                StringGraph replace=StringGraph.mul(replacement,add);
                right.add(replace);
                //right.replaceVariable(varName, replacement);
                output=true;
            }
        }


        return output;
    }

    /**
     * Заменяет все var в системе функций и удаляет строки.
     * @param varName
     * @param replacement
     * @param potM
     * @param potR
     * @param currM
     * @param curR
     * @param gathered
     * @param forbidden
     * @return
     */
    private static boolean replaceVar(String varName,
                                      StringGraph replacement,
                                      List<List<Integer>> potM,
                                      List<StringGraph> potR,
                                      List<List<Integer>> currM,
                                      List<StringGraph> curR,
                                      StringFunctionSystem gathered,int forbidden)
    {
        boolean output=false;
        int varIndex=Integer.parseInt(varName.substring(2))-1;  // NE 2=const Nixiya!!!!!   inp.N opapapa
        if(varName.charAt(0)=='p'){
            //Replace variable in pot Matrix
            for(int i=0;i<potM.size();i++){     //check rows
                List<Integer> row=potM.get(i);
                if(row==null) continue;
                if(row.get(varIndex)!=0){
                    int add=row.get(varIndex)*-1;
                    StringGraph replace=StringGraph.mul(replacement,add);
                    potR.get(i).add(replace);
                    output=true;
                    if(MatrixEqu.getSingleInd(row)==-1){
                        row.set(varIndex, 0);
                    }else{
                        potM.set(i, null);
                    }
                }
            }
        }else{
            //Replace variable in curr Matrix
            for(int i=0;i<currM.size();i++){     //check rows
                List<Integer> row=currM.get(i);
                if(row==null) continue;
                if(row.get(varIndex)!=0){
                    int add=row.get(varIndex)*-1;
                    StringGraph replace=StringGraph.mul(replacement,add);
                    curR.get(i).add(replace);
                    output=true;
                    if(MatrixEqu.getSingleInd(row)==-1){
                        row.set(varIndex, 0);
                    }else{
                        currM.set(i,null);
                    }
                }
            }
        }

        for(StringGraph right:potR){
            if(right.contains(varName)){
                right.replaceVariable(varName, replacement);
                output=true;
            }
        }
        for(StringGraph right:curR){
            if(right.contains(varName)){
                right.replaceVariable(varName, replacement);
                output=true;
            }
        }

        // Cheсkaem funkcii
        //label:
        for(int i=0;i<gathered.leftSides.size();i++){
            LeftPart left=gathered.leftSides.get(i);
            StringGraph right=gathered.rightSides.get(i);

            if(left.contain(varName)){
                if(i==forbidden) continue;   //DO NOT WORKs

//                for(int k=stateVarCnt;k>=0;k--){
//                    if(replacement.contains("d.X."+k)){
//                        for(int h=stateVarCnt;h>=0;h--){
//                            if(right.contains("d.X."+h)){
//                                continue label;
//                            }
//                        }
//                    }
//                }

                int add=left.getValue(varName)*-1;
                left.remove(varName);
                StringGraph replace=StringGraph.mul(replacement,add);
                right.add(replace);
                right.replaceVariable(varName, replacement);
                output=true;
            }else if(right.contains(varName)){
                right.replaceVariable(varName, replacement);
                output=true;
            }
        }

        //Chekaem outputsi
        for(int i=0;i<gathered.outputFuncs.size();i++){
            StringGraph func=gathered.outputFuncs.get(i);
            if(func.contains(varName))
                func.replaceVariable(varName, replacement);
        }

        return output;
    }

//    /**
//     *
//     * @param time
//     * @param X
//     * @param extInput external inputs from math elements
//     * @return Vector = d.X/dt = F(X,t).
//     */
//    public List<Double> evaluale(WorkSpace vars,List<MathInPin> extInput){
//        List<Double> output=new ArrayList();
//        for(StringGraph sg:rightSides){
//            output.add(sg.evaluate(vars, extInput));
//        }
//        return output;
//    }
}

class LeftPart{
    List<Integer> indexs;
    List<Integer> gains;
    List<Variable> names;

    LeftPart(String str){
        indexs=new ArrayList();
        names=new ArrayList();
        gains=new ArrayList();
        String temp="";
        int gain=1;
        for(char c:str.toCharArray()){
            switch (c) {
                case '+':
                    names.add(new Variable(temp));
                    int i=temp.lastIndexOf('.');
                    indexs.add(Integer.parseInt(temp.substring(i+1))-1);   // NOT CONSTANT VALUE 2!!!!!!!!
                    gains.add(gain);
                    temp="";
                    gain=1;
                    break;
                case '-':
                    if(!temp.isEmpty()){
                        names.add(new Variable(temp));
                        i=temp.lastIndexOf('.');
                        indexs.add(Integer.parseInt(temp.substring(i+1))-1);
                        gains.add(gain);
                        temp="";
                    }
                    gain=-1;
                    break;
                default:
                    temp+=c;
                    break;
            }
        }
        if(!temp.isEmpty()){
            names.add(new Variable(temp));
            indexs.add(Integer.parseInt(temp.substring(temp.lastIndexOf('.')+1))-1);
            gains.add(gain);
        }
    }

    LeftPart(String name,int add){
        indexs=new ArrayList();
        names=new ArrayList();
        gains=new ArrayList();
        names.add(new Variable(name));
        gains.add(add);
        String tmp=name.substring(name.lastIndexOf('.')+1);
        indexs.add(Integer.parseInt(tmp)-1);
    }


    int getNumOfVars(){
        return names.size();
    }

    void clear(){
        this.indexs.clear();
        this.names.clear();
        this.gains.clear();
    }

    StringGraph toFormula(){
        return new StringGraph(this.toString());
    }

    boolean isEmpty(){
        return indexs.isEmpty()&&names.isEmpty()&&gains.isEmpty();
    }

    void add(String name,int gain){
        this.gains.add(gain);
        this.names.add(new Variable(name));
        this.indexs.add(Integer.parseInt(name.substring(name.lastIndexOf('.')+1))-1);
    }

    int getValue(String name){
        int ind=names.indexOf(name);
        return gains.get(ind);
    }

    int getValue(int ind){
        return gains.get(ind);
    }

    Variable getVar(int ind){
        return this.names.get(ind);
    }

    String getName(int ind){
        return this.names.get(ind).toString();
    }

    int getIndex(int ind){
        return this.indexs.get(ind);
    }

    int getIndex(String name){
        int ind=names.indexOf(name);
        return indexs.get(ind);
    }

    void remove(String name){
        int ind=this.names.indexOf(name);
        this.indexs.remove(ind);
        this.names.remove(ind);
        this.gains.remove(ind);
    }

    /**
     * full name needed
     * @param name
     * @return
     */
    boolean contain(String name){
        for(int i=0;i<names.size();i++){
            Variable var=names.get(i);
            if(var.getName().equals(name))
                return true;
        }
        return false;
    }

    boolean containInstance(String name){
        for(int i=0;i<names.size();i++){
            //String nam=names.get(i).substring(0, names.get(i).lastIndexOf('.')+1);
            String nam=names.get(i).getShortName();
            if(nam.equals(name)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString(){
        String str="";
        for(int i=0;i<names.size();i++){
            if(gains.get(i)==1)  str+="+";
            else if(gains.get(i)==-1) str+="-";
            str+=names.get(i).getName();
        }
        return str;
    }
}
