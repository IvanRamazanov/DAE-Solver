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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    private ArrayList<Wire> wireList;
    private Pane draws;
    private SimpleStringProperty solver;
    private SimpleDoubleProperty dt,tend,AbsTol,RelTol;
    private int jacobianEstimationType;
    private String filePath;

    ModelState(){
        elementList=new ArrayList();
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

    public void save(){
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

            int cnt=0;
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

            br.useDelimiter(System.lineSeparator());
            String line;
            while(br.hasNext()){
                line=br.next();
                if(line.equals("<config>")){
                    StringBuilder config=new StringBuilder();
                    while(!(line=br.next()).equals("</config>")){
                        config.append(line+System.lineSeparator());
                    }
                    parseConfig(config.toString());
                    continue;
                }
                if(line.equals("<Elements>")){
                    while(!(line=br.next()).equals("</Elements>")) {
                        String elemName=line,end="</"+elemName.substring(1);
                        StringBuilder elementInfo=new StringBuilder();
                        while (!(line=br.next()).equals(end)){
                            elementInfo.append(line+System.lineSeparator());
                        }
                        String name=elemName.substring(1,elemName.length()-1);
                        Element elem=parseElement(name,elementInfo.toString());

                        getElementList().add(elem);
                    }
                    continue;
                }
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
    }

    private void parseWire(String wireInfo){
        String className=wireInfo.substring(wireInfo.lastIndexOf("<ClassName>")+"<ClassName>".length(),wireInfo.indexOf("</ClassName>"));
        Class<?> clas= null;
        try {
            clas = Class.forName(className);
            Constructor<?> ctor=clas.getConstructor();
            Wire w=(Wire)ctor.newInstance(new Object[] {});
            getWireList().add(w);
            w.configure(wireInfo);
        } catch (Exception e) {
            e.printStackTrace(System.err);
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
            e.printStackTrace(System.err);
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

