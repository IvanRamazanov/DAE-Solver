/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Elements.Environment.Subsystem;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivan
 */
abstract public class OutputElement extends MathElement{
//
    public OutputElement(Subsystem sys){
        super(sys);
//        data=new ArrayList();
//        time=new ArrayList();
    }

    public OutputElement(boolean catalog) {
        super(catalog);
    }

//    @Override
//    abstract public void init();

    public abstract void updateData(double t);

//    @Override
//    protected void delete(){
//
//    }

    @Override
    protected List<Double> getValue(int outIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

