/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raschetkz;

import Connections.MathWire;

import Connections.ElectricWire;
import Connections.MechWire;
import Connections.Wire;
import ElementBase.*;
import ElementBase.SchemeElement;

import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import MathPack.Parser;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;

/**
 *
 * @author Иван
 */
public class ModelState{
    private List<Element> elementList;
//    private List<MathElement> MathElemList;
//    private List<MathWire> mathConnList;
//    private List<ElectricWire> BranchList;
//    private List<MechWire> mechWires;
    private ArrayList<Wire> wireList;
    private Pane draws;
    private SimpleStringProperty solver;
    private SimpleDoubleProperty dt,tend,AbsTol,RelTol;
    private int jacobianEstimationType;
    private String filePath;

    ModelState(){
        elementList=new ArrayList();
//        MathElemList=new ArrayList();
//        BranchList=new ArrayList();
//        mathConnList=new ArrayList();
//        mechWires=new ArrayList<>();
        wireList=new ArrayList<>();
        draws=new Pane();
        dt=new SimpleDoubleProperty();
        tend=new SimpleDoubleProperty();
        solver=new SimpleStringProperty();
        AbsTol=new SimpleDoubleProperty();
        RelTol=new SimpleDoubleProperty();
        solver.setValue("Adams4");
        jacobianEstimationType=2;
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

    public void SaveString(){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filePath)))) {
            bw.write("<config>");bw.newLine();

            bw.write("<Solver>");
            bw.write(getSolver().getValue()); // TODO empty string
            bw.write("</Solver>");bw.newLine();

            bw.write("<tEnd>");
            bw.write(Double.toString(getTend().doubleValue()));
            bw.write("</tEnd>");bw.newLine();

            bw.write("<dt>");
            bw.write(Double.toString(getDt().doubleValue()));
            bw.write("</dt>");bw.newLine();

            bw.write("<JacobEsim>");
            bw.write(Integer.toString(getJacobianEstimationType()));
            bw.write("</JacobEsim>");bw.newLine();

            bw.write("</config>");bw.newLine();


            bw.write("<Elements>");bw.newLine();
            for(Element elem:getElementList()){
                elem.save(bw);
            }
            bw.write("</Elements>");bw.newLine();

            bw.write("<WireList>");bw.newLine();
//            List<Wire> wireList=new ArrayList<>();
//            wireList.addAll(BranchList);
//            wireList.addAll(mathConnList);
//            wireList.addAll(mechWires);
            int cnt=0;
            for(Wire w: getWireList()){
//                w.Save(baos, ECList);
                bw.write("<wire"+cnt+">");bw.newLine();
                w.save(bw);
                bw.write("</wire"+cnt+">");bw.newLine();
                cnt++;
            }

            bw.write("</WireList>");bw.newLine();

        }catch(Exception ex){
            ex.printStackTrace(System.err);
        }
    }


