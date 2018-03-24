package Elements.Environment.Subsystem;

import Connections.*;
import ElementBase.*;
import Elements.Environment.ElectricPass.ElectricPass;
import Elements.Environment.MathInPass.MathInPass;
import Elements.Environment.MathOutPass.MathOutPass;
import Elements.Environment.MechPass.MechPass;
import Elements.Environment.ThreePhasePass.ThreePhasePass;
import MathPack.Parser;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class Subsystem extends Element {
    final private Pane drawBoard=new Pane();
    final private List<Element> elementList=new ArrayList<>();
    final private List<Wire> wireList=new ArrayList<>();
    private Stage stage;
    private final ScrollPane scrollPane =new ScrollPane(getDrawBoard());
    private SelectionModel selectionModel;
    private int leftPinCnt;
    private int rightPinCnt;
    private static final int pinOffset=10;
    private static final int pinStep=20;
    private double windowHeight=600;
    private double windowWidth=800;

    public Subsystem(){
        initGui();
        name=new Label();
    }

    public Subsystem(Subsystem sys){
        super(sys);

        initGui();
    }

    public Subsystem(boolean val){
        super(val);
    }

    public Subsystem(Subsystem oldSys, List<Element> childrens){
        this(oldSys);

        double minX=Double.MAX_VALUE,minY=Double.MAX_VALUE;

        List<Wire> blackList=new ArrayList<>();
        for(Element elem:childrens){
            // check pins
            for(Pin pin:elem.getAllPins()){
                if(pin.getItsConnection()!=null){
                    Wire pinWire=pin.getItsConnection().getWire();
                    if(blackList.indexOf(pinWire)==-1){
                        blackList.add(pinWire);
                        for(LineMarker lm:pinWire.getWireContacts()){
                            if(lm.getItsConnectedPin()!=null){
                                Element e=lm.getItsConnectedPin().getOwner();
                                if(childrens.indexOf(e)==-1&&
                                        childrens.indexOf(e.getItsSystem())==-1){  // elem outside subsystem
                                    // entermediate its connection
                                    addPass(oldSys,lm);
                                }
                            }
                        }

                        // migrate wire into subsystem drawBoard
                        oldSys.getDrawBoard().getChildren().remove(pinWire.getView());
                        oldSys.getWireList().remove(pinWire);
                        getDrawBoard().getChildren().addAll(pinWire.getView());
                        pinWire.setItsSystem(this);
                        getWireList().add(pinWire);
                    }
                }
            }
            oldSys.getDrawBoard().getChildren().remove(elem.getView());
            oldSys.getElementList().remove(elem);
            elem.setItsSystem(this);
            getElementList().add(elem);

            double x=elem.getView().getLayoutX(),y=elem.getView().getLayoutY();
            if(x<minX)
                minX=x;
            if(y<minY)
                minY=y;
        }
        double dx=minX-50,dy=minY-50;
        for(Element elem:childrens){
            double x=elem.getView().getLayoutX(),
                    y=elem.getView().getLayoutY();
            elem.getView().setLayoutX(x-dx);
            elem.getView().setLayoutY(y-dy);
        }

        getView().setLayoutX(minX);
        getView().setLayoutY(minY);
    }

    public static int getPinOffset() {
        return pinOffset;
    }

    public static int getPinStep() {
        return pinStep;
    }

    @Override
    public void setItsSystem(Subsystem sys){
        super.setItsSystem(sys);

        if(getElementList()!=null)
            for(Element e:getElementList()){
                if(e instanceof Pass)
                    ((Pass)e).getOutside().setSystem(sys);
            }
    }

    private void addPass(Subsystem oldSys,LineMarker lm){
        Pass ep=null;
        if(lm instanceof ElectricMarker){
            //check height
            if(getLeftPinCnt() * getPinStep() + getPinOffset() *2>this.viewPane.getHeight()){
//                this.setHeight(electroPinCnt*pinStep+pinOffset*2); // TODO not fully implemented
            }

            ep=new ElectricPass(this);
        }else
        if(lm instanceof MechMarker){
            ep=new MechPass(this);
        }
        else
        if(lm instanceof MathMarker){
            if(lm.getItsConnectedPin() instanceof MathInPin){
                ep=new MathOutPass(this);
            }else{
                ep=new MathInPass(this);
            }
        }else
        if(lm instanceof ThreePhaseMarker)
            ep=new ThreePhasePass(this);

        ep.setPass(oldSys,lm);
    }

    @Override
    protected void init() {

    }

    @Override
    public void delete() {
        super.delete();
        for(int i=getElementList().size()-1;i>=0;i--){
            getElementList().get(i).delete();
        }
        for(int i=getWireList().size()-1;i>=0;i--){
            getWireList().get(i).delete();
        }
    }

    @Override
    protected String getDescription() {
        return null;
    }

    @Override
    protected void setParams() {
        setName("Subsystem");
    }

    @Override
    public void openDialogStage(){
        getStage().setTitle(getName());
        getStage().show();
        getStage().toFront();
    }

    @Override
    public final StringBuilder save() {
        StringBuilder bw=super.save();

        bw.append("<WindowSize>");
        bw.append("["+ getWindowWidth() +" "+ getWindowHeight() +"]");
        bw.append("</WindowSize>");bw.append("\r\n");

        bw.append("<Elements>");bw.append("\r\n");
        int cnt=0;
        for(Element elem:getElementList()){
            bw.append("<Element"+cnt+">");bw.append("\r\n");
            bw.append(elem.save().toString());
            bw.append("</Element"+cnt+">");bw.append("\r\n");
            cnt++;
        }
        bw.append("</Elements>");bw.append("\r\n");

        bw.append("<WireList>");bw.append("\r\n");
        cnt=0;
        for(Wire w: getWireList()){
            bw.append("<wire"+cnt+">");bw.append("\r\n");
            bw.append(w.save());
            bw.append("</wire"+cnt+">");bw.append("\r\n");
            cnt++;
        }
        bw.append("</WireList>");bw.append("\r\n");

        return bw;
    }

    @Override
    public void configurate(String elemInfo){
        super.configurate(elemInfo);
        double[] v=Parser.parseRow(Parser.getKeyValue(elemInfo,"<WindowSize>"));
        if(v!=null){
            getStage().setHeight(v[1]);
            getStage().setWidth(v[0]);
        }

        String elems=Parser.getBlock(elemInfo,"<Elements>");
        int i=0;
        String data="";
        while(!(data=Parser.getBlock(elems,"<Element"+i+">")).isEmpty()){
            parseElement(data);
            i++;
        }

        String wires=Parser.getBlock(elemInfo,"<WireList>");
        i=0;
        data="";
        while(!(data=Parser.getBlock(wires,"<wire"+i+">")).isEmpty()){
            parseWire(data);
            i++;
        }
    }

    public Pane getViewPane(){
        return viewPane;
    }

    public List<Element> getAllElements(){
        List<Element> out=new ArrayList<>();
        for(Element elem:getElementList()){
            if(elem instanceof Subsystem){
                out.addAll(((Subsystem)elem).getAllElements());
            }else{
                out.add(elem);
            }
        }
        return out;
    }

    public List<Wire> getAllWires(){
        List<Wire> out=new ArrayList<>();
        out.addAll(getWireList());
        for(Element elem:getElementList()){
            if(elem instanceof Subsystem){
                out.addAll(((Subsystem)elem).getAllWires());
            }
        }
        return out;
    }

    public Element parseElement(String elemInfo){
        String className=Parser.getKeyValue(elemInfo,"<ClassName>");

        Element elem=null;
        try {
            Class<?> clas = Class.forName(className);
            Constructor<?> ctor=clas.getConstructor(Subsystem.class);
            elem=(Element)ctor.newInstance(this);
            elem.configurate(elemInfo);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

        return elem;
    }

    private void parseWire(String wireInfo){
        String className=wireInfo.substring(wireInfo.lastIndexOf("<ClassName>")+"<ClassName>".length(),wireInfo.indexOf("</ClassName>"));

        Class<?> clas= null;
        try {
            clas = Class.forName(className);
            Constructor<?> ctor=clas.getConstructor(Subsystem.class);
            Wire w=(Wire)ctor.newInstance(this);
            w.configure(wireInfo);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void initGui(){
        setRightPinCnt(0);
        setLeftPinCnt(0);

        setStage(new Stage());
        getScrollPane().setPannable(true);
        getScrollPane().setOnDragDetected((MouseEvent me)->{
            getScrollPane().setCursor(Cursor.CLOSED_HAND);
        });

        getScrollPane().setOnDragOver(de->{
            de.acceptTransferModes(TransferMode.ANY);
        });
        getScrollPane().setOnDragDropped(de->{

            ElemSerialization content=(ElemSerialization)de.getDragboard().getContent(SchemeElement.CUSTOM_FORMAT);
            Element obj=content.deserialize(this);
            obj.getView().setLayoutX(de.getX());
            obj.getView().setLayoutY(de.getY());

            de.setDropCompleted(true);
            getScrollPane().setCursor(Cursor.DEFAULT);

        });

        getScrollPane().setOnKeyTyped(ke->{
            if(ke.getCharacter().equals("G")&&ke.isShiftDown()){
                if(!selectionModel.getSelectedElements().isEmpty())
                    new Subsystem(this, selectionModel.getSelectedElements());
            }
        });

        getScrollPane().setOnMousePressed(e->{
            if(e.isControlDown()) {
                getScrollPane().setPannable(false);
                selectionModel.init(e.getX(), e.getY());
            }
        });
        drawBoard.setOnMouseDragged(e->{
            if(!getScrollPane().isPannable())
                selectionModel.update(e.getX(),e.getY());
        });
        drawBoard.setOnMouseReleased(e->{
            if(!getScrollPane().isPannable()) {
                getScrollPane().setPannable(true);
                selectionModel.confirm(drawBoard.getChildren());
            }else{
                selectionModel.clear();
            }
        });

        selectionModel=new SelectionModel(drawBoard);

        final Scene scene=new Scene(getScrollPane(),500,400);
        scene.getStylesheets().add("raschetkz/mod.css");
        getStage().setScene(scene);
    }

    public Pane getDrawBoard() {
        return drawBoard;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public double getWindowHeight() {
        return windowHeight;
    }

    public double getWindowWidth() {
        return windowWidth;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stg) {
        stage=stg;
        stage.setWidth(getWindowWidth());
        stage.setHeight(getWindowHeight());
        getStage().heightProperty().addListener((t, o, n)->{
            setWindowHeight(n.doubleValue());
        });

        getStage().widthProperty().addListener((t, o, n)->{
            setWindowWidth(n.doubleValue());
        });
    }

    public void setWindowHeight(double windowHeight) {
        this.windowHeight = windowHeight;
    }

    public void setWindowWidth(double windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getLeftPinCnt() {
        return leftPinCnt;
    }

    public void setLeftPinCnt(int leftPinCnt) {
        this.leftPinCnt = leftPinCnt;
    }

    public int getRightPinCnt() {
        return rightPinCnt;
    }

    public void setRightPinCnt(int rightPinCnt) {
        this.rightPinCnt = rightPinCnt;
    }

    public List<Element> getElementList() {
        return elementList;
    }

    public List<Wire> getWireList() {
        return wireList;
    }

    class SelectionModel{
        double ix,iy;
        final private List<Node> selection=new ArrayList<>();
        final private List<Element> selectedElements=new ArrayList<>();
        Rectangle rect;

        SelectionModel(Pane parent){
            rect=new Rectangle();
            rect.setFill(Color.AQUA);
            rect.setOpacity(0.3);
            rect.setStroke(Color.DARKGRAY);
            rect.setVisible(false);
            parent.getChildren().add(rect);
        }

        void init(double x,double y){
            ix=x;
            iy=y;
            rect.setX(x);
            rect.setY(y);
            rect.setHeight(0);
            rect.setWidth(0);
            rect.toFront();
            clear();
        }

        void update(double x,double y){
            rect.setVisible(true);
            rect.setHeight(y-iy);
            rect.setWidth(x-ix);
        }

        void confirm(ObservableList<Node> childrens){
            rect.setVisible(false);
            Bounds area=rect.getBoundsInParent();
            for(Node node:childrens){
                if(node.getBoundsInParent().intersects(area)){
                    selection.add(node);
                    node.setEffect(new DropShadow(1,Color.GREEN));
                }
            }
            for(Element elem: elementList){
                if(elem.getItsSystem().equals(Subsystem.this)) {
                    Bounds b = elem.getView().getBoundsInParent();
                    if (b.intersects(area))
                        selectedElements.add(elem);
                }
            }
        }

        void clear(){
            selection.forEach(n->{
                n.setEffect(null);
            });
            selection.clear();
            selectedElements.clear();
        }

        List<Element> getSelectedElements(){
            return selectedElements;
        }
    }
}
