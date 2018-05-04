/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPackODE;

import Connections.MathWire;
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

import static java.lang.Math.*;

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
    private List<MathWire> wires;
    protected WorkSpace vars;
    protected DAE dae;
    protected ArrayList<WorkSpace.Variable> commonVarsVector,Xvector,dXvector;
    protected int jacobEstType,diffRank;
    protected double[]
            vals;
    private double[][] J;
    protected int[] ind;
    public static double dt, tEnd, absTol, relTol;
    public static WorkSpace.Variable time;
    public static SimpleDoubleProperty progress=new SimpleDoubleProperty();
    protected Task cancelFlag;

    double[] testFac;
    boolean testFlag;

    abstract public void evalNextStep();

    public void init(DAE daeSys,ModelState state, Task tsk){
        vars=daeSys.getVars();
        time=vars.get("time");
        tEnd=state.getTend().doubleValue();
        progress.setValue(0.0);
        dae=daeSys;

        symbJacobian=daeSys.getJacob();
        invJacob=daeSys.getInvJacob();
        newtonFunc=daeSys.getNewtonFunc();
        algSystem=daeSys.getAlgSystem();
        mathOuts=daeSys.getMathOuts();
        mathDynamics=daeSys.getDynMaths();
        cancelFlag=tsk;

        wires=state.getMathConnList();

        dt=state.getDt().doubleValue();
        absTol=state.getAbsTol().doubleValue();
        relTol=state.getRelTol().doubleValue();

        vals=new double[algSystem.size()];
        J=new double[algSystem.size()][algSystem.size()];

        ind=new int[algSystem.size()];
        commonVarsVector=new ArrayList<>();
        dXvector=new ArrayList<>();
        Xvector=new ArrayList<>();
        diffRank=dae.getSymbDiffRank();
        for(int i=0;i<diffRank;i++){
            Xvector.add(null);
            dXvector.add(null);
        }

        for(WorkSpace.Variable var:vars.getVarList()){
            String name=var.getName();
            if(!name.startsWith("X.")&&WorkSpace.isRealVariable(name)){
                commonVarsVector.add(var);
                if(name.startsWith("d.X.")) {
                    int i=var.getIndex();
                    dXvector.set(i,var);
                    if(var instanceof WorkSpace.StringFuncVar)
                        commonVarsVector.remove(var);
                }
            }else if(name.startsWith("X.")){
                int i=var.getIndex();
                Xvector.set(i,var);
            }
        }
        testFac=new double[commonVarsVector.size()];
        testFlag=true;
        for(int i=0;i<commonVarsVector.size();i++)
            testFac[i]=1e15;

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
        while(time.getValue()<=tEnd){
            if(cancelFlag.isCancelled())
                break;
            preEvaluate();
            evalNextStep();
            postEvaluate();
            updateOutputs();
            progress.set(time.getValue());
        }
    }

    public void evalSysState(){
        int cnt=0;
        double tol=1e-6,m=1.0;
        for(MathWire w:wires)
            w.resetValue();

        if(symbJacobian!=null) {
            if (!symbJacobian.isEmpty()) {
                boolean faultflag = false;

                while (true) {
                    if (cancelFlag.isCancelled())
                        break;

                    //eval F(x)=0
                    MathPack.MatrixEqu.putValuesFromSymbRow(vals, algSystem, vars);
                    double norm = MathPack.MatrixEqu.norm(vals);


                    switch (jacobEstType) {
                        case 0: //full symbolic
//                            MathPack.MatrixEqu.putValuesFromSymbRow(vals, newtonFunc, vars);

                            //test estimJ
                            if(testFlag){
                                MathPack.MatrixEqu.putValuesFromSymbMatr(J, symbJacobian, vars);

                                testFlag=false;
                            }else {
                                estimJ(J, algSystem, vals, commonVarsVector, testFac);
                                double[][] jj = new double[J.length][J.length];
                                MathPack.MatrixEqu.putValuesFromSymbMatr(jj, symbJacobian, vars);
                                for (int k = 0; k < J.length; k++) {
                                    for (int q = 0; q < J.length; q++) {
                                        System.out.print("("+jj[k][q] +"/"+ J[k][q] +") " + " ");
                                    }
                                    System.out.println();
                                }
                            }
                            MatrixEqu.solveLU(J, vals); // first 'vals' contains F(x)
                            break;
                        case 1: //inverse symbolic
                            List<List<Double>> invJ = MathPack.MatrixEqu.evalSymbMatr(invJacob, vars);
                            vals = MathPack.MatrixEqu.mulMatxToRow(invJ, vals);
                            break;
                        case 2: //only jacob symb
                            MathPack.MatrixEqu.putValuesFromSymbMatr(J, symbJacobian, vars);
                            MatrixEqu.solveLU(J, vals); // first 'vals' contains F(x)
                            break;
                        default:
                            vals = null;
                    }
                    for (int i = 0; i < ind.length; i++) {
                        if (Double.isNaN(vals[i])) {
                            throw new Error(vars.getName(i) + " is not a number! At time "+time.getValue());
                        } else if (Double.isInfinite(vals[i])) {
                            throw new Error(vars.getName(i) + " is not finite! At time "+time.getValue());
                        }
//                        double y=vector.get(ind[i])-vals[i];
//                        vector.set(ind[i], y);
                        double newval = commonVarsVector.get(i).getValue() - m*vals[i];
                        commonVarsVector.get(i).set(newval);
                    }

                    MathPack.MatrixEqu.putValuesFromSymbRow(vals, algSystem, vars);
                    norm = MathPack.MatrixEqu.norm(vals);
                    if (norm < tol)
                        break;

                    cnt++;
                    if (cnt > 250) {
                        if (false) { // for debug
                            for (double[] row : J) {
                                System.out.println(Arrays.toString(row));
                            }
                            System.out.println("x:");
                            System.out.println(Arrays.toString(vals));
                            System.out.println("F(x):");
                            MathPack.MatrixEqu.putValuesFromSymbRow(vals, algSystem, vars);
                            System.out.println(Arrays.toString(vals));
                        }
                        //tol*=10;
                        cnt = 0;
                        m=0.5;
                        if (faultflag) {
                            throw new Error("Dead loop! At time "+time.getValue());
                        } else {

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
        }
        evalMathDX();

        //for logout
        if(Rechatel.IS_LOGGING) {
            if (time.getValue() == 0) {
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
                bw.write(Double.toString(time.getValue()) + " ");
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
            el.preEvaluate(time.getValue());
    }

    private void postEvaluate(){
        for(Updatable el:dae.getUpdatableElements())
            el.postEvaluate(time.getValue());
    }

    private void updateOutputs() {
        for (OutputElement el : mathOuts)
            el.updateData(time.getValue());
    }

    protected void selfInit(){}

    protected void copyArray(List<WorkSpace.Variable> source, double[] destination){
        for (int i=0;i<source.size();i++) {
            destination[i]=source.get(i).getValue();
        }
    }

    /**
     * Evaluates estimation of Jacobian of fx near f0=fx(x0) point.
     * @param J - output
     * @param fx - target function
     * @param f0 - function value at fx(x0)
     * @param x - x0 initial point
     * @param fac
     */
    protected void estimJ(double[][] J, List<StringGraph> fx, double[] f0,ArrayList<WorkSpace.Variable> x, double[] fac){
        final double eps=Math.ulp(1.0),
                br=pow(eps,0.875),
                bl=pow(eps,0.75),
                bu=pow(eps,0.25),
                facmin=pow(eps,0.98),
                facmax=1e15;
        int ny=fx.size();
        double[] f1=new double[ny],x0=new double[ny];
        for(int i=0;i<ny;i++){
            x0[i]=x.get(i).getValue();
        }

        // row cycle
        int i=0;
        while(i<ny){

            double yScale=max(abs(x0[i]),0.0); //?
            yScale=yScale==0.0?160*Math.ulp(eps):yScale;

            double del=fac[i]*abs(yScale);
            x.get(i).set(x0[i]+del);  //shift X

//            evalSysState();

            for(int m=0;m<ny;m++) {
                f1[m] = fx.get(m).evaluate(vars);
            }
            double maxDiff=-1.0;
            int max=-1;

            for(int j=0;j<ny;j++){
//                f1[j]=dXvector.get(j).getValue();

                double aVal=abs(f1[j]-f0[j]);
                if(aVal>maxDiff) {
                    maxDiff=aVal;
                    max = j;
                }
            }

            double scale=max(abs(f1[max]),abs(f0[max]));

            if(min(abs(f1[max]),abs(f0[max]))==0) {
                for(int j=0;j<ny;j++){
                    J[j][i]=(f1[j]-f0[j])/del;
                }
                x.get(i).set(x0[i]);
                i++;
            }else if(maxDiff>bu*scale){
                fac[i]=max(0.013*fac[i],facmin);
            }else if(maxDiff<=bl*scale){
                fac[i]=min(11*fac[i],facmax);
            }else if(maxDiff<br*scale){
                System.out.println("bad!");
            }else{
                for(int j=0;j<ny;j++){
                    J[j][i]=(f1[j]-f0[j])/del;
                }
                x.get(i).set(x0[i]);
                i++;
            }
        }
    }

    protected void updateJ(double[][] J,double[] fNew,double[] fOld,double[] xDelta){
        double denum=0,num=0;
        int len=J.length;
        double[][] tmp=new double[len][len];
        double[] rowTmp=new double[len];
        for(int i=0;i<len;i++){
            for(int j=0;j<len;j++){
                num+=J[i][j]*(xDelta[j]);
            }
            rowTmp[i]=num;
            num=0;

            denum+=(xDelta[i])*(xDelta[i]);
        }
        for(int i=0;i<len;i++){
            rowTmp[i]=fNew[i]-fOld[i]-rowTmp[i];
        }
        for(int i=0;i<len;i++) {
            for (int j = 0; j < len; j++) {
                J[i][j]+=rowTmp[i]*(xDelta[j])/denum;
            }
        }
    }
}