//    public void Save(){
//        //this.init();
//        ByteArrayOutputStream baos=new ByteArrayOutputStream();
//        ByteBuffer temp=ByteBuffer.allocate(8);
//
//        //solver info
//        for(char c:getSolver().get().toCharArray()){
//            temp.putChar(0,c);
//            baos.write(temp.array(), 0, 2);
//        }
//        temp.putChar(0,'\0');
//        baos.write(temp.array(), 0, 2);
//        temp.putDouble(0, getDt().doubleValue()); // dt
//        baos.write(temp.array(), 0, 8);
//        temp.putDouble(0, getTend().doubleValue()); // t_end
//        baos.write(temp.array(), 0, 8);
//        temp.putInt(0, getJacobianEstimationType()); // jacobian estimation
//        baos.write(temp.array(), 0, 4);
//
//        //elements
////            List<MathInPin> inpConts=new ArrayList();
//        List<MathPin> mPins=new ArrayList();
//
//        temp.putInt(0, this.ElementList.size());    //num of elems
//        baos.write(temp.array(), 0, 4);
//        for(SchemeElement shE:this.ElementList){
//            //name
//            for(char c:shE.getClass().getName().toCharArray()){
//                temp.putChar(0,c);
//                baos.write(temp.array(), 0, 2);
//            }
//            temp.putChar(0,'\0');
//            baos.write(temp.array(), 0, 2);
//            //layouts
//            temp.putDouble(0, shE.getView().getLayoutX());
//            baos.write(temp.array(), 0, 8);
//            temp.putDouble(0, shE.getView().getLayoutY());
//            baos.write(temp.array(), 0, 8);
//            //rotation
//            temp.putDouble(0, shE.getRotation());
//            baos.write(temp.array(), 0, 8);
//            //params
//            for(SchemeElement.Parameter p:shE.getParameters()){
//                temp.putDouble(0, p.getDoubleValue());
//                baos.write(temp.array(), 0, 8);
//            }
//            //inits
//            for(Element.InitParam ip:shE.getInitials()){
//                temp.putDouble(0, ip.getDoubleValue());
//                baos.write(temp.array(), 0, 8);// value
//                if(ip.getPriority()){
//                    temp.putInt(0,1);
//                    baos.write(temp.array(), 0, 4);
//                }else{
//                    temp.putInt(0,0);
//                    baos.write(temp.array(), 0, 4);
//                }
//            }
//
//            mPins.addAll(shE.getInputs());
//            mPins.addAll(shE.getOutputs());
//        }
//
//        //Wires
//        List<ElemPin> ECList=new ArrayList();
//        for(SchemeElement e:this.ElementList){
//            ECList.addAll(e.getElemContactList());
//        }
//
//        List<ElemMechPin> mechPinList=new ArrayList();
//        for(SchemeElement e:this.ElementList){
//            mechPinList.addAll(e.getMechContactList());
//        }
//
//        //num of wires
//        temp.putInt(0, this.BranchList.size());
//        baos.write(temp.array(), 0, 4);
//
//        for(ElectricWire w:this.BranchList){
//            w.Save(baos, ECList);
//        }
//
//        //math elems
//        temp.putInt(0, this.MathElemList.size());    //num of elems
//        baos.write(temp.array(), 0, 4);
//        for(MathElement mathE:this.MathElemList){
//            //name
//            for(char c:mathE.getClass().getName().toCharArray()){
//                temp.putChar(0,c);
//                baos.write(temp.array(), 0, 2);
//            }
//            temp.putChar(0,'\0');
//            baos.write(temp.array(), 0, 2);
//            //layouts
//            temp.putDouble(0, mathE.getView().getLayoutX());
//            baos.write(temp.array(), 0, 8);
//            temp.putDouble(0, mathE.getView().getLayoutY());
//            baos.write(temp.array(), 0, 8);
//            //rotation
//            temp.putDouble(0, mathE.getView().getRotate());
//            baos.write(temp.array(), 0, 8);
//            //params
//            for(SchemeElement.Parameter p:mathE.getParameters()){
//                temp.putDouble(0, p.getDoubleValue());
//                baos.write(temp.array(), 0, 8);
//            }
//
//            mPins.addAll(mathE.getInputs());
//            mPins.addAll(mathE.getOutputs());
//        }
//
//        //MathWires
//        //num of wires
//        temp.putInt(0, this.mathConnList.size());
//        baos.write(temp.array(), 0, 4);
//
//        for(MathWire w:mathConnList){
//            w.Save(baos, mPins);
//        }
//
//        // Mechanical wires
//        //num of wires
//        temp.putInt(0, mechWires.size());
//        baos.write(temp.array(), 0, 4);
//
//        for(MechWire w:mechWires){
//            w.Save(baos, mechPinList);
//        }
//
//        //write to file
//
//        try (FileOutputStream fos = new FileOutputStream(getFilePath())) {
//            baos.writeTo(fos);
//        }
//        catch(IOException io){
//            System.err.println(io.getMessage());
//        }
//    }

    public void Save(String filePath){
        this.setFileName(filePath);
        SaveString();
    }

    public void clearState(){
//        for(int i=BranchList.size()-1;i>=0;i--)
//            BranchList.get(i).delete();
        for(int i=getElementList().size()-1;i>=0;i--)
            getElementList().get(i).delete();
//        for(int i=MathElemList.size()-1;i>=0;i--)
//            MathElemList.get(i).delete();
//        for(int i=mathConnList.size()-1;i>=0;i--)
//            mathConnList.get(i).delete();
//        for(int i=mechWires.size()-1;i>=0;i--)
//            mechWires.get(i).delete();
        for(int i = getWireList().size()-1; i>=0; i--)
            getWireList().get(i).delete();
        setFileName(null);
    }

    public void LoadString(String filePath){
        try (BufferedReader br = Files.newBufferedReader(new File(filePath).toPath())) {
            clearState();
            setFileName(filePath);

            String line=null;
            while((line=br.readLine())!=null){
                if(line.equals("<config>")){
                    String config="";
                    while(!(line=br.readLine()).equals("</config>")){
                        config+=line+System.lineSeparator();
                    }
                    parseConfig(config);
                    continue;
                }
                if(line.equals("<Elements>")){
                    while(!(line=br.readLine()).equals("</Elements>")) {
                        String elemName=line,end="</"+elemName.substring(1);
                        StringBuilder elementInfo=new StringBuilder();
                        while (!(line=br.readLine()).equals(end)){
                            elementInfo.append(line+System.lineSeparator());
                        }
                        String name=elemName.substring(1,elemName.length()-1);
                        Element elem=parseElement(name,elementInfo.toString());

                        getElementList().add(elem);
                    }
                    continue;
                }
                if(line.equals("<WireList>")){
                    while(!(line=br.readLine()).equals("</WireList>")) {
                        String wire=line,end="</"+wire.substring(1);
                        StringBuilder wireInfo=new StringBuilder();
                        while (!(line=br.readLine()).equals(end)){
                            wireInfo.append(line+System.lineSeparator());
                        }


                        parseWire(wireInfo.toString());
                    }
                }
            }
        }catch(IOException io){
            io.printStackTrace(System.err);
        }
    }

