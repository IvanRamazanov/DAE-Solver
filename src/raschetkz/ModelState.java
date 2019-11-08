/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raschetkz;

import Connections.MathWire;
import Connections.MechWire;
import Connections.Wire;
import ElementBase.MathElement;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem.Subsystem;
import MathPack.Parser;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Иван
 */
public class ModelState{
    private Subsystem mainSystem;
    private SimpleStringProperty solver;
    private SimpleDoubleProperty dt,tend,AbsTol,RelTol;
    private int jacobianEstimationType;
    private String filePath;
    private final static SimpleBooleanProperty simplyfingFlag=new SimpleBooleanProperty(false);

    ModelState(){
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
            StringBuilder sb=new StringBuilder();
            sb.append("<config>");sb.append("\r\n");

            sb.append("<Solver>");
            sb.append(getSolver().getValue());
            sb.append("</Solver>");sb.append("\r\n");

            sb.append("<tEnd>");
            sb.append(Double.toString(getTend().doubleValue()));
            sb.append("</tEnd>");sb.append("\r\n");

            sb.append("<dt>");
            sb.append(Double.toString(getDt().doubleValue()));
            sb.append("</dt>");sb.append("\r\n");

            sb.append("<JacobEsim>");
            sb.append(Integer.toString(getJacobianEstimationType()));
            sb.append("</JacobEsim>");sb.append("\r\n");

            sb.append("<AbsTolerance>");
            sb.append(Double.toString(getAbsTol().get()));
            sb.append("</AbsTolerance>");sb.append("\r\n");

            sb.append("<RelativeTolerance>");
            sb.append(Double.toString(getRelTol().get()));
            sb.append("</RelativeTolerance>");sb.append("\r\n");

            sb.append("<TryReduce>");
            sb.append(getSimplyfingFlag().getValue().toString());
            sb.append("</TryReduce>");sb.append("\r\n");

            sb.append("<WindowSize>");
            sb.append("["+getMainSystem().getWindowWidth()+" "+getMainSystem().getWindowHeight()+"]");
            sb.append("</WindowSize>");sb.append("\r\n");

            sb.append("</config>");sb.append("\r\n");
            bw.write(Parser.formatBlock(sb.toString()));

            // main subsystem
            sb=new StringBuilder("<MainSystem>");sb.append("\r\n");
            sb.append(getMainSystem().save());
            sb.append("</MainSystem>");
            bw.write(Parser.formatBlock(sb.toString()));

        }catch(Exception ex){
            ex.printStackTrace(System.err);
        }
    }

    public void Save(String filePath){
        this.setFileName(filePath);
        save();
    }

    public void clearState(){
        getMainSystem().delete();

//        for(int i=getMainSystem().getElementList().size()-1;i>=0;i--)
//            getMainSystem().getElementList().get(i).delete();
//        for(int i = getMainSystem().getAllWires().size()-1; i>=0; i--)  // TODO empty list?
//            getMainSystem().getAllWires().get(i).delete();
        setFileName(null);
    }

    public void load(String filePath){
        try (Scanner br = new Scanner(new File(filePath).toPath())) {
            clearState();
            setFileName(filePath);

            Path p=new File(filePath).toPath();

            parseConfig(Parser.getBlock(p,"<config>"));

            getMainSystem().configurate(Parser.getBlock(p,"<MainSystem>"));

        }catch(IOException io){
            io.printStackTrace(System.err);
        }
    }

    public List<Wire> getElectroWires() {
//        List<Wire> out=new ArrayList<>();
//        for(Wire w:getMainSystem().getAllWires()){
//            if(w instanceof ElectricWire || w instanceof ThreePhaseWire)
//                out.add(w);
//        }
//        return out;
        return getMainSystem().getElectroWires();
    }

    private void parseConfig(String config){
//        String[] lines = config.split("\r\n");

        String solverName=Parser.getKeyValue(config,"<Solver>");
        solver.set(solverName);

        String tEnd=Parser.getKeyValue(config,"<tEnd>");
        tend.set(Double.valueOf(tEnd));

        String dts=Parser.getKeyValue(config,"<dt>");
        dt.set(Double.valueOf(dts));

        String jac=Parser.getKeyValue(config,"<JacobEsim>");
        setJacobianEstimationType(Integer.valueOf(jac));

        String aTol=Parser.getKeyValue(config,"<AbsTolerance>");
        if(aTol!=null)
            getAbsTol().set(Double.valueOf(aTol));

        String rTol=Parser.getKeyValue(config,"<RelativeTolerance>");
        if(rTol!=null)
            getRelTol().set(Double.valueOf(rTol));

        String reduceFlag=Parser.getKeyValue(config,"<TryReduce>");
        if(reduceFlag!=null)
            getSimplyfingFlag().set(Boolean.valueOf(reduceFlag));

        double[] wxy=Parser.parseRow(Parser.getKeyValue(config,"<WindowSize>"));
        if(wxy!=null) {
            getMainSystem().getStage().setWidth(wxy[0]);
            getMainSystem().getStage().setHeight(wxy[1]);
        }
    }



    public List<MathElement> getMathElements(){
//        List<MathElement> out=new ArrayList<>();
//        for(Element elem:getMainSystem().getAllElements()){
//            if(elem instanceof MathElement)
//                out.add((MathElement) elem);
//        }
//        return out;
        return getMainSystem().getMathElements();
    }

    public List<MechWire> getMechWires(){
//        List<MechWire> out=new ArrayList<>();
//        for(Wire w:getMainSystem().getAllWires()){
//            if(w instanceof MechWire)
//                out.add((MechWire) w);
//        }
//        return out;
        return getMainSystem().getMechWires();
    }

    public List<SchemeElement> getSchemeElements(){
//        List<SchemeElement> out=new ArrayList<>();
//        for(Element elem:getMainSystem().getAllElements()){
//            if(elem instanceof SchemeElement)
//                out.add((SchemeElement) elem);
//        }
//        return out;
        return getMainSystem().getSchemeElements();
    }

    public Subsystem getMainSystem(){
        return mainSystem;
    }

    /**
     * @return the mathConnList
     */
    public List<MathWire> getMathConnList() {
//        List<MathWire> out=new ArrayList<>();
//        for(Wire w:getMainSystem().getAllWires()){
//            if(w instanceof MathWire)
//                out.add((MathWire) w);
//        }
//        return out;
        return getMainSystem().getMathConnList();
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

}

