/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPackODE;

import java.util.List;
import ElementBase.ShemeElement;
import Connections.ElectricWire;
import ElementBase.DynamMathElem;
import ElementBase.MathElement;
import ElementBase.OutputElement;
import MathPack.DAE;
import javafx.beans.property.SimpleBooleanProperty;
import MathPack.MatrixEqu;
import MathPack.Rechatel;
import MathPack.StringFunctionSystem;
import java.util.ArrayList;
import raschetkz.ModelState;

/**
 *
 * @author Ivan
 */
public class Compiler {
    List<ShemeElement> elemList=new ArrayList();
    List<DynamMathElem> dynMathElemList=new ArrayList();
    List<OutputElement> outputs=new ArrayList();
    List<ElectricWire> wireList=new ArrayList();
    SimpleBooleanProperty recompile;
    //StringFunctionSystem ODEfunc;
    DAE DAEsys;

    public Compiler(){
        recompile=new SimpleBooleanProperty(true);

    }

//    public void eval(ModelState state){
//        List<ShemeElement> eleList=state.GetElems();
//        List<Wire> wires=state.GetWires();
//
//        if(recompile.get()){
//            if(!elemList.isEmpty())
//                elemList.clear();
//            elemList.addAll(eleList);
//            if(!wireList.isEmpty())
//                wireList.clear();
//            wireList.addAll(wires);
//
//
//            if(wireList.isEmpty()||elemList.isEmpty()){
//                throw new Error("Empty sheme!");
//            }else{
//                //expand if needed
//                //-------------------
//                //check for bad lines
//                //oneContourCircle=false;
//                //-------------------
//                //eval Arcs
//                int[][] potM=MatrixEqu.getPotentialsMap(wireList, elemList);
//                int[][] currM=MatrixEqu.getCurrentsMap(wireList, elemList);
//
//                //somehow eval functions matrix
//                StringFunctionSystem.initVarCount();
//                List<StringFunctionSystem> elemFuncs=new ArrayList();
//                for(ShemeElement elem:this.elemList){
//                    elemFuncs.add(new StringFunctionSystem(elem));
//                }
//
//                ODEfunc=StringFunctionSystem.MergeFunctions(elemFuncs, potM, currM);
//                // v right sidax dX/t=F(X,time)
//
//                //---------
//                //create right side
//            }
//            if(state.GetMathElems().isEmpty()||state.getMathConnList().isEmpty()){
//
//            }else{
//                for(MathElement elem:state.GetMathElems()){
//                    if(elem instanceof OutputElement){
//                        outputs.add((OutputElement)elem);
//                    }else if(elem instanceof DynamMathElem){
//                        dynMathElemList.add((DynamMathElem)elem);
//                    }
//                }
//            }
//
//            //recompile.set(false);
//        }
//    }

    public DAE evalNumState(ModelState state) throws Exception{
        List<ShemeElement> eleList=state.GetElems();
        List<ElectricWire> wires=state.GetWires();

        if(recompile.get()){
            if(!elemList.isEmpty())
                elemList.clear();
            elemList.addAll(eleList);
            if(!wireList.isEmpty())
                wireList.clear();
            wireList.addAll(wires);

            if((wireList.isEmpty()||elemList.isEmpty())&&(state.GetMathElems().isEmpty()||state.getMathConnList().isEmpty())){
                throw new Error("Empty Sheme");
            }else{
                //expand if needed
                //-------------------
                //check for bad lines
                //oneContourCircle=false;
                //-------------------
                //eval Arcs
                int[][] potM=MatrixEqu.getPotentialsMap(wireList, elemList);
                int[][] currM=MatrixEqu.getCurrentsMap(wireList, elemList);

                //somehow eval functions matrix
                StringFunctionSystem.initVarCount();
                List<StringFunctionSystem> elemFuncs=new ArrayList();
                for(ShemeElement elem:this.elemList){
                    elemFuncs.add(new StringFunctionSystem(elem));
                }
                DAEsys=StringFunctionSystem.getNumODE(elemFuncs, potM, currM);
                DAEsys.initJacobian(state.getJacobianEstimationType());

                //simulink
                for(MathElement elem:state.GetMathElems()){
                    if(elem instanceof OutputElement){
                        outputs.add((OutputElement)elem);
                    }else if(elem instanceof DynamMathElem){
                        dynMathElemList.add((DynamMathElem)elem);
                    }
                }
                DAEsys.setDynMaths(dynMathElemList);
                DAEsys.setMathOuts(outputs);
            }
        }
        return DAEsys;
    }


//    public StringFunctionSystem getODE(){
//        return ODEfunc;
//    }

    public List<OutputElement> getOutputs(){
        return outputs;
    }

    public List<DynamMathElem> getDynamics(){
        return dynMathElemList;
    }

//    public void alternateEval(List<ShemeElement> eleList,List<Wire> wires){
//        if(recompile.get()){
//            if(!elemList.isEmpty())
//                elemList.clear();
//            elemList.addAll(eleList);
//            if(!wireList.isEmpty())
//                wireList.clear();
//            wireList.addAll(wires);
//            //expand if needed
//            //----------------
//            //check for bad lines
//            //oneContourCircle=false;
//            //-------------------
//            //eval Arcs
//            List<MathPack.Arc> arcs=new ArrayList();
//
//            //---------
//            //
//        }
//    }
}

