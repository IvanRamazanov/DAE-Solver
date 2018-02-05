/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raschetkz;

import Connections.MathWire;

import Connections.ElectricWire;
import ElementBase.ElemPin;
import ElementBase.Element;
import ElementBase.MathInPin;
import ElementBase.MathElement;
import ElementBase.MathOutPin;
import ElementBase.ShemeElement;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;

/**
 *
 * @author Иван
 */
    public class ModelState{
        List<ShemeElement> ElementList;
        List<MathElement> MathElemList;
        private List<MathWire> mathConnList;
        List<ElectricWire> BranchList;
        Pane draws;
//        int numOfElems;
        //double dt,tend;
        private SimpleStringProperty solver;
        private SimpleDoubleProperty dt;
        private SimpleDoubleProperty tend;
        private int jacobianEstimationType;
        String fileName;
        
        ModelState(){
            ElementList=new ArrayList();
            MathElemList=new ArrayList();
            BranchList=new ArrayList();
            mathConnList=new ArrayList();
            draws=new Pane();
            dt=new SimpleDoubleProperty();
            tend=new SimpleDoubleProperty();
            solver=new SimpleStringProperty();
            jacobianEstimationType=0;
        }
        
        
        
        /**
         * Inits moledwide params. 
         * @return true, if recompilation is needed;????
         */
//        public boolean init(){
////            this.numOfElems=this.ElementList.size();
//            solver=RaschetKz.solverType;
//            return true;
//        }
        
        public void Save(){
            //this.init();
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            ByteBuffer temp=ByteBuffer.allocate(8);

            //solver info
            for(char c:getSolver().get().toCharArray()){
                temp.putChar(0,c);
                baos.write(temp.array(), 0, 2);
            }
            temp.putChar(0,'\0');
            baos.write(temp.array(), 0, 2);
            temp.putDouble(0, getDt().doubleValue()); // dt
            baos.write(temp.array(), 0, 8);
            temp.putDouble(0, getTend().doubleValue()); // t_end
            baos.write(temp.array(), 0, 8);
            temp.putInt(0, getJacobianEstimationType()); // jacobian estimation
            baos.write(temp.array(), 0, 4);
            
            //elements
            List<MathInPin> inpConts=new ArrayList();
            List<MathOutPin> outConts=new ArrayList();
            
            temp.putInt(0, this.ElementList.size());    //num of elems
            baos.write(temp.array(), 0, 4);
            for(ShemeElement shE:this.ElementList){
                //name
                for(char c:shE.getClass().getName().toCharArray()){
                    temp.putChar(0,c);
                    baos.write(temp.array(), 0, 2);
                }
                temp.putChar(0,'\0');
                baos.write(temp.array(), 0, 2);
                //layouts
                temp.putDouble(0, shE.getView().getLayoutX());
                baos.write(temp.array(), 0, 8);
                temp.putDouble(0, shE.getView().getLayoutY());
                baos.write(temp.array(), 0, 8);
                //rotation
                temp.putDouble(0, shE.getView().getRotate());
                baos.write(temp.array(), 0, 8);
                //params
                for(ShemeElement.Parameter p:shE.getParameters()){
                    temp.putDouble(0, p.getDoubleValue());
                    baos.write(temp.array(), 0, 8);
                }
                //inits
                for(Element.InitParam ip:shE.getInitials()){
                    temp.putDouble(0, ip.getDoubleValue());
                    baos.write(temp.array(), 0, 8);// value
                    if(ip.getPriority()){
                        temp.putInt(0,1);
                        baos.write(temp.array(), 0, 4);
                    }else{
                        temp.putInt(0,0);
                        baos.write(temp.array(), 0, 4);
                    }
                }
                
                inpConts.addAll(shE.getInputs());
                outConts.addAll(shE.getOutputs());
            }
            
            //Wires                   
            List<ElemPin> ECList=new ArrayList();
            for(ShemeElement e:this.ElementList){
                ECList.addAll(e.getElemContactList());                
            }  
            
            //num of wires
            temp.putInt(0, this.BranchList.size());
            baos.write(temp.array(), 0, 4);
            
            for(ElectricWire w:this.BranchList){
                w.Save(baos, ECList);
            }
            
            //math elems
            
            temp.putInt(0, this.MathElemList.size());    //num of elems
            baos.write(temp.array(), 0, 4);
            for(MathElement mathE:this.MathElemList){
                //name
                for(char c:mathE.getClass().getName().toCharArray()){
                    temp.putChar(0,c);
                    baos.write(temp.array(), 0, 2);
                }
                temp.putChar(0,'\0');
                baos.write(temp.array(), 0, 2);
                //layouts
                temp.putDouble(0, mathE.getView().getLayoutX());
                baos.write(temp.array(), 0, 8);
                temp.putDouble(0, mathE.getView().getLayoutY());
                baos.write(temp.array(), 0, 8);
                //rotation
                temp.putDouble(0, mathE.getView().getRotate());
                baos.write(temp.array(), 0, 8);
                //params
                for(ShemeElement.Parameter p:mathE.getParameters()){
                    temp.putDouble(0, p.getDoubleValue());
                    baos.write(temp.array(), 0, 8);
                }
                
                inpConts.addAll(mathE.getInputs());
                outConts.addAll(mathE.getOutputs());
            }
            
            //MathWires
            //num of wires
            temp.putInt(0, this.mathConnList.size());
            baos.write(temp.array(), 0, 4);
            
            for(MathWire mc:this.mathConnList){
                //source (start)
                int tma=outConts.indexOf(mc.getSource());
                temp.putInt(0, outConts.indexOf(mc.getSource()));
                baos.write(temp.array(), 0, 4);
                //destin (end)
//                tma=inpConts.indexOf(mc.get());
//                temp.putInt(0, inpConts.indexOf(mc.getDestin()));
                
                
                baos.write(temp.array(), 0, 4);
                // !!!! NOT PLUGGED CASE !!!!
            }
            
            //write to file
            try{
                FileOutputStream fos=new FileOutputStream(fileName);
                baos.writeTo(fos);
                fos.close();
            }catch(IOException io){
                raschetkz.RaschetKz.layoutString(io.getStackTrace().toString());
            }
        }
        
        public void Save(String filePath){
            this.fileName=filePath;
            Save();
        }
        
        public void Load(String filePath){
            try{            
                FileInputStream fis = new FileInputStream(filePath);
                this.fileName=filePath;
                this.BranchList.clear();
                this.ElementList.clear();
                this.MathElemList.clear();
                this.mathConnList.clear();
                this.draws.getChildren().clear();
                ByteBuffer temp=ByteBuffer.allocate(8);
                
                //solver
                String sTemp=new String();
                while(true){            //solver type
                    fis.read(temp.array(),0,2);
                    if(temp.getChar(0)!='\0'){
                        sTemp+=temp.getChar(0);
                    }else{
                        break;
                    }                    
                }
                solver.set(sTemp);
                fis.read(temp.array(), 0, 8);  //dt
                dt.set(temp.getDouble(0));
                fis.read(temp.array(), 0, 8);  //tend
                tend.set(temp.getDouble(0));
                fis.read(temp.array(), 0, 4);  //tend
                setJacobianEstimationType(temp.getInt(0));
                
                //Elements
                List<MathInPin> inpConts=new ArrayList();
                List<MathOutPin> outConts=new ArrayList();
                
                fis.read(temp.array(), 0, 4);  //num of elems
                int numOfElems=temp.getInt(0);
                
                for(int i=0;i<numOfElems;i++){
                    sTemp="";
                    while(true){            //Elem type
                        fis.read(temp.array(),0,2);
                        if(temp.getChar(0)!='\0'){
                            sTemp+=temp.getChar(0);
                        }else{
                            break;
                        }                    
                    }
                    try{
                        //creation
                        Class<?> clas=Class.forName(sTemp);
                        Constructor<?> ctor=clas.getConstructor();
                        ShemeElement elem=(ShemeElement)ctor.newInstance(new Object[] {});
                        
                        //Layouts
                        fis.read(temp.array(), 0, 8);  //x
                        elem.getView().setLayoutX(temp.getDouble(0));
                        fis.read(temp.array(), 0, 8);  //y
                        elem.getView().setLayoutY(temp.getDouble(0));
                        fis.read(temp.array(), 0, 8);  //rotation
                        elem.getView().setRotate(temp.getDouble(0));
                        
                        for(ShemeElement.Parameter p:elem.getParameters()){
                            fis.read(temp.array(), 0, 8); //pram val
                            p.setValue(temp.getDouble(0));
                        }
                        for(ShemeElement.InitParam p:elem.getInitials()){
                            fis.read(temp.array(), 0, 8); //init val
                            p.setValue(temp.getDouble(0));
                            fis.read(temp.array(), 0, 4); //priority
                            if(temp.getInt(0)==1)
                                p.setPriority(true);
                            else
                                p.setPriority(false);
                        }
                        this.ElementList.add(elem);
                        inpConts.addAll(elem.getInputs());
                        outConts.addAll(elem.getOutputs());
                    }catch(ClassNotFoundException|NoSuchMethodException|InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex){
                        raschetkz.RaschetKz.layoutString(ex.getStackTrace().toString());
                    }
                }
                
                
                
                //Wires
                List<ElemPin> ECList=new ArrayList();
                for(ShemeElement e:this.ElementList){
                    ECList.addAll(e.getElemContactList());                
                }
                //num of wires
                fis.read(temp.array(),0,4);
                numOfElems=temp.getInt(0);
                //wire cycle
                for(int i=0;i<numOfElems;i++){  
                    BranchList.add(new ElectricWire(fis,ECList));
                }
                
                //math elems

                //num of elems
                fis.read(temp.array(),0,4);
                numOfElems=temp.getInt(0);
                for(int i=0;i<numOfElems;i++){
                    sTemp="";
                    while(true){            //Elem type
                        fis.read(temp.array(),0,2);
                        if(temp.getChar(0)!='\0'){
                            sTemp+=temp.getChar(0);
                        }else{
                            break;
                        }                    
                    }
                    try{
                        //creation
                        Class<?> clas=Class.forName(sTemp);
                        Constructor<?> ctor=clas.getConstructor();
                        MathElement elem=(MathElement)ctor.newInstance(new Object[] {});
                        
                        //Layouts
                        fis.read(temp.array(), 0, 8);  //x
                        elem.getView().setLayoutX(temp.getDouble(0));
                        fis.read(temp.array(), 0, 8);  //y
                        elem.getView().setLayoutY(temp.getDouble(0));
                        fis.read(temp.array(), 0, 8);  //rotation
                        elem.getView().setRotate(temp.getDouble(0));
                        
                        for(ShemeElement.Parameter p:elem.getParameters()){
                            fis.read(temp.array(), 0, 8); //pram val
                            p.setValue(temp.getDouble(0));
                        }
                        this.MathElemList.add(elem);
                        inpConts.addAll(elem.getInputs());
                        outConts.addAll(elem.getOutputs());
                    }catch(ClassNotFoundException|NoSuchMethodException|InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex){
                        raschetkz.RaschetKz.layoutString(ex.getStackTrace().toString());
                    }
                }
                
                //Math connections
                //num of connections
                fis.read(temp.array(),0,4);
                numOfElems=temp.getInt(0);
                for(int i=0;i<numOfElems;i++){
                    //index of source (out)
                    fis.read(temp.array(),0,4);
                    int outIndx=temp.getInt(0);
                    //index of destin (inp)
                    fis.read(temp.array(),0,4);   
                    int inpIndx=temp.getInt(0);
                    //creation !?
//                    this.mathConnList.add(new MathWire(outConts.get(outIndx),inpConts.get(inpIndx)));
                }
                
                fis.close();
            }catch(FileNotFoundException fnf){
                raschetkz.RaschetKz.layoutString("Файл не существует");
            }
            catch(IOException io){
                raschetkz.RaschetKz.layoutString("стриму жопа");
            }
        }       

        public List<ElectricWire> GetWires() {
            return(this.BranchList);
        }

        public List<ShemeElement> GetElems() {
            return(this.ElementList);
        }
        
        public List<MathElement> GetMathElems() {
            return(this.MathElemList);
        }
        
        Pane getDrawBoard(){
            return this.draws;
        }

//        public double getDT(){
//            return this.dt;
//        }
//        
//        public double getTEnd(){
//            return this.tend;
//        }
        
        String getName() {
            return(this.fileName);
        }

    /**
     * @return the mathConnList
     */
    public List<MathWire> getMathConnList() {
        return mathConnList;
    }

    /**
     * @return the dt
     */
    public SimpleDoubleProperty getDt() {
        return dt;
    }

    /**
     * @return the tend
     */
    public SimpleDoubleProperty getTend() {
        return tend;
    }

    /**
     * @return the solver
     */
    public SimpleStringProperty getSolver() {
        return solver;
    }

    /**
     * @return the jacobianEstimationType
     */
    public int getJacobianEstimationType() {
        return jacobianEstimationType;
    }

    /**
     * @param jacobianEstimationType the jacobianEstimationType to set
     */
    public void setJacobianEstimationType(int jacobianEstimationType) {
        this.jacobianEstimationType = jacobianEstimationType;
    }
                                
    }
