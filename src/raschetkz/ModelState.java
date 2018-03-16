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

import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

import Elements.Environment.Subsystem.Subsystem;
import MathPack.Parser;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Иван
 */
public class ModelState{
    private List<Element> elementList;
    private ArrayList<Wire> wireList;
    private Subsystem mainSystem;
    private SimpleStringProperty solver;
    private SimpleDoubleProperty dt,tend,AbsTol,RelTol;
    private int jacobianEstimationType;
    private String filePath;
    private final static SimpleBooleanProperty simplyfingFlag=new SimpleBooleanProperty(false);

    ModelState(){
        elementList=new ArrayList();
        wireList=new ArrayList<>();
        mainSystem=new Subsystem();
        dt=new SimpleDoubleProperty();
        tend=new SimpleDoubleProperty();
        solver=new SimpleStringProperty();
        AbsTol=new SimpleDoubleProperty();
        RelTol=new SimpleDoubleProperty();
        solver.setValue("Adams4");
        jacobianEstimationType=2;
    }

    public static SimpleBooleanProperty getSimplyfingFlag() {
        return simplyfingFlag;
    }

    public void save(){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filePath)))) {
            bw.write("<config>");bw.newLine();

            bw.write("<Solver>");
            bw.write(getSolver().getValue());
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

            bw.write("<AbsTolerance>");
            bw.write(Double.toString(getAbsTol().get()));
            bw.write("</AbsTolerance>");bw.newLine();

            bw.write("<RelativeTolerance>");
            bw.write(Double.toString(getRelTol().get()));
            bw.write("</RelativeTolerance>");bw.newLine();

            bw.write("<TryReduce>");
            bw.write(getSimplyfingFlag().getValue().toString());
            bw.write("</TryReduce>");bw.newLine();

            bw.write("<WindowSize>");
            bw.write("["+getMainSystem().getWindowWidth()+" "+getMainSystem().getWindowHeight()+"]");
            bw.write("</WindowSize>");bw.newLine();

            bw.write("</config>");bw.newLine();


            bw.write("<Elements>");bw.newLine();
            int cnt=0;
            for(Element elem:getElementList()){
                //bw.write("<Elem"+cnt+">");bw.newLine();
                bw.write(elem.save().toString());
                //bw.write("</Elem"+cnt+">");bw.newLine();
                //cnt++;
            }
            bw.write("</Elements>");bw.newLine();

            bw.write("<WireList>");bw.newLine();

            cnt=0;
            for(Wire w: getWireList()){
                bw.write("<wire"+cnt+">");bw.newLine();
                bw.write(w.save());
                bw.write("</wire"+cnt+">");bw.newLine();
                cnt++;
            }

            bw.write("</WireList>");bw.newLine();

        }catch(Exception ex){
            ex.printStackTrace(System.err);
        }
    }

    public void Save(String filePath){
        this.setFileName(filePath);
        save();
    }

    public void clearState(){
        for(int i=getElementList().size()-1;i>=0;i--)
            getElementList().get(i).delete();
        for(int i = getWireList().size()-1; i>=0; i--)
            getWireList().get(i).delete();
        setFileName(null);
    }

    public void load(String filePath){
        try (Scanner br = new Scanner(new File(filePath).toPath())) {
            clearState();
            setFileName(filePath);

            Path p=new File(filePath).toPath();

            parseConfig(Parser.getBlock(p,"<config>"));

            AtomicReference<String> elems=new AtomicReference<>(Parser.getBlock(p,"<Elements>"));
            if(!elems.get().isEmpty()){
                while(!elems.get().isEmpty()){
                    String blockName=elems.get().substring(0,elems.get().indexOf('>')+1),
                    elemName=blockName.substring(1,blockName.length()-1),
                            elemInfo=Parser.getBlock(p,blockName);

                    Element elem=parseElement(elemName,elemInfo,elems);
                }
            }


            br.useDelimiter(System.lineSeparator());
            String line;
            while(br.hasNext()){
                line=br.next();
                if(line.equals("<WireList>")){
                    while(!(line=br.next()).equals("</WireList>")) {
                        String wire=line,end="</"+wire.substring(1);
                        StringBuilder wireInfo=new StringBuilder();
                        while (!(line=br.next()).equals(end)){
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

        String aTol=Parser.getKeyValue(lines,"<AbsTolerance>");
        if(aTol!=null)
            getAbsTol().set(Double.valueOf(aTol));

        String rTol=Parser.getKeyValue(lines,"<RelativeTolerance>");
        if(rTol!=null)
            getRelTol().set(Double.valueOf(rTol));

        String reduceFlag=Parser.getKeyValue(lines,"<TryReduce>");
        if(reduceFlag!=null)
            getSimplyfingFlag().set(Boolean.valueOf(reduceFlag));

        double[] wxy=Parser.parseRow(Parser.getKeyValue(lines,"<WindowSize>"));
        if(wxy!=null) {
            getMainSystem().getStage().setWidth(wxy[0]);
            getMainSystem().getStage().setHeight(wxy[0]);
        }
    }

    private void parseWire(String wireInfo){
        String className=wireInfo.substring(wireInfo.lastIndexOf("<ClassName>")+"<ClassName>".length(),wireInfo.indexOf("</ClassName>"));

        String sysName=Parser.getFirstKeyValue(wireInfo,"<Subsystem>");

        Subsystem sys;

        if(sysName.isEmpty()){
            sys=mainSystem;
        }else{
            Element e=Element.findElement(sysName);
            sys=(Subsystem) e;
        }

        Class<?> clas= null;
        try {
            clas = Class.forName(className);
            Constructor<?> ctor=clas.getConstructor(Subsystem.class);
            Wire w=(Wire)ctor.newInstance(sys);
//            getWireList().add(w);
            w.configure(wireInfo);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private Element parseElement(String name, String elemInfo, AtomicReference<String> elemBlock){
//        String className=elemInfo.substring(elemInfo.indexOf("<ClassName>")+"<ClassName>".length(),elemInfo.indexOf("</ClassName>"));
        String className=Parser.getFirstKeyValue(elemInfo,"<ClassName>");

        String sysName=Parser.getFirstKeyValue(elemInfo,"<Subsystem>");

        Subsystem sys;

        if(sysName.isEmpty()){
            sys=mainSystem;
        }else{
            Element e=Element.findElement(sysName);
            if(e==null){
                // create subsys
                String sysInfo=Parser.getBlock(elemBlock.get(),"<"+sysName+">");
                Element subsys=parseElement(sysName,sysInfo,elemBlock);
                sys=(Subsystem)subsys;
            }else
                sys=(Subsystem) e;
        }

        Element elem=null;
        try {
            Class<?> clas = Class.forName(className);
            Constructor<?> ctor=clas.getConstructor(Subsystem.class);
            elem=(Element)ctor.newInstance(sys);
            elem.configurate(name,elemInfo);

            getElementList().add(elem);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        // reduce block
        String ls=System.lineSeparator();
        String rep="<"+name+">"+ls+elemInfo+"</"+name+">"+ls;
        elemBlock.set(elemBlock.get().replace(rep,""));

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

    Subsystem getMainSystem(){
        return mainSystem;
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

