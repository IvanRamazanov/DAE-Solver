/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPackODE;

import ElementBase.*;
import MathPack.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import raschetkz.ModelState;

/**
 *
 * @author Ivan
 */
abstract public class Solver {
    protected List<StringGraph> newtonFunc;
    protected List<OutputElement> mathOuts;
    protected List<DynamMathElem> mathDynamics;
    protected List<List<StringGraph>> symbJacobian,invJacob;
    protected List<StringGraph> algSystem;
    protected WorkSpace vars;
    protected DAE dae;
    protected List<MathInPin> inps;
    protected ArrayList<WorkSpace.Variable> commonVarsVector,Xvector,dXvector;
    protected int jacobEstType,diffRank;
    protected double[]
            //s,
            vals;
    private double[][] J;
    protected int[] ind;
    public static double dt, time, tEnd, absTol, relTol;
    public static SimpleDoubleProperty progress=new SimpleDoubleProperty();
    protected Task cancelFlag;

    abstract public void evalNextStep();

    public void init(DAE daeSys,ModelState state, Task tsk){
        time=0;
        tEnd=state.getTend().doubleValue();
        progress.setValue(0.0);
        dae=daeSys;
        vars=daeSys.getVars();
        inps=daeSys.getInps();
        symbJacobian=daeSys.getJacob();
        invJacob=daeSys.getInvJacob();
        newtonFunc=daeSys.getNewtonFunc();
        algSystem=daeSys.getAlgSystem();
        mathOuts=daeSys.getMathOuts();
        mathDynamics=daeSys.getDynMaths();
        cancelFlag=tsk;

        dt=state.getDt().doubleValue();
        absTol=state.getAbsTol().doubleValue();
        relTol=state.getRelTol().doubleValue();

        vals=new double[algSystem.size()];
        J=new double[algSystem.size()][algSystem.size()];

        ind=new int[algSystem.size()];
        commonVarsVector=new ArrayList<>();
        dXvector=new ArrayList<>();
        Xvector=new ArrayList<>();

        for(WorkSpace.Variable var:vars.getVarList()){
            if(!var.getName().startsWith("X.")){
                commonVarsVector.add(var);
                if(var.getName().startsWith("d.X."))
                    dXvector.add(var);
            }else{
                Xvector.add(var);
            }
        }
        diffRank=dae.getSymbDiffRank();

        for(SchemeElement elem:state.getSchemeElements()){
            elem.init();
        }
        for(Updatable elem:dae.getUpdatableElements()){
            ((MathElement)elem).init();
        }
        for(DynamMathElem del:mathDynamics){
            del.init();
//            mathDiffRank+=del.getRank();
//            for(int i=0;i<del.getX0().size();i++){
                Element.VectorParameter vp=del.getX0();
                int i=0;
                for(double v:vp.getValue()) {
                    WorkSpace.Variable xvar = vars.add("mX." + diffRank, v),
                            dxvar = vars.add("md.X." + diffRank, v);
                    del.setWorkSpaceLink(i, xvar, dxvar);
                    Xvector.add(xvar);
                    dXvector.add(dxvar);
                    diffRank++;
                    i++;
                }
//            }
        }

        jacobEstType=state.getJacobianEstimationType();

        evalSysState(); //zerotime init

//        postEvaluate();

        for(OutputElement elem:mathOuts){
//            elem.updateData(time);
            elem.init();
        }
        selfInit();
    }

    public void solve(){
        //for(time=dt;time<=tEnd;time=time+dt){
        while(time<=tEnd){
            if(cancelFlag.isCancelled())
                break;
            preEvaluate();
            evalNextStep();
            postEvaluate();
            updateOutputs();
            progress.set(time);
        }
    }

