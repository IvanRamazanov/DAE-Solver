/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPackODE;

import java.util.List;

import Connections.MechWire;
import ElementBase.*;
import Connections.ElectricWire;
import javafx.beans.property.SimpleBooleanProperty;
import MathPack.MatrixEqu;
import MathPack.StringFunctionSystem;
import java.util.ArrayList;
import raschetkz.ModelState;

/**
 *
 * @author Ivan
 */
public class Compiler {
    List<SchemeElement> elemList=new ArrayList();
    List<DynamMathElem> dynMathElemList=new ArrayList();
    List<OutputElement> outputs=new ArrayList();
    List<ElectricWire> wireList=new ArrayList();
    List<MechWire> mechWires=new ArrayList<>();
    SimpleBooleanProperty recompile;
    DAE DAEsys;

    public Compiler(){
        recompile=new SimpleBooleanProperty(true);

    }

    public DAE evalNumState(ModelState state) throws Exception{
        if(recompile.get()){
            elemList.clear();
            elemList.addAll(state.getSchemeElements());
            wireList.clear();
            wireList.addAll(state.getElectroWires());
            mechWires.clear();
            mechWires.addAll(state.getMechWires());

            for(SchemeElement elem:elemList){
                int i=0;
                for(ElemPin pin:elem.getElemContactList()){
                    if(pin.getItsConnection()==null)
                        throw new Error("All electrical contacts must be connected! (Pin #"+i+" in "+elem.getName());
                    i++;
                }
                i=0;
                for(ElemMechPin pin:elem.getMechContactList()){
                    if(pin.getItsConnection()==null){
                        throw new Error("All mechanical contacts must be connected! (Pin #"+i+" in "+elem.getName());
                    }
                    i++;
                }
            }
            if((wireList.isEmpty()&&mechWires.isEmpty()||elemList.isEmpty())&&(state.getMathElements().isEmpty()||state.getMathConnList().isEmpty())){
                throw new Error("Empty Scheme");
            }else{
                //expand if needed
                //-------------------
                //check for bad lines
                //oneContourCircle=false;
                //-------------------
                //eval Arcs
                int[][] potM=MatrixEqu.getPotentialsMap(wireList, elemList);
                int[][] currM=MatrixEqu.getCurrentsMap(wireList, elemList);

                int[][] speedM=MatrixEqu.getSpeedMap(mechWires, elemList);
                int[][] torqM=MatrixEqu.getTorqueMap(mechWires,elemList);

                //somehow eval functions matrix
                StringFunctionSystem.initVarCount();
                List<StringFunctionSystem> elemFuncs=new ArrayList();
                for(SchemeElement elem:this.elemList){
                    elemFuncs.add(new StringFunctionSystem(elem));
                }
                DAEsys=StringFunctionSystem.getNumODE(elemFuncs, potM, currM,speedM,torqM);
                DAEsys.initJacobian(state.getJacobianEstimationType());

                //simulink
                for(MathElement elem:state.getMathElements()){
                    if(elem instanceof OutputElement){
                        outputs.add((OutputElement)elem);
                    }else if(elem instanceof DynamMathElem){
                        dynMathElemList.add((DynamMathElem)elem);
                    }
                    if(elem instanceof Updatable)
                        DAEsys.getUpdatableElements().add((Updatable)elem);
                }
                DAEsys.setDynMaths(dynMathElemList);
                DAEsys.setMathOuts(outputs);
            }
        }
        return DAEsys;
    }

    public List<OutputElement> getOutputs(){
        return outputs;
    }

    public List<DynamMathElem> getDynamics(){
        return dynMathElemList;
    }

}

