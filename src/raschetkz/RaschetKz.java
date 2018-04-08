/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raschetkz;

import MathPackODE.Rechatel;
import ElementBase.ListOfElements;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import javafx.util.converter.DoubleStringConverter;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Ivan
 */
public class RaschetKz extends Application{
    private static String[] arguments;
    private Button stopBtn;
    public SimpleStringProperty solverType;
    public static ProgressBar progBar=new ProgressBar(0);
    public SimpleDoubleProperty dt,t_end,absTol,relTol;
    private ModelState state;
    private boolean isSaveNeeded;
    private Stage parentStage;
    private static Label Status;
    private Label currentFile=new Label("untitled");
    private Logger myLog=new Logger();

    @Override
    public void start(Stage primaryStage) {
        System.setErr(new PrintStream(myLog));

        state=new ModelState();
        state.getMainSystem().setStage(primaryStage);

        dt=state.getDt();
        dt.set(1e-4);
        t_end=state.getTend();
        t_end.set(1);
        absTol=state.getAbsTol();
        absTol.set(1e-5);
        relTol=state.getRelTol();
        relTol.set(1e-3);
        solverType=state.getSolver();
        parentStage=primaryStage;
        switch(arguments.length) {
            case 2:
                if (arguments[0].equals("-o")) {
                    File f = new File(arguments[1]);
                    state.load(arguments[1]);
                    currentFile.setText(f.getName());
                }
                break;
            case 1:
                String arg=arguments[0];
                Path uri= Paths.get(arg);
                if(Files.exists(uri)){
                    state.load(arg);
                    currentFile.setText(uri.getFileName().toString());
                }
        }
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("raschetkz/mod.css");
        initGui(root);
        primaryStage.setTitle("OmniSystem Simulator");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            Stage st=new Stage();
            BorderPane bp=new BorderPane();
            Scene sc=new Scene(bp);
            st.setScene(sc);
            ButtonBar bb=new ButtonBar(ButtonBar.BUTTON_ORDER_WINDOWS);
            Button yes=new Button("Yes");
            yes.setOnAction(ae->{
                Platform.exit();
            });
            ButtonBar.setButtonData(yes, ButtonBar.ButtonData.YES);
            Button no=new Button("No");
            no.setOnAction(ae->{
                st.close();
            });
            ButtonBar.setButtonData(no, ButtonBar.ButtonData.NO);
            bb.getButtons().addAll(no,yes);
            bb.setButtonOrder("+YN");
            bp.setBottom(bb);

            bp.setTop(new Label("Are you really want to exit?"));

            st.sizeToScene();
            st.show();

            we.consume();
        });
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        arguments=args;

