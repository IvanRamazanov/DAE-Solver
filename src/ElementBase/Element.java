/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import static raschetkz.RaschetKz.drawBoard;


/**
 *
 * @author Ivan
 */
public abstract class Element {
    protected ContextMenu cm,catCm;
    protected Pane viewPane;
    private VBox layout;
    private String imagePath;
    protected boolean catalogFlag=false;
    private Point2D initialPoint,anchorPoint;
    protected Label name;
    protected List<MathInPin> mathInputs=new ArrayList();
    protected List<MathOutPin> mathOutputs=new ArrayList();
    private static double HEIGHT_FIT=5;

    public final static DataFormat CUSTOM_FORMAT = new DataFormat("ivan.ramazanov");
    protected List<Parameter> parameters=new ArrayList();
    protected List<InitParam> initials=new ArrayList();

    EventHandler<MouseEvent> usualHandler = (MouseEvent me)->{
        if(me.getButton()==MouseButton.PRIMARY){
            switch(me.getClickCount()){
                case 1:
                    break;
                case 2:
                    this.openDialogStage();
                    break;
            }
        }
        if(me.getButton()==MouseButton.SECONDARY){
            cm.hide();
            cm.show(viewPane, me.getScreenX(), me.getScreenY());
        }
        me.consume();
    };

    EventHandler<MouseEvent> catHandler=new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent me){
            if(me.getButton()==MouseButton.SECONDARY){
                catCm.hide();
                catCm.show(viewPane, me.getScreenX(), me.getScreenY());  //getView()
            }
            if(me.getButton()==MouseButton.PRIMARY){
                if(me.getClickCount()==2){
                    catElemCreation();
                }
            }
            me.consume();
        }
    };

    Element(){
        cm=new ContextMenu();
        MenuItem deleteMenu =new MenuItem("Удалить");
        deleteMenu.setOnAction((ActionEvent ae)-> this.delete());
        MenuItem paramMenu=new MenuItem("Параметры");
        paramMenu.setOnAction((ActionEvent ae)-> this.openDialogStage());
        MenuItem rotate=new MenuItem("Поворот");
        rotate.setOnAction(ae-> rotate());
        cm.getItems().addAll(deleteMenu,paramMenu,rotate);
        imagePath="Elements/images/"+this.getClass().getSimpleName()+".png";


        drawBoard.getChildren().add(this.getView());
        setParams();

        this.viewPane.setOnDragDetected(de->{
            if(de.getButton().equals(MouseButton.PRIMARY)&&de.isControlDown()){
                initDND();
            }
        });
    }

    Element(boolean catalog){
        if(catalog){
            catCm=new ContextMenu();
            MenuItem menu =new MenuItem("Добавить");
            menu.setOnAction((ActionEvent ae)->{
                catCm.hide();
                catElemCreation();
            });
            catCm.getItems().add(menu);
            imagePath="Elements/images/"+this.getClass().getSimpleName()+".png";
            catalogFlag=catalog;

            getView(); //for creation!
        }

        setParams();
        this.viewPane.setOnDragDetected(de->{
            if(de.getButton().equals(MouseButton.PRIMARY)){
                initDND();
            }
        });
    }

    private void initDND(){
        Dragboard db=this.viewPane.startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.put(CUSTOM_FORMAT, new ElemSerialization(this));
        Image img=new Image(imagePath,0,0,false,false);
        double h=img.getHeight()/HEIGHT_FIT;
        db.setDragView(new Image(imagePath,0,h,true,false));
        db.setContent(content);
    }

    private void catElemCreation(){
        try{
            Class<?> clas=this.getClass();
            Constructor<?> ctor = clas.getConstructor();
            Element elem=(Element)ctor.newInstance(new Object[] {});
            double x=0;
            elem.getView().setLayoutX(x);
            double y=0;
            elem.getView().setLayoutY(y);
            if(elem instanceof SchemeElement)    raschetkz.RaschetKz.ElementList.add((SchemeElement)elem);
            if(elem instanceof MathElement) raschetkz.RaschetKz.MathElemList.add((MathElement)elem);
        }catch(NoSuchMethodException|InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex){
            Logger.getLogger(Element.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    final public Pane getView(){
        if(viewPane==null){
            viewPane=new Pane();

            layout=new VBox();
            layout.setAlignment(Pos.TOP_CENTER);
            //viewPane.setStyle("-fx-border-color: #ff0000");

            View view=new View(imagePath,0,0);
            viewPane.setPrefSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);
            viewPane.setMaxSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);
            viewPane.focusedProperty().addListener((type,oldVal,newVal)->{
                if(newVal){
                    view.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 1.0, 1.0, 0, 0));
//                    view.setStyle("-fx-border-color: #ff0000");
                }else{
//                    view.setStyle("-fx-border-color: null");
                    view.setEffect(null);
                }
            });
            viewPane.getChildren().add(view);
            name=new Label();
            layout.getChildren().addAll(viewPane,name);
//            viewPane.setMaxSize(view.getLayoutBounds().getWidth(), view.getLayoutBounds().getHeight());
            if(catalogFlag){
                viewPane.setOnMouseClicked(catHandler);
            }
            else{
                viewPane.setOnMouseClicked(usualHandler);
                viewPane.setOnMousePressed((MouseEvent me) -> {
                    if(me.getButton()!=MouseButton.MIDDLE){
                        viewPane.requestFocus();
                        me.consume();
                    }
                    if(me.getButton()==MouseButton.PRIMARY){
                        anchorPoint=new Point2D(me.getSceneX(),me.getSceneY());
                        initialPoint=new Point2D(layout.getLayoutX(),layout.getLayoutY());
                        layout.toFront();
                        me.consume();
                    }
                });
                viewPane.setOnMouseDragged((MouseEvent me)->{
                    if(me.getButton()==MouseButton.PRIMARY&&!me.isControlDown()){
                        double x=initialPoint.getX()+me.getSceneX()-anchorPoint.getX();
                        double y=initialPoint.getY()+me.getSceneY()-anchorPoint.getY();
                        if(x<0)
                            x=0;
                        if(y<0)
                            y=0;
//                        viewPane.setLayoutX(x);
//                        viewPane.setLayoutY(y);
                        layout.setLayoutX(x);
                        layout.setLayoutY(y);
                        me.consume();
                    }
                });
            }
            viewPane.setOnKeyReleased(ke->{
                if(ke.getCode()==KeyCode.DELETE){
                    this.delete();
                }
            });
            viewPane.setOnKeyPressed(ke->{
                if(ke.getCode()==KeyCode.LEFT){
                    layout.setLayoutX(layout.getLayoutX()-1);
                }else
                if(ke.getCode()==KeyCode.RIGHT){
                    layout.setLayoutX(layout.getLayoutX()+1);
                }else
                if(ke.getCode()==KeyCode.UP){
                    layout.setLayoutY(layout.getLayoutY()-1);
                }else
                if(ke.getCode()==KeyCode.DOWN){
                    layout.setLayoutY(layout.getLayoutY()+1);
                }else
                if(ke.getCode()==KeyCode.R&&ke.isControlDown()){
                    rotate();
                }
            });
        }

