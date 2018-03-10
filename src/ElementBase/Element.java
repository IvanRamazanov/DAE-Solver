/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import Elements.Environment.Subsystem;
import MathPack.MatrixEqu;
import MathPack.Parser;
import MathPack.StringGraph;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import javafx.scene.text.*;
import javafx.stage.Stage;
import raschetkz.RaschetKz;

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
    protected List<ElectricPin> electricContacts =new ArrayList();
    protected List<MechPin> mechContacts=new ArrayList();
    private static double HEIGHT_FIT=5;
    private Subsystem itsSystem;
    private View view;

    protected double contStep=15,maxX,mathContOffset;

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
                catCm.show(viewPane, me.getScreenX(), me.getScreenY());  //getMarker()
            }
            if(me.getButton()==MouseButton.PRIMARY){
                if(me.getClickCount()==2){
                    catElemCreation();
                }
            }
            me.consume();
        }
    };

    public Element(){}

    public Element(Subsystem sys){
        cm=new ContextMenu();
        MenuItem deleteMenu =new MenuItem("Удалить");
        deleteMenu.setOnAction((ActionEvent ae)-> this.delete());
        MenuItem paramMenu=new MenuItem("Параметры");
        paramMenu.setOnAction((ActionEvent ae)-> this.openDialogStage());
        MenuItem rotate=new MenuItem("Поворот");
        rotate.setOnAction(ae-> rotate());
        cm.getItems().addAll(deleteMenu,paramMenu,rotate);
        imagePath="Elements/images/"+this.getClass().getSimpleName()+".png";

        //drawBoard.getChildren().add(this.getView());
//        getView(); // for init
        this.setItsSystem(sys);
        setParams();

        this.viewPane.setOnDragDetected(de->{
            if(de.getButton().equals(MouseButton.PRIMARY)&&de.isControlDown()){
                initDND();
            }
        });
    }

    public Element(boolean catalog){
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

            setParams();
            this.viewPane.setOnDragDetected(de->{
                if(de.getButton().equals(MouseButton.PRIMARY)){
                    initDND();
                }
            });
        }
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

            RaschetKz.elementList.add(elem);