    public void evalSysState(){
        int cnt=0;

        if(symbJacobian!=null)
            if(!symbJacobian.isEmpty()){
                boolean faultflag=false;

                while(true){
                    if(cancelFlag.isCancelled())
                        break;
                    //eval F(x)=0
                    MathPack.MatrixEqu.putValuesFromSymbRow(vals,algSystem,vars,inps);
                    double norm=MathPack.MatrixEqu.norm(vals);
                    if(norm<0.000001)
                        break;

                    switch(jacobEstType){
                        case 0: //full symbolic
                            MathPack.MatrixEqu.putValuesFromSymbRow(vals,newtonFunc,vars,inps);
                            break;
                        case 1: //inverse symbolic
                            List<List<Double>> invJ=MathPack.MatrixEqu.evalSymbMatr(invJacob, vars, inps);
                            vals=MathPack.MatrixEqu.mulMatxToRow(invJ,vals);
                            break;
                        case 2: //only jacob symb
                            MathPack.MatrixEqu.putValuesFromSymbMatr(J,symbJacobian,vars, inps);
                            MatrixEqu.solveLU(J,vals); // first 'vals' contains F(x)
                            break;
                        default:
                            vals=null;
                    }
                    for(int i=0;i<ind.length;i++){
                        if(Double.isNaN(vals[i])){
                            throw new Error(vars.getName(i)+" is not a number!");
                        }else if(Double.isInfinite(vals[i])){
                            throw new Error(vars.getName(i)+" is not finite!");
                        }
//                        double y=vector.get(ind[i])-vals[i];
//                        vector.set(ind[i], y);
                        double newval=commonVarsVector.get(i).getValue()-vals[i];
                        commonVarsVector.get(i).set(newval);
                    }
                    cnt++;
                    if(cnt>500){
                        if(false) { // for debug
                            for (double[] row : J) {
                                System.out.println(Arrays.toString(row));
                            }
                            System.out.println("x:");
                            System.out.println(Arrays.toString(vals));
                            System.out.println("F(x):");
                            MathPack.MatrixEqu.putValuesFromSymbRow(vals, algSystem, vars, inps);
                            System.out.println(Arrays.toString(vals));
                        }

                        cnt=0;
                        if(faultflag){
                            throw new Error("Dead loop!");
                        }else {

                            faultflag = true;
//                            int ii = 0;
//                            for (String var : vars.getVarNameList()) {
//
//                                if (!var.startsWith("X.")) {
//                                    vars.setValue(var, 0);
//                                    x0[ii] = 0;
//                                    //s[ii]=0;
//                                    ii++;
//                                }
//                            }
                        }
                    }
                }
            }

        evalMathDX();

        //for logout
        if(Rechatel.IS_LOGGING) {
            if (time == 0) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\NetBeansLogs\\XesLog.txt"))) {
                    bw.write("t ");
                    for (WorkSpace.Variable entry : vars.getVarList()) {
                        //if(entry.getKey().startsWith("X.")){
                        bw.write(entry.getName() + " ");
                        //}
                    }
                    bw.write(" numOfJac");
                    bw.newLine();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\NetBeansLogs\\XesLog.txt", true))) {
                bw.write(Double.toString(time) + " ");
                for (WorkSpace.Variable entry : vars.getVarList()) {
                    bw.write(entry.getValue() + " ");
                }
                bw.write(Integer.toString(cnt));
                bw.newLine();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

    }

    protected void evalMathDX(){
        for(DynamMathElem el:mathDynamics){
            el.evalDerivatives();
        }
    }

    private void preEvaluate(){
        for(Updatable el:dae.getUpdatableElements())
            el.preEvaluate(time);
    }

    private void postEvaluate(){
        for(Updatable el:dae.getUpdatableElements())
            el.postEvaluate(time);
    }

    private void updateOutputs() {
        for (OutputElement el : mathOuts)
            el.updateData(time);
    }

    protected void selfInit(){}


//    public void Save(BufferedWriter bw) throws IOException{
//        bw.write("<Solver>");bw.newLine();
//
//        bw.write("<Name>");
//        bw.write(getClass().getName());
//        bw.write("</Name>");bw.newLine();
//
//        bw.write("</Solver>");bw.newLine();
//
//    }
}

