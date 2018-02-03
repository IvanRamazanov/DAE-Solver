/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.util.converter.DoubleStringConverter;
import static raschetkz.RaschetKz.drawBoard;


/**
 *
 * @author Ivan
 */
public abstract class Element {
    protected ContextMenu cm,catCm;
    protected Pane viewPane;
    String imagePath;
    protected boolean catalogFlag=false;
    Point2D initialPoint,anchorPoint;
    protected String name;
    
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
        deleteMenu.setOnAction((ActionEvent ae)->{
            this.delete();
        });            
        MenuItem paramMenu=new MenuItem("Параметры");
        paramMenu.setOnAction((ActionEvent ae)->{
            this.openDialogStage();
        });
        MenuItem rotate=new MenuItem("Поворот");
        rotate.setOnAction(ae->{
            rotate();
        });
        cm.getItems().addAll(deleteMenu,paramMenu,rotate);
        imagePath="Elements/images/"+this.getClass().getSimpleName()+".png";
        drawBoard.getChildren().add(this.getView());
    }
    
    Element(boolean catalog){
        if(catalog==true){
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
    }
    
    void catElemCreation(){
        try{
            Class<?> clas=this.getClass();
            Constructor<?> ctor = clas.getConstructor();
            Element elem=(Element)ctor.newInstance(new Object[] {});
            double x=0;
            elem.getView().setLayoutX(x);
            double y=0;
            elem.getView().setLayoutY(y);
            if(elem instanceof ShemeElement)    raschetkz.RaschetKz.ElementList.add((ShemeElement)elem);
            if(elem instanceof MathElement) raschetkz.RaschetKz.MathElemList.add((MathElement)elem);
        }catch(NoSuchMethodException|InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex){
              Logger.getLogger(Element.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
    
    final public Pane getView(){
        if(viewPane==null){
            viewPane=new Pane();
            //viewPane.setStyle("-fx-border-color: #ff0000");
            
            View view=new View(imagePath,0,0);
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
//            viewPane.setMaxSize(view.getLayoutBounds().getWidth(), view.getLayoutBounds().getHeight());
            if(catalogFlag){
                viewPane.setOnMouseClicked(catHandler);
            }
            else{
                viewPane.setOnMouseClicked(usualHandler);
                viewPane.setOnMousePressed((MouseEvent me) -> {
                    if(me.getButton()!=MouseButton.MIDDLE)
                        viewPane.requestFocus();
                    if(me.getButton()==MouseButton.PRIMARY){
                        anchorPoint=new Point2D(me.getSceneX(),me.getSceneY());
                        initialPoint=new Point2D(viewPane.getLayoutX(),viewPane.getLayoutY());
                        me.consume();
                    }
                });
                viewPane.setOnMouseDragged((MouseEvent me)->{
                    if(me.getButton()==MouseButton.PRIMARY){
                        double x=initialPoint.getX()+me.getSceneX()-anchorPoint.getX();
                        double y=initialPoint.getY()+me.getSceneY()-anchorPoint.getY();
                        if(x<0)
                            x=0;
                        if(y<0)
                            y=0;
                        viewPane.setLayoutX(x);
                        viewPane.setLayoutY(y);
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
                    viewPane.setLayoutX(viewPane.getLayoutX()-1);
                }else
                if(ke.getCode()==KeyCode.RIGHT){
                    viewPane.setLayoutX(viewPane.getLayoutX()+1);
                }else
                if(ke.getCode()==KeyCode.UP){
                    viewPane.setLayoutY(viewPane.getLayoutY()-1);
                }else
                if(ke.getCode()==KeyCode.DOWN){
                    viewPane.setLayoutY(viewPane.getLayoutY()+1);
                }else
                if(ke.getCode()==KeyCode.R&&ke.isControlDown()){
                    rotate();
                }
            });
        }
//        Label lbl=new Label(name);
//        lbl.setTextAlignment(TextAlignment.CENTER);
//        VBox out=new VBox(viewPane,lbl);
//        out.setAlignment(Pos.CENTER);
//        out.setLayoutX(100);
//        out.setLayoutY(100);
        return(viewPane);
    }
    
    abstract protected void init();
    
    private void rotate(){
        double angle=this.viewPane.getRotate();
        double add=90;
        this.viewPane.setRotate((angle+add)%360);
    }
    
    abstract protected void delete();
    
    abstract protected void openDialogStage();

    /**
     * @return the name
     */
    public String getName() {
        return name;
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
        
        public String getStringValue(){
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
}