//        lbl.setTextAlignment(TextAlignment.CENTER);
//        VBox out=new VBox(viewPane,lbl);
//        out.setAlignment(Pos.CENTER);
//        out.setLayoutX(100);
//        out.setLayoutY(100);
        return layout;
//        return(viewPane);
    }

    abstract protected void init();

    private void rotate(){
        double angle=this.viewPane.getRotate();
        double add=90;
        this.viewPane.setRotate((angle+add)%360);
    }

    public void setRotation(double value){
        viewPane.setRotate(value);
    }

    public double getRotation(){
        return viewPane.getRotate();
    }

    abstract protected void delete();

    protected void openDialogStage() {
        double maxnamelenght=0;
        Stage subWind=new Stage();
        subWind.setTitle("Параметры: "+this.getName());
        VBox root=new VBox();
        Scene scene=new Scene(root,300,200,Color.DARKCYAN);
        subWind.setScene(scene);
//        subWind.

        Text header=new Text(getName()+"\n\n");
        header.setFont(Font.font("Times New Roman", FontWeight.BOLD, 12));
        root.getChildren().add(new TextFlow(header, new Text(getDescription())));

        VBox top=new VBox();
        for(Parameter p:this.getParameters()){
            top.getChildren().add(p.getLayout());
        }
        Tab params=new Tab("Параметры элемента");
        ScrollPane asd=new ScrollPane(top);
//        asd.setPannable(true);
        //asd.setFitToWidth(true);
        asd.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        //asd.setPrefViewportHeight(150);
        //asd.setHvalue(0.5);
        params.setContent(asd);

        TabPane pane;

        if(this.getInitials().isEmpty()){
            pane=new TabPane(params);
            pane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            root.getChildren().add(pane);
        }else{
            GridPane ttop=new GridPane();
            ttop.addRow(0, new Label("Переменная"),new Label("Приоритет"),new Label("Значение"));
            for(int k=0;k<this.getInitials().size();k++){
                InitParam p=this.getInitials().get(k);
                List<Node> pn=new ArrayList(p.getLayouts());
                int siz=pn.size();
                for(int i=siz-1;i>=0;i--){
                    ttop.add(pn.get(i),i,k+1);
                }
            }
            Tab inits=new Tab("Начальные условия");
            ttop.getColumnConstraints().add(new ColumnConstraints(Control.USE_COMPUTED_SIZE));
            ttop.getColumnConstraints().add(new ColumnConstraints(Control.USE_COMPUTED_SIZE));
            ttop.getColumnConstraints().add(new ColumnConstraints(Control.USE_COMPUTED_SIZE));
            ttop.setHgap(2);
            inits.setContent(ttop);
//            ttop.getChildren().f
            pane=new TabPane(params,inits);
            pane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            root.getChildren().add(pane);
        }
        //---buttns
        HBox bot=new HBox();
        Button btn=new Button("Отмена");
        btn.setOnAction((ActionEvent ae)->{
            subWind.close();
        });
        bot.getChildren().add(btn);
        btn=new Button("Ок");
        btn.setOnAction((ActionEvent ae)->{
            this.getParameters().forEach(data->{
                data.update();
            });
            this.getInitials().forEach(data->{
                data.update();
            });
            subWind.close();
        });

        bot.setAlignment(Pos.CENTER_RIGHT);
        bot.getChildren().add(btn);
        root.getChildren().add(bot);
        //
        //subWind.sizeToScene();
        subWind.show();
        //subWind.setMaxHeight(root.getLayoutBounds().getHeight()+bot.getHeight()+pane.getHeight());
        scene.setOnKeyReleased(ke->{
            if(ke.getCode()==KeyCode.ENTER){
                this.getParameters().forEach(data->{
                    data.update();
                });
                this.getInitials().forEach(data->{
                    data.update();
                });
                subWind.close();
            }
        });
        if(this.getParameters().size()>0){
            this.getParameters().get(0).requestFocus();
        }
    }

    abstract protected String getDescription();

    /**
     * @return the name
     */
    public String getName() {
        return name.getText();
    }

    abstract protected void setParams();

    /**
     * @return the parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * @return the initials
     */
    public List<InitParam> getInitials() {
        return initials;
    }

    public List<Double> getInitialVals() {
        List<Double> out=new ArrayList();
        initials.forEach(v->{
            out.add(v.getDoubleValue());
        });
        return out;
    }

    /**
     * @param initials the initials to set
     */
    public void setInitials(List<InitParam> initials) {
        this.initials = initials;
    }


    public class Parameter{
        double initVal=0;
        String name="";
        VBox layout;
        TextField text;

        protected Parameter(){
        }

        public Parameter(String name,double initVal){
            this.name=name;
            this.initVal=initVal;
            this.layout=new VBox();
            this.text=new TextField(Double.toString(initVal));
            layout.getChildren().add(new Label(name));
            layout.getChildren().add(text);
        }

        void update(){
            DoubleStringConverter conv=new DoubleStringConverter();
            this.initVal=conv.fromString(this.text.getText());
        }

        /**
         * Value at t=0.
         * @return
         */
        public double getDoubleValue(){
            return(this.initVal);
        }

        protected void requestFocus(){
            text.requestFocus();
        }

        @Override
        public String toString(){
            return text.getText();
        }

        public void setValue(double val){
            this.initVal=val;
            this.text.setText(Double.toString(val));
        }

        public Pane getLayout(){
            return layout;
        }

//        public List<Node> getLayout(){
//            return layout;
//        }
    }

    public class InitParam extends Parameter{
        boolean priority;
        ComboBox box;
        List<Node> layout;

        /**
         *
         * @param name
         * @param initVal
         */
        public InitParam(String name, double initVal){
            this.name=name;
            this.initVal=initVal;
            this.layout=new ArrayList();
            this.text=new TextField(Double.toString(initVal));
            layout.add(new Label(name));
            box=new ComboBox();
            box.getItems().addAll("High","Low");
            box.setValue("Low");
            //box.setMinWidth(box.getc);
            layout.add(box);
            layout.add(text);
        }

        @Override
        void update(){
            DoubleStringConverter conv=new DoubleStringConverter();
            this.initVal=conv.fromString(this.text.getText());
            this.priority = box.getValue().equals("High");
        }

        public boolean getPriority(){
            return(this.priority);
        }

        public void setPriority(boolean val){
            this.priority=val;
            if(val)
                box.setValue("High");
            else
                box.setValue("Low");
        }

        //        @Override
        public List<Node> getLayouts(){
            return this.layout;
        }
    }

    class View extends ImageView{

        View(String root,double x,double y){


            this.setImage(new Image(root));
            this.setSmooth(true);
            this.setPreserveRatio(true);
            double h=this.getImage().getHeight();
            this.setFitHeight(h/HEIGHT_FIT);
            this.setTranslateX(x);
            this.setTranslateY(y);
        }
    }

    /**
     * @param name the name to set
     */
    protected final void setName(String name) {
        this.name.setText(name);
    }
}
