package Elements.Environment;

import Connections.*;
import ElementBase.*;
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
import raschetkz.RaschetKz;

import java.util.ArrayList;
import java.util.List;

public class Subsystem extends Element {
    final private Pane drawBoard=new Pane();
    final private Stage stage=new Stage();
    private final ScrollPane scrollPane =new ScrollPane(getDrawBoard());
    private SelectionModel selectionModel;
    private int leftPinCnt,rightPinCnt;
    private static final int pinOffset=10,pinStep=20;

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

    public Subsystem(Subsystem sys, List<Element> childrens){
        this(sys);

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
                                if(childrens.indexOf(lm.getItsConnectedPin().getOwner())==-1){  // elem outside subsystem
                                    // entermediate its connection
                                    addPass(sys,lm);
                                }
                            }
                        }

                        // migrate wire into subsystem drawBoard
                        sys.getDrawBoard().getChildren().remove(pinWire.getView());
                        getDrawBoard().getChildren().addAll(pinWire.getView());
                        pinWire.setItsSystem(this);
                    }
                }
            }

            // migrate elem into subsystem drawBoard
            sys.getDrawBoard().getChildren().remove(elem.getView());
            //getDrawBoard().getChildren().add(elem.getView());

            elem.setItsSystem(this);
        }





        //relocate childrens
        //drawBoard.getChildren().addAll(childrens);


    }

    private void addPass(Subsystem oldSys,LineMarker lm){
        if(lm instanceof WireMarker){
            //check height
            if(leftPinCnt*pinStep+pinOffset*2>this.viewPane.getHeight()){
//                this.setHeight(electroPinCnt*pinStep+pinOffset*2); // TODO not fully implemented
            }

            ElectricPass ep=new ElectricPass(this);

            RaschetKz.elementList.add(ep);

            //reconnect
            Pin oldPin=lm.getItsConnectedPin();
            lm.bindElemContact(ep.inner);
            new ElectricWire(oldSys,oldPin,ep.outside);
        }else
            if(lm instanceof MechMarker){
                MechPass ep=new MechPass(this);

                RaschetKz.elementList.add(ep);

                //reconnect
                Pin oldPin=lm.getItsConnectedPin();
                lm.bindElemContact(ep.inner);
                new MechWire(oldSys,oldPin,ep.outside);
            }
            else
                if(lm instanceof MathMarker){
                    if(lm.getItsConnectedPin() instanceof MathInPin){
                        MathOutPass ep=new MathOutPass(this);

                        RaschetKz.elementList.add(ep);

                        //reconnect
                        Pin oldPin=lm.getItsConnectedPin();
                        lm.bindElemContact(ep.inner);
                        new MathWire(oldSys,oldPin,ep.outside);
                    }else{
                        MathInPass ep=new MathInPass(this);

                        RaschetKz.elementList.add(ep);

                        //reconnect
                        Pin oldPin=lm.getItsConnectedPin();
                        lm.bindElemContact(ep.inner);
                        new MathWire(oldSys,oldPin,ep.outside);
                    }
                }
    }

    @Override
    protected void init() {

    }

    @Override
    public void delete() {

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
        stage.setTitle(getName());
        stage.show();
    }

    private void initGui(){
        rightPinCnt=leftPinCnt=0;

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

            RaschetKz.elementList.add(obj);

            de.setDropCompleted(true);
            getScrollPane().setCursor(Cursor.DEFAULT);
            //de.consume();
        });

        getScrollPane().setOnKeyTyped(ke->{
            if(ke.getCharacter().equals("G")&&ke.isShiftDown()){
                if(!selectionModel.getSelectedElements().isEmpty())
                    RaschetKz.elementList.add(new Subsystem(this,selectionModel.getSelectedElements()));
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
        stage.setScene(scene);
    }

    public Pane getDrawBoard() {
        return drawBoard;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public static class MechPass extends SchemeElement{
        MechPin outside,inner;

        public MechPass(Subsystem sys){
            super(sys);

            outside=new MechPin( this,0,pinOffset+sys.leftPinCnt*pinStep);
            getMechContactList().add(outside);
            outside.setSystem(sys.getItsSystem());
            sys.viewPane.getChildren().add(outside.getView());
            //add view to subsystem Pane
            addMechCont(inner=new MechPin(this,30,15)); // local pin

            sys.leftPinCnt++;
        }

        public MechPass(boolean val){
            super(val);
        }

        @Override
        public String[] getStringFunction() {
            return new String[]{
                    "w.1-w.2=0",
                    "T.1+T.2=0"
            };
        }

        @Override
        public void init() {

        }

        @Override
        public void delete() {

        }

        @Override
        protected String getDescription() {
            return "Passes values from and in sybsystems";
        }

        @Override
        protected void setParams() {
            setName("MechOut");
        }
    }

    public static class MathInPass extends MathElement{
        private MathInPin outside;
        private MathOutPin inner;

        public MathInPass(Subsystem sys){
            super(sys);

            outside=new MathInPin( this,0,pinOffset+sys.leftPinCnt*pinStep);
            getInputs().add(outside);
            outside.setSystem(sys.getItsSystem());
            sys.viewPane.getChildren().add(outside.getView());
            //add view to subsystem Pane
            inner=new MathOutPin(this,30,15); // local pin
            getOutputs().add(inner);
            viewPane.getChildren().add(inner.getView());

            sys.leftPinCnt++;
        }

        public MathInPass(boolean val){
            super(val);
        }

        @Override
        public void init() {

        }

        @Override
        public void delete() {

        }

        @Override
        protected List<Double> getValue(int outIndex) {
            return getInputs().get(0).getValue();
        }

        @Override
        protected String getDescription() {
            return "Passes values from and in sybsystems";
        }

        @Override
        protected void setParams() {
            setName("MathIn");
        }
    }

    public static class MathOutPass extends MathElement{
        private MathOutPin outside;
        private MathInPin inner;

        public MathOutPass(Subsystem sys){
            super(sys);

            outside=new MathOutPin( this,sys.viewPane.getBoundsInLocal().getMaxX(),pinOffset+sys.rightPinCnt*pinStep);
            getOutputs().add(outside);
            outside.setSystem(sys.getItsSystem());
            sys.viewPane.getChildren().add(outside.getView());
            //add view to subsystem Pane
            inner=new MathInPin(this,0,15); // local pin
            getInputs().add(inner);
            viewPane.getChildren().add(inner.getView());

            sys.rightPinCnt++;
        }

        public MathOutPass(boolean val){
            super(val);
        }

        @Override
        public void init() {

        }

        @Override
        public void delete() {

        }

        @Override
        protected List<Double> getValue(int outIndex) {
            return getInputs().get(0).getValue();
        }

        @Override
        protected String getDescription() {
            return "Passes values from and in sybsystems";
        }

        @Override
        protected void setParams() {
            setName("MathOut");
        }
    }

    public static class ElectricPass extends SchemeElement {
        ElectricPin outside,inner;

        public ElectricPass(Subsystem sys){
            super(sys);

            outside=new ElectricPin( sys,0,pinOffset+sys.leftPinCnt*pinStep);
            sys.getElemContactList().add(outside);
            getElemContactList().add(outside);
            //outside.setSystem(sys.getItsSystem());
            sys.viewPane.getChildren().add(outside.getView());
            //add view to subsystem Pane
            addElemCont(inner=new ElectricPin(this,30,15)); // local pin

            sys.leftPinCnt++;
        }

        public ElectricPass(boolean val){
            super(val);
        }

        @Override
        public String[] getStringFunction() {
            return new String[]{
                    "p.1-p.2=0",
                    "i.1+i.2=0"
            };
        }

        @Override
        public void init() {

        }

        @Override
        public void delete() {

        }

        @Override
        protected String getDescription() {
            return "Passes values from and in sybsystems";
        }

        @Override
        protected void setParams() {
            ScalarParameter position=new ScalarParameter("Position 0 - left; 1 - right",0);
//            position.setChangeListener((t,o,n)->{
//                if(Integer.parseInt(n)==1){
//                    outside.getView().setLayoutX(getItsSystem().viewPane.getWidth());
//                }
//            });
            getParameters().add(position);
            setName("ElectroPin");
        }
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
            for(Element elem: RaschetKz.elementList){
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
