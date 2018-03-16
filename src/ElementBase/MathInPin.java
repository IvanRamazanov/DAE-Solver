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
public class MathInPin extends MathPin{
    private MathOutPin source;

    MathInPin(){

    }

    MathInPin(Element owner){
        super(owner);
    }

    public MathInPin(Element owner,double x,double y){
        this(owner);
        getView().setLayoutX(x);
        getView().setLayoutY(y);
    }

    public List<Double> getValue(){
        if(getSource()!=null){
            return getSource().getValue();
        }else{
            List<Double> out=new ArrayList();
            out.add(0.0);
            return out;
        }
    }

    /**
     * @return the source
     */
    public MathOutPin getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(MathOutPin source) {
        this.source = source;
    }

    @Override
    final public void clear(){
        super.clear();
        source=null;
    }
}