//            if(elem instanceof SchemeElement)    raschetkz.RaschetKz.ElementList.add((SchemeElement)elem);
//            if(elem instanceof MathElement) raschetkz.RaschetKz.MathElemList.add((MathElement)elem);
        }catch(NoSuchMethodException|InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex){
            Logger.getLogger(Element.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public void setHeight(double val){
        view.setPreserveRatio(false);
        view.setFitHeight(val);
    }

    final public Pane getView(){
        if(viewPane==null){
            viewPane=new Pane();

            layout=new VBox();
            layout.setAlignment(Pos.TOP_CENTER);

            view=new View(imagePath,0,0);
            viewPane.setPrefSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);
            viewPane.setMaxSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);
            viewPane.focusedProperty().addListener((type,oldVal,newVal)->{
                if(newVal){
                    view.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 1.0, 1.0, 0, 0));
                }else{
                    view.setEffect(null);
                }
            });
            viewPane.getChildren().add(view);
            name=new Label();
            name.setTextAlignment(TextAlignment.CENTER);
            layout.getChildren().addAll(viewPane,name);
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
        return layout;
    }

    abstract protected void init();

    public void setItsSystem(Subsystem sys){
        itsSystem=sys;

        Pane draw = sys.getDrawBoard();

        draw.getChildren().add(getView());

        for(Pin p:getAllPins()){
            p.setSystem(sys);
        }
    }

    public Subsystem getItsSystem(){
        return itsSystem;
    }

    /**
     * Find element by its name
     * @param name
     * @return
     */
    public static Element findElement(String name){
        for(Element elem:RaschetKz.elementList){
            if(elem.getName().equals(name))
                return elem;
        }
        return null;
    }

    public final int findPin(Pin pin){
        int index=-1;
        if(pin instanceof MathInPin)
            index=mathInputs.indexOf(pin);
        else if(pin instanceof MathOutPin)
            index=mathOutputs.indexOf(pin);
        else  if(pin instanceof ElectricPin)
            index=electricContacts.indexOf(pin);
        else if(pin instanceof MechPin)
            index=mechContacts.indexOf(pin);
        return index;
    }


    /**
     * Pin name format: ClassSimpleName.index
     * @param pin
     * @return
     */
    public Pin getPin(String pin){
        Pin out=null;
        int index=Integer.valueOf(pin.substring(pin.indexOf('.')+1));
        if(pin.startsWith("MathInPin"))
            out=mathInputs.get(index);
        else if(pin.startsWith("MathOutPin"))
            out=mathOutputs.get(index);
        else if(pin.startsWith("ElectricPin"))
            out=electricContacts.get(index);
        else if(pin.startsWith("MechPin"))
            out=mechContacts.get(index);
        return out;
    }

    final protected void addHiddenMathContact(char ch){
        if(ch=='i'){
            if(mathInputs==null) mathInputs=new ArrayList();
            MathInPin ic=new MathInPin();
            ic.setOwner(this);
//            MathInPin ic=new MathInPin(this);
            mathInputs.add(ic);
        }else{
            if(mathOutputs==null) mathOutputs=new ArrayList();
            MathOutPin oc=new MathOutPin();
            oc.setOwner(this);
//            MathOutPin oc=new MathOutPin(this);
            mathOutputs.add(oc);
        }
    }

    public List<ElectricPin> getElemContactList(){
        return(electricContacts);
    }

    /**
     *
     * @param type 'i' or 'o'
     */
    protected Pin addMathContact(char type){
        Pin out;
        if(type=='i'){
            if(mathInputs==null) mathInputs=new ArrayList();
            int num=mathInputs.size();
            out=new MathInPin(this,0,num*contStep+mathContOffset);
            mathInputs.add((MathInPin)out);
            viewPane.getChildren().add(out.getView());
        }else{
            if(mathOutputs==null) mathOutputs=new ArrayList();
            int num=mathOutputs.size();
            out=new MathOutPin(this,maxX,num*contStep+mathContOffset);
            mathOutputs.add((MathOutPin)out);
            viewPane.getChildren().add(out.getView());
        }
        return out;
    }

    public void configurate(String name,String elemInfo){
        setName(name);

        String[] lines = elemInfo.split(System.getProperty("line.separator"));
        double[] layout= Parser.parseRow(Parser.getKeyValue(lines,"<Layout>"));
        double x=layout[0],
                y=layout[1],
                rotate=layout[2];
        getView().setLayoutX(x);
        getView().setLayoutY(y);
        setRotation(rotate);

        //parameter cycle
        Integer pSt=null,pEn=null;
        for(int i=0;i<lines.length;i++){
            String l=lines[i];
            if(l.equals("<Parameters>")){
                pSt=Integer.valueOf(i+1);
            }else if(l.equals("</Parameters>")){
                pEn=Integer.valueOf(i);
                break;
            }
        }
        String[] pLines=Arrays.copyOfRange(lines,pSt,pEn);
        for(Parameter param:getParameters()){
            String pName=param.getName();

            pSt=null;
            pEn=null;
            for(int i=0;i<pLines.length;i++){
                String l=pLines[i];
                if(l.equals("<Param."+pName+">")){
                    pSt=Integer.valueOf(i+1);
                }else if(l.equals("</Param."+pName+">")){
                    pEn=Integer.valueOf(i);
                    break;
                }
            }

            if(pSt!=null&&pEn!=null){
                String[] pInfo=Arrays.copyOfRange(pLines,pSt,pEn);

                String type=Parser.getKeyValue(pInfo,"<Type>");

                param.setValue(Parser.getKeyValue(pInfo,"<Value>"));
            }
        }

        pSt=null;pEn=null;
        for(int i=0;i<lines.length;i++){
            String l=lines[i];
            if(l.equals("<InitParameters>")){
                pSt=Integer.valueOf(i+1);
            }else if(l.equals("</InitParameters>")){
                pEn=Integer.valueOf(i);
                break;
            }
        }
        pLines=Arrays.copyOfRange(lines,pSt,pEn);
        for(InitParam param:getInitials()){
            String pName=param.getName();

            pSt=null;
            pEn=null;
            for(int i=0;i<pLines.length;i++){
                String l=pLines[i];
                if(l.equals("<Param."+pName+">")){
                    pSt=Integer.valueOf(i+1);
                }else if(l.equals("</Param."+pName+">")){
                    pEn=Integer.valueOf(i);
                    break;
                }
            }

            if(pSt!=null&&pEn!=null){
                String[] pInfo=Arrays.copyOfRange(pLines,pSt,pEn);

                String type=Parser.getKeyValue(pInfo,"<Type>");

                param.setValue(Parser.getKeyValue(pInfo,"<Value>"));
            }
        }
    }

    public final void save(BufferedWriter bw) throws IOException{
//        bw.write("<Name>");
//        bw.write(getName());
        bw.write("<"+getName()+">");bw.newLine();

        bw.write("<ClassName>");
        bw.write(getClass().getName());
        bw.write("</ClassName>");bw.newLine();

        bw.write("<Subsystem>");
        bw.write(getItsSystem().getName());
        bw.write("</Subsystem>");bw.newLine();

        bw.write("<Layout>");
        String str="["+getView().getLayoutX()+" "+getView().getLayoutY()+" "+getRotation()+"]";
        bw.write(str);
        bw.write("</Layout>");bw.newLine();

        bw.write("<Parameters>");bw.newLine();
        int cnt=0;
        for(Element.Parameter param:getParameters()){
            bw.write("<Param."+param.getName()+">");bw.newLine();

            bw.write("<Type>");
            bw.write(param.getClass().getSimpleName());
            bw.write("</Type>");bw.newLine();

            bw.write("<Name>");
            bw.write(param.getName());
            bw.write("</Name>");bw.newLine();

            bw.write("<Value>");
            bw.write(param.getStringValue());
            bw.write("</Value>");bw.newLine();

            bw.write("</Param."+param.getName()+">");bw.newLine();

            cnt++;
        }
        bw.write("</Parameters>");bw.newLine();

        bw.write("<InitParameters>");bw.newLine();
        cnt=0;
        for(Element.InitParam param:getInitials()){
            bw.write("<Param."+param.getName()+">");bw.newLine();

            bw.write("<Name>");
            bw.write(param.getName());
            bw.write("</Name>");bw.newLine();

            bw.write("<Value>");
            bw.write(param.getStringValue());
            bw.write("</Value>");bw.newLine();

            bw.write("</Param."+param.getName()+">");bw.newLine();

            cnt++;
        }
        bw.write("</InitParameters>");bw.newLine();

        bw.write("</"+getName()+">");bw.newLine();
    }

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

    final protected void addElemCont(ElectricPin input){
        this.electricContacts.add(input);
        this.viewPane.getChildren().add(input.getView());
    }

    final protected void addMechCont(MechPin input){
        this.mechContacts.add(input);
        this.viewPane.getChildren().add(input.getView());
    }

    abstract public void delete();

    protected void openDialogStage() {
        final Stage subWind=new Stage();
        subWind.setTitle("Параметры: "+this.getName());
        VBox root=new VBox();
        final Scene scene=new Scene(root,300,200,Color.DARKCYAN);
        subWind.setScene(scene);

        Text header=new Text(getName()+"\n\n");
        header.setFont(Font.font("Times New Roman", FontWeight.BOLD, 12));
        root.getChildren().add(new TextFlow(header, new Text(getDescription())));

        VBox top=new VBox();
        for(Parameter p:this.getParameters()){
            top.getChildren().add(p.getLayout());
        }
        Tab params=new Tab("Параметры элемента");
        ScrollPane asd=new ScrollPane(top);
        asd.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
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
        subWind.show();
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

    public List<Pin> getAllPins(){
        List<Pin> out=new ArrayList<>();
        out.addAll(electricContacts);
        out.addAll(mechContacts);
        out.addAll(mathInputs);
        out.addAll(mathOutputs);
        return out;
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

//    public List<Double> getInitialVals() {
//        List<Double> out=new ArrayList();
//        initials.forEach(v->{
//            out.add(v.getDoubleValue());
//        });
//        return out;
//    }

    /**
     * @param initials the initials to set
     */
    public void setInitials(List<InitParam> initials) {
        this.initials = initials;
    }

    public void toBack(){
        getView().toBack();
    }

    public void toFront(){
        getView().toFront();
    }
    /**
     * @param name the name to set
     */
    protected final void setName(String name) {
        int cnt=1;
        String nameTmp=name;
        for(int i=0;i<RaschetKz.elementList.size();i++){
            String elemName=RaschetKz.elementList.get(i).getName();
            if(elemName.equals(nameTmp)){
                nameTmp=name+cnt;
                cnt++;
                i=-1;
            }
        }

        this.name.setText(nameTmp);
    }


    public class Parameter{
        protected double[][] value;
        protected String name="";
        private VBox layout;
        protected TextField text;
        protected boolean isScalar=false,isVector=false;

        protected Parameter(){
        }

        private Parameter(String name,double initVal){
            this.name=name;
            value=new double[1][1];
            value[0][0]=initVal;
            this.layout=new VBox();
            this.text=new TextField(Double.toString(initVal));
            layout.getChildren().add(new Label(name));
            layout.getChildren().add(text);
        }

        public Parameter(String name,String initVal){
            this.name=name;
            value=parse(initVal);
            layout=new VBox();
            text=new TextField(initVal);
            layout.getChildren().add(new Label(name));
            layout.getChildren().add(text);
        }

        public void setChangeListener(ChangeListener<String> changeListener){
            text.textProperty().addListener(changeListener);
        }

        void update(){
//            DoubleStringConverter conv=new DoubleStringConverter();
            value=parse(text.getText());
        }

        /**
         * Value at t=0.
         * @return
         */
        public double[][] getDoubleValue(){
            return(value);
        }

        protected void requestFocus(){
            text.requestFocus();
        }

        @Override
        public String toString(){
            return text.getText();
        }

        public String getStringValue(){
            return text.getText();
        }

        public void setValue(String val){
            value=parse(val);
            this.text.setText(val);
        }

        public void setValue(double[][] val){
            value=val;
            this.text.setText(parse(val));
        }

        private String parse(double[][] arr){
            String out="";
            if(arr.length==1){
                if(arr[0].length==1){
                    out= Double.toString(arr[0][0]);
                }else
                    out= Arrays.toString(arr[0]);
            }else{
                out+='[';
                for(double[] row:arr){
                    out+=Arrays.toString(row);
                }
                out+=']';
            }
            return out;
        }

        protected double[][] parse(String str){
            double[][] out=null;
            List<String> lines=new ArrayList<>();
            if(str.charAt(0)=='['){
                if(str.charAt(str.length()-1)==']'){
                    str=str.substring(1,str.length()-1);
                    String temp="";
                    for(int i=0;i<str.length();i++){
                        char c=str.charAt(i);
                        if(c!=';')
                            temp+=c;
                        else {
                            lines.add(temp);
                            temp="";
                        }
                    }
                    if(!temp.isEmpty())
                        lines.add(temp);
                }else{
                    throw new Error("Corrupted parameter! "+this.name+" = "+str);
                }
                int numberOfRows=lines.size();

                // now we have row of lines
                int i=0;
                Integer numberOfCols=null;
                for(String row:lines){
                    String temp="";
                    List<String> cols=new ArrayList<>();
                    for(int j=0;j<row.length();j++){
                        char c=row.charAt(j);
                        if(c!=','&&c!=' '){
                            temp+=c;
                        }else{
                            if(!temp.isEmpty()) {
                                cols.add(temp);
                                temp = "";
                            }
                        }
                    }
                    if(!temp.isEmpty())
                        cols.add(temp);

                    if(numberOfCols!=null){
                        if(!numberOfCols.equals(cols.size()))
                            throw new Error("Dimensions mismatch!\n Parameter: "+this.name+" line: "+cols.toString()+"\n in "+str);
                    }else{
                        numberOfCols=Integer.valueOf(cols.size());
                        out=new double[numberOfRows][numberOfCols];
                    }

                    // cast to array
                    int j=0;
                    for(String col:cols){
                        out[i][j]= StringGraph.doubleValue(col);
                        j++;
                    }
                    i++;
                }

            }else{
                // scalar case
                out=new double[1][1];
                out[0][0]=StringGraph.doubleValue(str);
            }
            if(this instanceof ScalarParameter){
                if(out.length!=1&&out[0].length!=1)
                    throw new Error("Dimensions mismatch in "+name+". Must be a scalar.");
            }else if(this instanceof VectorParameter){
                if(out.length>1&&out[0].length>1) {
                    throw new Error("Dimensions mismatch in "+name+". Must be a vector (but matrix).");
                }else {
                    if(out.length>1){
                        // transpose
                        out=MatrixEqu.transpose(out);
                    }
                }
//                    if(out[0].length!=1)
//                        throw new Error("Dimensions mismatch in "+name+". Must be a vector.");
            }
            return out;
        }

        public Pane getLayout(){
            return layout;
        }

        public String getName(){
            return name;
        }
    }

    public class ScalarParameter extends Parameter{
        ScalarParameter(){}

        public ScalarParameter(String name,double value){
            super(name, value);
            isScalar=true;
        }

        public double getValue(){
            return value[0][0];
        }

        public void setValue(double val){
            value[0][0]=0;
            this.text.setText(Double.toString(val));
        }
    }

    public class VectorParameter extends Parameter{
        VectorParameter(){
            isVector=true;
        }


        public VectorParameter(String name,String value){
            super(name, value);
            isVector=true;
        }

        public VectorParameter(String name,double val){
            super(name,val);
            isVector=true;
        }

        public double[] getValue(){
            return value[0];
        }
    }

    public class InitParam extends ScalarParameter{
        private boolean priority;
        private ComboBox box;
        private List<Node> layout;

        /**
         *
         * @param name
         * @param initVal
         */
        public InitParam(String name, String initVal){
            this.name=name;
            value=parse(initVal);
            this.layout=new ArrayList();
            this.text=new TextField(initVal);
            layout.add(new Label(name));
            box=new ComboBox();
            box.getItems().addAll("High","Low");
            box.setValue("Low");
            layout.add(box);
            layout.add(text);

            isScalar=true;
        }

        public InitParam(String name, double val){
            this.name=name;
            value=new double[1][1];
            value[0][0]=val;
            this.layout=new ArrayList();
            this.text=new TextField(Double.toString(val));
            layout.add(new Label(name));
            box=new ComboBox();
            box.getItems().addAll("High","Low");
            box.setValue("Low");
            layout.add(box);
            layout.add(text);

            isScalar=true;
        }

        @Override
        void update(){
            value=parse(this.text.getText());
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
}