//    public void Load(String filePath){
//        try{
//            FileInputStream fis = new FileInputStream(filePath);
//            clearState();
//            setFileName(filePath);
//
//            ByteBuffer temp=ByteBuffer.allocate(8);
//
//            //solver
//            String sTemp=new String();
//            while(true){            //solver type
//                fis.read(temp.array(),0,2);
//                if(temp.getChar(0)!='\0'){
//                    sTemp+=temp.getChar(0);
//                }else{
//                    break;
//                }
//            }
//            solver.set(sTemp);
//            fis.read(temp.array(), 0, 8);  //dt
//            dt.set(temp.getDouble(0));
//            fis.read(temp.array(), 0, 8);  //tend
//            tend.set(temp.getDouble(0));
//            fis.read(temp.array(), 0, 4);  //tend
//            setJacobianEstimationType(temp.getInt(0));
//
//            //Elements
//            List<MathPin> mPins=new ArrayList();
//
//            fis.read(temp.array(), 0, 4);  //num of elems
//            int numOfElems=temp.getInt(0);
//
//            for(int i=0;i<numOfElems;i++){
//                sTemp="";
//                while(true){            //Elem type
//                    fis.read(temp.array(),0,2);
//                    if(temp.getChar(0)!='\0'){
//                        sTemp+=temp.getChar(0);
//                    }else{
//                        break;
//                    }
//                }
//                try{
//                    //creation
//                    Class<?> clas=Class.forName(sTemp);
//                    Constructor<?> ctor=clas.getConstructor();
//                    SchemeElement elem=(SchemeElement)ctor.newInstance(new Object[] {});
//
//                    //Layouts
//                    fis.read(temp.array(), 0, 8);  //x
//                    elem.getView().setLayoutX(temp.getDouble(0));
//                    fis.read(temp.array(), 0, 8);  //y
//                    elem.getView().setLayoutY(temp.getDouble(0));
//                    fis.read(temp.array(), 0, 8);  //rotation
//                    elem.setRotation(temp.getDouble(0));
//
//                    for(SchemeElement.Parameter p:elem.getParameters()){
//                        fis.read(temp.array(), 0, 8); //pram val
////                        p.setValue(temp.getDouble(0));
//                    }
//                    for(SchemeElement.InitParam p:elem.getInitials()){
//                        fis.read(temp.array(), 0, 8); //init val
////                        p.setValue(temp.getDouble(0));
//                        fis.read(temp.array(), 0, 4); //priority
//                        if(temp.getInt(0)==1)
//                            p.setPriority(true);
//                        else
//                            p.setPriority(false);
//                    }
//                    this.ElementList.add(elem);
//                    mPins.addAll(elem.getInputs());
//                    mPins.addAll(elem.getOutputs());
//                }catch(ClassNotFoundException|NoSuchMethodException|InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex){
//                    raschetkz.RaschetKz.layoutString(ex.getStackTrace().toString());
//                }
//            }
//
//
//
//            //Wires
//            List<ElemPin> ECList=new ArrayList();
//            for(SchemeElement e:this.ElementList){
//                ECList.addAll(e.getElemContactList());
//            }
//            List<ElemMechPin> mechPinList=new ArrayList();
//            for(SchemeElement e:this.ElementList){
//                mechPinList.addAll(e.getMechContactList());
//            }
//
//            //num of wires
//            fis.read(temp.array(),0,4);
//            numOfElems=temp.getInt(0);
//            //wire cycle
//            for(int i=0;i<numOfElems;i++){
//                BranchList.add(new ElectricWire(fis,ECList));
//            }
//
//            //math elems
//
//            //num of elems
//            fis.read(temp.array(),0,4);
//            numOfElems=temp.getInt(0);
//            for(int i=0;i<numOfElems;i++){
//                sTemp="";
//                while(true){            //Elem type
//                    fis.read(temp.array(),0,2);
//                    if(temp.getChar(0)!='\0'){
//                        sTemp+=temp.getChar(0);
//                    }else{
//                        break;
//                    }
//                }
//                try{
//                    //creation
//                    Class<?> clas=Class.forName(sTemp);
//                    Constructor<?> ctor=clas.getConstructor();
//                    MathElement elem=(MathElement)ctor.newInstance(new Object[] {});
//
//                    //Layouts
//                    fis.read(temp.array(), 0, 8);  //x
//                    elem.getView().setLayoutX(temp.getDouble(0));
//                    fis.read(temp.array(), 0, 8);  //y
//                    elem.getView().setLayoutY(temp.getDouble(0));
//                    fis.read(temp.array(), 0, 8);  //rotation
//                    elem.setRotation(temp.getDouble(0));
//
//                    for(SchemeElement.Parameter p:elem.getParameters()){
//                        fis.read(temp.array(), 0, 8); //pram val
////                        p.setValue(temp.getDouble(0));
//                    }
//                    this.MathElemList.add(elem);
//                    mPins.addAll(elem.getInputs());
//                    mPins.addAll(elem.getOutputs());
//                }catch(ClassNotFoundException|NoSuchMethodException|InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex){
//                    raschetkz.RaschetKz.layoutString(ex.getStackTrace().toString());
//                }
//            }
//
//            //Math connections
//            //num of connections
//            fis.read(temp.array(),0,4);
//            numOfElems=temp.getInt(0);
//            for(int i=0;i<numOfElems;i++){
//                mathConnList.add(new MathWire(fis,mPins));
//            }
//
//            //num of wires
//            fis.read(temp.array(),0,4);
//            numOfElems=temp.getInt(0);
//            //wire cycle
//            for(int i=0;i<numOfElems;i++){
//                mechWires.add(new MechWire(fis,mechPinList));
//            }
//
//            fis.close();
//        }catch(FileNotFoundException fnf){
//            raschetkz.RaschetKz.layoutString("Файл не существует");
//        }
//        catch(IOException io){
//            raschetkz.RaschetKz.layoutString("стриму жопа");
//        }
//    }

    public List<ElectricWire> getElectroWires() {
        List<ElectricWire> out=new ArrayList<>();
        for(Wire w:getWireList()){
            if(w instanceof ElectricWire)
                out.add((ElectricWire)w);
        }
        return out;
    }

    private void parseConfig(String config){
        String[] lines = config.split(System.getProperty("line.separator"));

        String solverName=Parser.getKeyValue(lines,"<Solver>");
        solver.set(solverName);

        String tEnd=Parser.getKeyValue(lines,"<tEnd>");
        tend.set(Double.valueOf(tEnd));

        String dts=Parser.getKeyValue(lines,"<dt>");
        dt.set(Double.valueOf(dts));

        String jac=Parser.getKeyValue(lines,"<JacobEsim>");
        setJacobianEstimationType(Integer.valueOf(jac));
    }

    private void parseWire(String wireInfo){
        //String[] lines = config.split(System.getProperty("line.separator"));

        String className=wireInfo.substring(wireInfo.lastIndexOf("<ClassName>")+"<ClassName>".length(),wireInfo.indexOf("</ClassName>"));
        Class<?> clas= null;
        try {
            clas = Class.forName(className);
            Constructor<?> ctor=clas.getConstructor();
            Wire w=(Wire)ctor.newInstance(new Object[] {});
            getWireList().add(w);
            w.configure(wireInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Element parseElement(String elementName,String elemInfo){
        String className=elemInfo.substring(elemInfo.indexOf("<ClassName>")+"<ClassName>".length(),elemInfo.indexOf("</ClassName>"));
        Element elem=null;
        try {
            Class<?> clas = Class.forName(className);
            Constructor<?> ctor=clas.getConstructor();
            elem=(Element)ctor.newInstance(new Object[] {});
            elem.configurate(elementName,elemInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elem;
    }

    public List<MathElement> getMathElements(){
        List<MathElement> out=new ArrayList<>();
        for(Element elem:getElementList()){
            if(elem instanceof MathElement)
                out.add((MathElement) elem);
        }
        return out;
    }

    public List<MechWire> getMechWires(){
        List<MechWire> out=new ArrayList<>();
        for(Wire w:getWireList()){
            if(w instanceof MechWire)
                out.add((MechWire) w);
        }
        return out;
    }

    public List<Element> getElementList() {
        return(this.elementList);
    }

    public List<SchemeElement> getSchemeElements(){
        List<SchemeElement> out=new ArrayList<>();
        for(Element elem:getElementList()){
            if(elem instanceof SchemeElement)
                out.add((SchemeElement) elem);
        }
        return out;
    }

//    public List<MathElement> getMathElem() {
//        return(this.MathElemList);
//    }

    Pane getDrawBoard(){
        return this.draws;
    }

    /**
     * @return the mathConnList
     */
    public List<MathWire> getMathConnList() {
        List<MathWire> out=new ArrayList<>();
        for(Wire w:getWireList()){
            if(w instanceof MathWire)
                out.add((MathWire) w);
        }
        return out;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFileName(String fileName) {
        this.filePath = fileName;
    }

    public SimpleDoubleProperty getAbsTol() {
        return AbsTol;
    }

    public SimpleDoubleProperty getRelTol() {
        return RelTol;
    }

    public ArrayList<Wire> getWireList() {
        return wireList;
    }
}

