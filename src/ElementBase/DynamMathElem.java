/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import MathPackODE.Solver;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class DynamMathElem extends MathElement{
    private List<Double> X_old;
    List<Double> dX;
    List<Parameter> x0;
    
    public DynamMathElem(){
        super();
        X_old=new ArrayList();
        dX=new ArrayList();
        x0=new ArrayList();
        x0.add(new Parameter("init X", 0));
        parameters.addAll(x0);
    }
    
    public DynamMathElem(boolean flag){
        super(flag);
    }
    
    //Only for Int!!!!!!!!!!
    public void evalDerivatives(double time){
        dX=getInputs().get(0).getValue();
//        return getInputs().get(0).getValue();
    }
    
    public void updateOutputs(Solver solver){
        solver.simpleUpdate(X_old,dX);
    }
    
    @Override
    public void init(){
        X_old.clear();
        X_old.add(x0.get(0).getDoubleValue());
        // MUST BE A VECTOR!!!!
    }
    
    @Override
    protected List<Double> getValue(int outIndex) {
        return X_old;
    }
    
    /**
     * Its dimension
     * @return 
     */
    public int getRank(){
        return X_old.size();
    }

//    @Override
//    protected void openDialogStage() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    
}
