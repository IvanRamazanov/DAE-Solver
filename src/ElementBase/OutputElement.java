/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivan
 */
public abstract class OutputElement extends MathElement {
    protected List<Double> time;
    protected List<List<Double>> data;

    public OutputElement(){
        super();
        data=new ArrayList();
        time=new ArrayList();
    }
    
    public OutputElement(boolean catalog) {
        super(catalog);
    }
    
    @Override
    abstract public void init();
    
    abstract public void updateData(double t);
    
//    @Override
//    protected void delete(){
//        
//    }
    
    @Override
    protected List<Double> getValue(int outIndex) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
