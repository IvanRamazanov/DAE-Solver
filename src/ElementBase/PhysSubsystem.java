/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import MathPack.Rechatel;
import MathPack.StringFunctionSystem;
import MathPack.StringGraph;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * what for is this?
 * @author Ivan
 */
public class PhysSubsystem{
    //List<PhysToMatOut> outPuts;
    List<Double> xes,dX;
    StringFunctionSystem ode;
    List<MathInPin> inputs;
    List<MathOutPin> outputs;
    List<Double> time=new ArrayList();
    List<List<Double>> data=new ArrayList();

    public PhysSubsystem(StringFunctionSystem input){
        ode=input;
        xes=ode.getInitsX();
        inputs=ode.getInputs();
        outputs=new ArrayList();
        List<MathOutPin> oldOuts=ode.getOutputs();
        for(int i=0;i<oldOuts.size();i++){
            PhysToMatOut out=new PhysToMatOut(ode.getOutFuncs().get(i));
            outputs.add(out);
//            oldOuts.get(i).getItsConnection().setSource(out);
        }
        int i=0;
        for(Double x:xes){
            data.add(new ArrayList());
            data.get(i).add(x);
            i++;
        }
        time.add(0.0);
    }


    public void evalDerivatives(double time){
//        dX=ode.evaluale(time, xes, inputs);
        this.time.add(time);
    }

//    public void updateOutputs(){
//        //x=x+dx
//        for(int i=0;i<xes.size();i++){
//            xes.set(i, xes.get(i)+dX.get(i)*raschetkz.RaschetKz.dt); //Euler
//            data.get(i).add(xes.get(i));
//
//        }
//    }

    public void init(){

    }

    public void layout(){
        File file=new File("C:\\NetBeansLogs\\XesLog.txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))){
            for(int i=0;i<time.size();i++){
                bw.write(time.get(i).toString()+" ");
                for(List<Double> val:data){
                    bw.write(val.get(i).toString()+" ");
                }
                bw.newLine();
            }
        } catch (IOException e) { System.err.println(e.getMessage()); }
    }

//    @Override
//    protected void openDialogStage() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }


    protected List<Double> getValue(int outIndex) {
        return outputs.get(outIndex).getValue();
    }

    class PhysToMatOut extends MathOutPin{
        List<StringGraph> function;

        PhysToMatOut(List<StringGraph> f){
            function=f;
            //replace reference in matConnect

        }

        @Override
        public List<Double> getValue(){
            List<Double> out=new ArrayList();
//            out.add(function.evaluate(Rechatel.time, xes, inputs));
            return out; //??
        }
    }
}

