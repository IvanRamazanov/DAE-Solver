/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raschetkz;

import Connections.MathWire;
import MathPack.Rechatel;
import Connections.ElectricWire;
import ElementBase.ElemSerialization;
import ElementBase.Element;
import ElementBase.ListOfElements;
import ElementBase.MathElement;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.util.List;
import javafx.scene.input.MouseEvent;
import ElementBase.ShemeElement;
import MathPack.WorkSpace;
import java.io.File;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import javafx.util.converter.DoubleStringConverter;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.TransferMode;

/**
 *
 * @author Ivan
 */
public class RaschetKz extends Application{
    private static String[] arguments;
    Button stopBtn;

    public SimpleStringProperty solverType;
    public static List<ShemeElement> ElementList;
    public static List<MathElement> MathElemList;
    public static List<ElectricWire> BranchList;

    public static List<MathWire> mathContsList;//!!!!!!!!!!!!

    public static Pane drawBoard;
    public static ProgressBar progBar=new ProgressBar(0);
    public SimpleDoubleProperty dt,t_end;//????????
    ModelState state;
    boolean isSaveNeeded;
    Stage parentStage;
    private static Label Status;
    private Label currentFile=new Label("untitled");

    @Override
    public void start(Stage primaryStage) {
//        for(int i=0;i<arguments.length;i++)
//            layoutString(arguments[i]);

        state=new ModelState();
        dt=state.getDt();
        dt.set(1e-4);
        t_end=state.getTend();
        t_end.set(1);
        solverType=state.getSolver();
        parentStage=primaryStage;
        drawBoard=state.getDrawBoard();
        BranchList=state.GetWires();
        ElementList=state.GetElems();
        MathElemList=state.GetMathElems();
        mathContsList=state.getMathConnList();
        if(arguments.length==2)
            if(arguments[0].equals("-o")){
                File f=new File(arguments[1]);
                state.Load(arguments[1]);
                currentFile.setText(f.getName());
            }
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("raschetkz/mod.css");
        initGui(root);
        primaryStage.setTitle("OmniSystem Simulator");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            Platform.exit();
        });
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        arguments=args;

