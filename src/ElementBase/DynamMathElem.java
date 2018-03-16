/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Elements.Environment.Subsystem.Subsystem;
import MathPack.WorkSpace;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivan
 */
abstract public class DynamMathElem extends MathElement{
    protected List<WorkSpace.Variable> wsX,wsDX;
    protected VectorParameter x0;

    public DynamMathElem(Subsystem sys){
        super(sys);
        wsX=new ArrayList();
        wsDX=new ArrayList();
        x0=new VectorParameter("init X", 0);

        parameters.add(x0);
    }

    public DynamMathElem(boolean flag){
        super(flag);

        x0=new VectorParameter("init X", 0);

        parameters.add(x0);
    }

    //Only for Int!!!!!!!!!!
    abstract public void evalDerivatives();

    @Override
    public void init(){
        super.init();
        wsX.clear();
        wsDX.clear();
        for(double v:x0.getValue()) {
            wsX.add(null);
            wsDX.add(null);
        }

//        List<Double> in=getInputs().get(0).getValue();
//        if(getInputs().get(0).getValue().size()!=wsX.size())
//            throw new Error("Dimensions mismatch in "+this.getName()+". Expected: "+wsX.size()+" present: "+getInputs().get(0).getValue().size());
        // MUST BE A VECTOR!!!!
    }

    public void setWorkSpaceLink(int index,WorkSpace.Variable Xlink,WorkSpace.Variable dXlink){
        wsX.set(index,Xlink);
        wsDX.set(index,dXlink);
    }

    /**
     * Its dimension
     * @return
     */
    public int getRank(){
        return x0.getValue().length;
    }

    public VectorParameter getX0(){
        return x0;
    }

}

