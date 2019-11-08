/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPackODE;

import Connections.MechWire;
import Connections.Wire;
import ElementBase.*;
import Elements.Environment.Subsystem.Subsystem;
import MathPack.MatrixEqu;
import MathPack.StringFunctionSystem;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Ivan
 */
public class Compiler {
    List<SchemeElement> elemList=new ArrayList();
    List<DynamMathElem> dynMathElemList=new ArrayList();
    List<OutputElement> outputs=new ArrayList();
    List<Wire> wireList=new ArrayList();
    List<MechWire> mechWires=new ArrayList<>();
    SimpleBooleanProperty recompile;
    DAE DAEsys;

    public Compiler(){
        recompile=new SimpleBooleanProperty(true);


    }

    public DAE evalNumState(Subsystem state, String logFile, boolean isLogNedded) throws Exception{
        if(recompile.get()){
            elemList.clear();
            elemList.addAll(state.getSchemeElements());
            wireList.clear();
            wireList.addAll(state.getElectroWires());
            mechWires.clear();
            mechWires.addAll(state.getMechWires());

            for(SchemeElement elem:elemList){
                int i=0;
                for(ElectricPin pin:elem.getElemContactList()){
                    if(pin.getItsConnection()==null)
                        throw new Error("All electrical contacts must be connected! (Pin #"+i+" in "+elem.getName());
                    i++;
                }
                i=0;
                for(MechPin pin:elem.getMechContactList()){
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
                DAEsys=StringFunctionSystem.getNumODE(
                        new MathPack.CompilerDataODE(logFile,isLogNedded,
                                elemFuncs, potM, currM,speedM,torqM));
                //DAEsys.initJacobian(state.getJacobianEstimationType());
                DAEsys.initJacobian(2); // symbolic Jac only, other cases for losers

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