//        arguments=new String[]{"-o","C:\\Users\\Ivan\\Desktop\\test.rim"};

        launch(args);
    }
    void initGui(BorderPane rootBp)
    {
        Status=new Label();
        rootBp.setTop(createMenu());
        GridPane bottomBox=new GridPane();
        bottomBox.gridLinesVisibleProperty().set(true);
//        bottomBox.setHgap(20);
        bottomBox.getColumnConstraints().add(new ColumnConstraints(100));
        bottomBox.getColumnConstraints().add(new ColumnConstraints(100));
        bottomBox.getColumnConstraints().add(new ColumnConstraints());
        bottomBox.getColumnConstraints().add(new ColumnConstraints(120));
        progBar.setPrefHeight(Region.USE_COMPUTED_SIZE);
        progBar.progressProperty().addListener((type,old,New)->{
            if(Status.textProperty().isBound())
                Status.textProperty().unbind();
            if(New.doubleValue()==1.0){
                Status.setText("Done!");
            }else{
                Status.setText("T="+String.format("%.3f",New.doubleValue()*state.getTend().doubleValue()));
            }
        });
        bottomBox.add(progBar, 1, 0);
        bottomBox.add(Status, 0, 0);
        bottomBox.add(new Label("File: "), 2, 0);
        bottomBox.add(currentFile, 3, 0);
        Button startBtn=new Button("Start");
        startBtn.setOnAction(ae->{
            startBtn.setDisable(true);
            //CREATE BACKUP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            Rechatel eval=new Rechatel(state, true);
            //RESTORE BACKUP!!!!!!!!!!!!!!!!!!
            Thread tr=new Thread(eval);
            tr.setDaemon(true);
            progBar.progressProperty().bind(eval.progressProperty());
            Status.textProperty().bind(eval.messageProperty());
            tr.start();
            stopBtn.setOnAction(e->{
                //Status.
                eval.cancel();

            });

            stopBtn.setDisable(false);
            eval.setOnSucceeded(e->{
                stopBtn.setDisable(true);
                startBtn.setDisable(false);
            });
            eval.setOnCancelled(e->{
                Status.textProperty().unbind();
                Status.setText("Stopped by user");
                stopBtn.setDisable(true);
                startBtn.setDisable(false);
            });
            eval.setOnFailed(e->{
                Status.textProperty().unbind();
                Status.setText("Fatal error");
                stopBtn.setDisable(true);
                startBtn.setDisable(false);
            });
//            MathPackODE.Compiler compiler=new MathPackODE.Compiler();
//            DAE sys=compiler.evalNumState(state);
        });
        bottomBox.add(startBtn,4,0);
        stopBtn=new Button("Stop");
        stopBtn.setDisable(true);
        bottomBox.add(stopBtn,5,0);
        rootBp.setBottom(bottomBox);
        ScrollPane scrllPane = new ScrollPane(drawBoard);
        scrllPane.setPannable(true);
        scrllPane.setOnDragDetected((MouseEvent me)->{
            scrllPane.setCursor(Cursor.CLOSED_HAND);
        });

        scrllPane.setOnDragOver(de->{
            //de.getDragboard().getContentTypes().contains(de)
            de.acceptTransferModes(TransferMode.ANY);
        });
        scrllPane.setOnDragDropped(de->{

            ElemSerialization content=(ElemSerialization)de.getDragboard().getContent(ShemeElement.CUSTOM_FORMAT);
            Element obj=content.deserialize();
            obj.getView().setLayoutX(de.getX());
            obj.getView().setLayoutY(de.getY());
            if(obj instanceof ShemeElement)
                ElementList.add((ShemeElement)obj);
            else if(obj instanceof MathElement)
                MathElemList.add((MathElement)obj);
            else
                throw new Error("error: incompatible types!");

            de.setDropCompleted(true);
            de.consume();
        });

        rootBp.setCenter(scrllPane);
    }

    void createElementCatalog(){
        Stage elementStage=new Stage();
        elementStage.setTitle("Element catalogue");
        elementStage.initOwner(parentStage);
        HBox root=new HBox(10);
        root.setStyle("-fx-border-style: solid");
        Scene scene=new Scene(root,400, 500,Color.ANTIQUEWHITE);
        VBox cats=new VBox(0);
        scene.getStylesheets().add("raschetkz/mod.css");
        TilePane elems=new TilePane(Orientation.HORIZONTAL, 5, 5);
        root.getChildren().addAll(cats,elems);
        ListOfElements list=new ListOfElements();
        cats.getChildren().addAll(list.getCategories());
        list.setElemPane(elems);
        elementStage.setScene(scene);
        elementStage.show();
    }

    MenuBar createMenu(){
        FileChooser fileChoose = new FileChooser();
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem newFile= new MenuItem("New");
        newFile.setOnAction((ActionEvent ae)->{
            if(isSaveNeeded){

            }else{
                //MUST BE List.forEach(delete)!!!!!!!!!!!!!!!!!1
                for(int i=BranchList.size()-1;i>=0;i--)
                    BranchList.get(i).delete();
                for(int i=ElementList.size()-1;i>=0;i--)
                    ElementList.get(i).delete();
                for(int i=MathElemList.size()-1;i>=0;i--)
                    MathElemList.get(i).delete();
                for(int i=mathContsList.size()-1;i>=0;i--)
                    mathContsList.get(i).delete();
                //drawBoard.getChildren().clear();
                state.fileName=null;
                currentFile.setText("Untitled.rim");
            }
        });
        MenuItem menuOpenFile = new MenuItem("Open");
        menuOpenFile.setAccelerator(KeyCombination.keyCombination(KeyCombination.CONTROL_DOWN+"+o"));
        menuOpenFile.setOnAction((ActionEvent ae) -> {
            FileChooser filechoose=new FileChooser();
            filechoose.getExtensionFilters().add(new FileChooser.ExtensionFilter("RIM", "*.rim"));
            filechoose.setTitle("Choose a file");
            File file=filechoose.showOpenDialog(parentStage);
            if(file!=null){
                state.Load(file.toString());
                currentFile.setText(file.getName());
            }
        });
        MenuItem menuSaveFile = new MenuItem("Save");
        menuSaveFile.setAccelerator(KeyCombination.keyCombination(KeyCombination.CONTROL_DOWN+"+s"));
        menuSaveFile.setOnAction((ActionEvent ae)->{
            if(state.getName()==null){
                FileChooser filechoose=new FileChooser();
                filechoose.getExtensionFilters().add(new FileChooser.ExtensionFilter("RIM", "*.rim"));
                filechoose.setTitle("Save as...");
                File file=filechoose.showSaveDialog(parentStage);
                if(file!=null){
                    state.Save(file.toString());
                    currentFile.setText(file.getName());
                }
            }else{
                state.Save();
            }
        });
        MenuItem menuSaveAsFile = new MenuItem("Save as...");
        menuSaveAsFile.setAccelerator(KeyCombination.keyCombination(KeyCombination.CONTROL_DOWN+"+"+KeyCombination.ALT_DOWN+"+s"));
        menuSaveAsFile.setOnAction((ActionEvent ae)->{
            FileChooser filechoose=new FileChooser();
            filechoose.getExtensionFilters().add(new FileChooser.ExtensionFilter("RIM", "*.rim"));
            filechoose.setTitle("Save as...");
            File file=filechoose.showSaveDialog(parentStage);
            if(file!=null){
                state.Save(file.toString());
                currentFile.setText(file.getName());
            }
        });
        MenuItem menuExit = new MenuItem("Exit");
//        menuExit.setAccelerator(KeyCombination.keyCombination("Esc"));
        menuExit.setOnAction((ActionEvent ae) -> {
            System.exit(0);
        });
        fileMenu.getItems().addAll(newFile,menuOpenFile,menuSaveFile,menuSaveAsFile,menuExit);
        Menu evalMenu = new Menu("Simulation");
        //MenuItem run=new MenuItem("Start");

        MenuItem config=new MenuItem("Solver configuration");
        config.setAccelerator(KeyCombination.keyCombination(KeyCombination.CONTROL_DOWN+"+g"));
        config.setOnAction((ActionEvent ae)->{
            showConfigurator();
        });
        evalMenu.getItems().addAll(config);
        menuBar.getMenus().addAll(fileMenu,evalMenu);
        Menu catMenu = new Menu("Element catalogue");
        MenuItem item=new MenuItem("Open catalogue");
        item.setAccelerator(KeyCombination.keyCombination(KeyCombination.CONTROL_DOWN+"+l"));
        item.setOnAction((ActionEvent ae)->{
            createElementCatalog();
        });
        catMenu.getItems().add(item);
        menuBar.getMenus().add(catMenu);

        return(menuBar);
    }

    private void showConfigurator(){
        Stage subWind=new Stage();
        subWind.setTitle("Solver confuguration");
        BorderPane root=new BorderPane();
        Scene scene=new Scene(root, 400, 300);
        scene.getStylesheets().add("raschetkz/mod.css");
        TextField delta=new TextField(Double.toString(dt.doubleValue()));
        TextField endTime=new TextField(Double.toString(t_end.doubleValue()));
        ComboBox solver=new ComboBox();
        solver.getItems().add("Euler");
        solver.getItems().add("Adams4");
        solver.setValue(solverType.get());
        ComboBox jacobEstType=new ComboBox();
        jacobEstType.getItems().add("Full symbolic");
        jacobEstType.getItems().add("Symbolic inverse Jacobian");
        jacobEstType.getItems().add("Symbolic only Jacobian");
        switch(state.getJacobianEstimationType()){
            case 0:
                jacobEstType.setValue("Full symbolic");
                break;
            case 1:
                jacobEstType.setValue("Symbolic inverse Jacobian");
                break;
            case 2:
                jacobEstType.setValue("Symbolic only Jacobian");
                break;
        }
        Label deltaL=new Label("Fixed step size:");
        Label endL=new Label("Simulation time:");
        Label solvL=new Label("Solver type:");
        Label jacoL=new Label("Jacobian type:");
        GridPane top=new GridPane();
        top.setVgap(5);
        top.setHgap(5);
        top.add(deltaL,0,0);
        top.add(delta,1,0);
        top.add(endL,0,1);
        top.add(endTime,1,1);
        top.add(solvL,0,2);
        top.add(solver, 1, 2);
        top.add(jacoL,0,3);
        top.add(jacobEstType,1,3);
        root.setTop(top);
        HBox bot=new HBox();
        Button okBtn=new Button("Ok");
        okBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent ae){
                try {
                    DoubleStringConverter conv=new DoubleStringConverter();
                    dt.set(conv.fromString(delta.getText()));
                    t_end.set(conv.fromString(endTime.getText()));
                    solverType.set((String)solver.getValue());
                    state.setJacobianEstimationType(jacobEstType.getItems().indexOf(jacobEstType.getValue()));
                } catch (Exception e) {
                    layoutString(e.getCause().toString());
                }
                subWind.close();
            };
        });
        Button applyBtn=new Button("Apply");
        applyBtn.setOnAction((ActionEvent ae)->{
            try {
                DoubleStringConverter conv=new DoubleStringConverter();
                dt.set(conv.fromString(delta.getText()));
                t_end.set(conv.fromString(endTime.getText()));
                solverType.set((String)solver.getValue());
                state.setJacobianEstimationType(jacobEstType.getItems().indexOf(jacobEstType.getValue()));
            } catch (Exception e) {
                layoutString(e.getCause().toString());
            }
        });
        Button cancelBtn=new Button("Cancel");
        cancelBtn.setOnAction((ActionEvent ae)->{
            subWind.close();
        });
        bot.setAlignment(Pos.CENTER_RIGHT);
        bot.getChildren().addAll(okBtn,applyBtn,cancelBtn);
        root.setBottom(bot);
        subWind.setScene(scene);
        subWind.show();

        root.setOnKeyReleased(ke->{
            if(ke.getCode()==KeyCode.ENTER){
                try {
                    DoubleStringConverter conv=new DoubleStringConverter();
                    dt.set(conv.fromString(delta.getText()));
                    t_end.set(conv.fromString(endTime.getText()));
                    solverType.set((String)solver.getValue());
                    subWind.close();
                } catch (Exception e) {
                    layoutString(e.getCause().toString());
                }
            }
        });
    }

    public static void layoutString(String input){
        Stage stag=new Stage();
        Pane root=new Pane();
        Label lbl=new Label();
        lbl.setText(input);
        root.getChildren().add(lbl);
        Scene sc=new Scene(root,300,50);
        stag.setScene(sc);
//        stag.setResizable(false);
        stag.show();
    }
}

