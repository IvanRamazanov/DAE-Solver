/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPack;

import Connections.Wire;
import ElementBase.ShemeElement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class Arc{
    private List<ShemeElement> elements;
    private Wire positive,negative;
    private int muliplex=0;
    private double i;

    Arc(){
        elements=new ArrayList<>();
    }

    private void setI(double I){
        this.getElements().forEach(data->{
            //data.setI(I);
        });
        i=I;
    }

    void init() {
        this.i=0;
        this.getElements().forEach(data->{
            //data.init();
        });
    }

    /**
     * @return the positive
     */
    public Wire getPositive() {
        return positive;
    }

    /**
     * @param positive the positive to set
     */
    public void setPositive(Wire positive) {
        this.positive = positive;
    }
    
//    public void evalArc(Elements.ShemeElement.ElemContact startContact, boolean isCircleCircuit){
//        Elements.ShemeElement.ElemContact nextContact=startContact.getOwner().;
//        if(isCircleCircuit){
//            next
//            while
//        }else{
//            
//        }
//        
//    }

    /**
     * @return the negative
     */
    public Wire getNegative() {
        return negative;
    }

    /**
     * @param negative the negative to set
     */
    public void setNegative(Wire negative) {
        this.negative = negative;
    }

    /**
     * @return the elements
     */
    public List<ShemeElement> getElements() {
        return elements;
    }

    public void addElement(ShemeElement shE) {
        this.elements.add(shE);
    }

    /**
     * @return the muliplex
     */
    public int getMuliplex() {
        return muliplex;
    }

    /**
     * @param muliplex the muliplex to set
     */
    public void setMuliplex(int muliplex) {
        this.muliplex = muliplex;
    }
}
