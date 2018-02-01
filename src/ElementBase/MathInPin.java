/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import Connections.MathMarker;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Иван
 */
public class MathInPin extends MathPin{
    private MathOutPin source;
    
    MathInPin(){
        super();
        type='i';
    }

    MathInPin(double x,double y){
        this();
        view.setLayoutX(x);
        view.setLayoutY(y);
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

    void delete(){
        raschetkz.RaschetKz.drawBoard.getChildren().remove(view);
        setItsConnection(null);
    }

    public void setMathConnLink(MathMarker mc){
        setItsConnection(mc);
        setSource(mc.getSource());
    }
    
    @Override
    public void clearPin(){
        source=null;
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
}