//        arguments=new String[]{"-o","C:\\Users\\Ivan\\Desktop\\test.rim"};
//        arguments=new String[]{"-o"};

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
                Toolkit.getDefaultToolkit().beep();
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
            myLog.initLogs();


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
                myLog.errorLayout();

                Toolkit.getDefaultToolkit().beep();
            });
        });
        bottomBox.add(startBtn,4,0);
        stopBtn=new Button("Stop");
        stopBtn.setDisable(true);
        bottomBox.add(stopBtn,5,0);
        Button errBtn=new Button();

        myLog.setButton(errBtn);
        errBtn.setMaxHeight(24);
        bottomBox.add(errBtn,6,0);
        rootBp.setBottom(bottomBox);

        rootBp.setCenter(state.getMainSystem().getScrollPane());
    }

    Stage createElementCatalog(){
        Stage elementStage=new Stage();
        elementStage.setTitle("Element catalogue");
        //elementStage.initOwner(parentStage);
        SplitPane root=new SplitPane();
        root.setStyle("-fx-border-style: solid");
        Scene scene=new Scene(root,600, 400,Color.ANTIQUEWHITE);
        //VBox cats=new VBox(0);
        scene.getStylesheets().add("raschetkz/mod.css");

        TilePane elems=new TilePane(Orientation.HORIZONTAL, 5, 5);
        elems.getChildren().addListener((ListChangeListener<Node>) c -> {
            if(c.getList().isEmpty())
                elems.setTranslateY(0);
        });
        elems.setOnScroll(se->{
            double val=elems.getTranslateY(),dy=se.getDeltaY();
            if(dy>0){
                val+=dy;
                if(val<=0)
                    elems.setTranslateY(val);
                else
                    elems.setTranslateY(0);
            }else{
                double max=elems.getLayoutBounds().getMaxY(),  // content box
                        height=root.getHeight(),  //visible
                        maxTranslate=height-max;
                val+=dy;
                if (val > maxTranslate)
                    elems.setTranslateY(val);
                else
                    elems.setTranslateY(maxTranslate);
            }
        });

        ScrollBar sc=new ScrollBar();
        sc.setOrientation(Orientation.VERTICAL);
        sc.valueProperty().addListener((t,o,n)->{
            elems.setTranslateY(-n.doubleValue());
        });

        final ListOfElements list=new ListOfElements();
        root.getItems().addAll(list.getCategories(),elems,sc);
        root.getDividers().get(0).setPosition(0.1);
        list.setElemPane(elems);
        elementStage.setScene(scene);

        return elementStage;
    }

    MenuBar createMenu(){
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem newFile= new MenuItem("New");
        newFile.setOnAction((ActionEvent ae)->{
            if(isSaveNeeded){

            }else{
                state.clearState();
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
                state.load(file.toString());
                currentFile.setText(file.getName());
            }
        });
        MenuItem menuSaveFile = new MenuItem("Save");
        menuSaveFile.setAccelerator(KeyCombination.keyCombination(KeyCombination.CONTROL_DOWN+"+s"));
        menuSaveFile.setOnAction((ActionEvent ae)->{
            if(state.getFilePath()==null){
                FileChooser filechoose=new FileChooser();
                filechoose.getExtensionFilters().add(new FileChooser.ExtensionFilter("RIM", "*.rim"));
                filechoose.setTitle("Save as...");
                File file=filechoose.showSaveDialog(parentStage);
                if(file!=null){
                    state.Save(file.toString());
                    currentFile.setText(file.getName());
                }
            }else{
                state.save();
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

        MenuItem config=new MenuItem("Solver configuration");
        //config.setAccelerator(KeyCombination.keyCombination(KeyCombination.CONTROL_DOWN+"+g"));
        config.setOnAction((ActionEvent ae)->{
            showConfigurator();
        });
        evalMenu.getItems().addAll(config);
        menuBar.getMenus().addAll(fileMenu,evalMenu);
        Menu catMenu = new Menu("Element catalogue");
        MenuItem item=new MenuItem("Open catalogue");
        item.setAccelerator(KeyCombination.keyCombination(KeyCombination.CONTROL_DOWN+"+l"));
        Stage st=createElementCatalog();
        item.setOnAction((ActionEvent ae)->{
            st.show();
            st.toFront();
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
        TextField abst=new TextField(Double.toString(absTol.doubleValue()));
        TextField relt=new TextField(Double.toString(relTol.doubleValue()));
        ComboBox solver=new ComboBox();
        solver.getItems().add("Euler");
        solver.getItems().add("Adams4");
        solver.getItems().add("RungeKuttaFehlberg");
        solver.getItems().add("RK4");
        solver.getItems().add("Roshenbrok");
        solver.getItems().add("BDF1");
        solver.getItems().add("BDF2");
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
        GridPane top=new GridPane();
        top.setVgap(5);
        top.setHgap(5);
        top.add(new Label("Fixed step size:"),0,0);
        top.add(delta,1,0);
        top.add(new Label("Simulation time:"),0,1);
        top.add(endTime,1,1);
        top.add(new Label("Absolute tolerance:"),0,2);
        top.add(abst,1,2);
        top.add(new Label("Relative tolerance:"),0,3);
        top.add(relt,1,3);
        top.add(new Label("Solver type:"),0,4);
        top.add(solver, 1, 4);
        top.add(new Label("Jacobian type:"),0,5);
        top.add(jacobEstType,1,5);
        top.add(new Label("Try to reduce system size"),0,6);
        CheckBox cb=new CheckBox();
        ModelState.getSimplyfingFlag().bind(cb.selectedProperty());
        top.add(cb,1,6);
        root.setTop(top);
        HBox bot=new HBox();
        Button okBtn=new Button("Ok");
        okBtn.setOnAction(ae -> {
            try {
                DoubleStringConverter conv=new DoubleStringConverter();
                dt.set(conv.fromString(delta.getText()));
                t_end.set(conv.fromString(endTime.getText()));
                solverType.set((String)solver.getValue());
                state.setJacobianEstimationType(jacobEstType.getItems().indexOf(jacobEstType.getValue()));
                absTol.set(conv.fromString(abst.getText()));
                relTol.set(conv.fromString(relt.getText()));
            } catch (Exception e) {
                layoutString(e.getMessage());
            }
            subWind.close();
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
        stag.show();
    }


}

