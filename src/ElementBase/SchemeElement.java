/*
  To change this license header, choose License Headers in Project Properties.
  To change this template file, choose Tools | Templates
  and open the template in the editor.
 */
package ElementBase;

import java.util.ArrayList;
import java.util.List;

import raschetkz.RaschetKz;

/**
 * Number of functions equals NumOfContacts-1. Not include obvious current dependencies.
 * @author Ivan
 */
public abstract class SchemeElement extends Element{



    public SchemeElement(){
        super();
        maxX=viewPane.getBoundsInLocal().getMaxX();
        mathContOffset=viewPane.getBoundsInLocal().getMaxY()/2;

    }

    public SchemeElement(boolean catalog){
        super(catalog);
    }

    public abstract String[] getStringFunction();

    public List<ElemPin> getElemContactList(){
        return(electricContacts);
    }

    public List<ElemMechPin> getMechContactList(){
        return mechContacts;
    }

    /**
     * Удаляет элемент
     */
    @Override
    public void delete(){
        getElemContactList().forEach(elemCont->{
            elemCont.clear();
            if(elemCont.getItsConnection()!=null){
                elemCont.getItsConnection().unPlug();
            }
        });
        getElemContactList().clear();

        getMechContactList().forEach(elemCont->{
            elemCont.clear();
            if(elemCont.getItsConnection()!=null){
                elemCont.getItsConnection().unPlug();
            }
        });
        getMechContactList().clear();

        getInputs().forEach(elemCont->{
            elemCont.clear();
            if(elemCont.getItsConnection()!=null){
                elemCont.getItsConnection().unPlug();
            }
        });
        getInputs().clear();

        getOutputs().forEach(elemCont->{
            elemCont.clear();
            if(elemCont.getItsConnection()!=null){
                elemCont.getItsConnection().unPlug();
            }
        });
        getOutputs().clear();

        RaschetKz.elementList.remove(this);
        RaschetKz.drawBoard.getChildren().remove(this.getView());
    }

    @Override
    public void init(){};

    /**
     * @return the inputs
     */
    public List<MathInPin> getInputs() {
        return mathInputs;
    }

    /**
     * @return the outputs
     */
    public List<MathOutPin> getOutputs() {
        return mathOutputs;
    }

}


