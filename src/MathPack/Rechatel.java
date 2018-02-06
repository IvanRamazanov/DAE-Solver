/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPack;

import Connections.ElectricWire;
import ElementBase.DynamMathElem;
import ElementBase.OutputElement;
import java.util.ArrayList;
import java.util.List;
import ElementBase.PhysSubsystem;
import MathPackODE.Solver;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javafx.concurrent.Task;
import raschetkz.ModelState;

/**
 *
 * @author Ivan
 *
 */
public class Rechatel extends Task<Integer>{
    static MathPackODE.Compiler compiler;
    ModelState state;
    private final boolean byGOST;
    public String ssstring;
    //public static double time;
    private Solver solver;



    public Rechatel(ModelState state,boolean byGost){
        this.state=state;
        try{
            String className="MathPackODE.Solver"+state.getSolver().get();
            Class<?> clas=Class.forName(className);
            Constructor<?> ctor=clas.getConstructor();
            solver=(Solver)ctor.newInstance(new Object[] {});
        }catch(ClassNotFoundException|NoSuchMethodException|InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex){
            ex.printStackTrace(System.err);
        }
        this.byGOST=byGost;
        compiler=new MathPackODE.Compiler();
    }

//    @Override
//    protected Integer call(){
//        List<ShemeElement> elems=new ArrayList();
//        List<Wire> branchs=new ArrayList();
//        if(expandSheme(elems, branchs)){
//            ShemeElement.time=0;
//            Variables vector=new Variables(elems,branchs);
//            vector.updateElems(true);
//            if(vector.isDynamic){
//                for(double t=dt;t<=tEnd;t=t+dt){
//                    ShemeElement.time=t;
//                    for(ShemeElement data:this.elemList){
//                        data.EvalNextStep(dt);
//                    }
//                    vector.reSolve();
//                    vector.updateElems(false);
//                    this.updateProgress(t,tEnd);
//                }
//            }else{
//                for(double t=dt;t<=tEnd;t=t+dt){
//                    ShemeElement.time=t;
//                    for(ShemeElement data:this.elemList){
//                        data.EvalNextStep(dt);
//                    }
//                    vector.reSolve();
//                    vector.updateElems(false);
//                    this.updateProgress(t,tEnd);
//                }
//            }
//            this.updateProgress(tEnd,tEnd);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Rechatel.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            this.updateProgress(0,tEnd);
//        }
//        Integer i = null;
//        return i;
//    }



    @Override
    protected Integer call(){
        try{
            List<OutputElement> outs;
            List<DynamMathElem> dynamics;
            PhysSubsystem physics;
            double tEnd=state.getTend().doubleValue();
            //double dt=state.getDt().doubleValue();
            updateMessage("Compiling...");
            DAE sys=compiler.evalNumState(state);
            this.updateProgress(0, tEnd);
            solver.init(sys,state,this);
            long start=System.currentTimeMillis();
            solver.progress.addListener((t,o,n)->{
                this.updateProgress(n.doubleValue(), tEnd);
            });
            //for(time=dt;time<=tEnd;time=time+dt){
            //solver.evalNextStep(time);
            //}
            solver.solve(tEnd);

            //sys.layout();
            long end=System.currentTimeMillis();
            System.out.println("eval time: "+(end-start));
            //this.updateProgress(tEnd, tEnd);
            //this.cancel();
        }catch(Exception|Error e){
            e.printStackTrace(System.err);
            throw new Error();
        }
        return null;
    }


//    /**
//     * Find nodes(branches whith rank>=2)
//     * @param input - Branch list
//     * @return nodes
//     */
//    private List<Wire> resortBranches(List<Wire> input){
//        List<Wire> output=new ArrayList<>();
//        try{
//            input.forEach(branch->{
//                if(branch.getRank()>2){
//                    output.add(branch);
//                }
//            });
//            if(output.isEmpty()){
//                output.add(input.get(0));
//                output.add(input.get(1));
//            }
//        }
//        catch(Exception e){
//            raschetkz.RaschetKz.layoutString("Empty sheme!");
//        }
//        return(output);
//    }

}
