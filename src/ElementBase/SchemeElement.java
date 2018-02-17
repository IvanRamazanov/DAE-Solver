/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ElementBase;

import java.util.ArrayList;
import java.util.List;

import raschetkz.RaschetKz;

/**
 *
 * @author Ivan
 */
public abstract class SchemeElement extends Element{
//    protected boolean Dymamic=false;

    protected List<ElemPin> electricContacts =new ArrayList();
    protected List<ElemMechPin> mechContacts=new ArrayList();

    private double contStep=15,maxX,mathContOffset;



    public SchemeElement(){
        super();
        maxX=viewPane.getBoundsInLocal().getMaxX();
        mathContOffset=viewPane.getBoundsInLocal().getMaxY()/2;

    }

    public SchemeElement(boolean catalog){
        super(catalog);
//        if(!catalog){
//            electricContacts.add(new ElemPin(this));    //for extend by gost
//            electricContacts.add(new ElemPin(this));
//        }

    }

    public abstract String[] getStringFunction();

    //Если есть, то добавь в список нодов, если нет, то нетрож нахуй, но верни элемент на выход
//    public abstract List<SchemeElement> expandElement(List<Wire> outNodes,boolean byGOST); // add sub elems to "elements" and sub br to "outNodes"

//    public boolean isDynamic(){
//        return(this.Dymamic);
//    }

    public List<ElemPin> getElemContactList(){
        return(electricContacts);
    }

    public List<ElemMechPin> getMechContactList(){
        return mechContacts;
    }

    final protected void addElemCont(ElemPin input){
        this.electricContacts.add(input);
        this.viewPane.getChildren().add(input.getView());
    }

    final protected void addMechCont(ElemMechPin input){
        this.mechContacts.add(input);
        this.viewPane.getChildren().add(input.getView());
    }

    /**
     * Удаляет элемент
     */
    @Override
    public void delete(){
        this.electricContacts.forEach(elemCont->{
            elemCont.clear();
            if(elemCont.getItsConnection()!=null){
                elemCont.getItsConnection().unPlug();
            }
        });
        RaschetKz.ElementList.remove(this);
        RaschetKz.drawBoard.getChildren().remove(this.getView());
    }

//    @Override
//    protected void openDialogStage() {
//        double maxnamelenght=0;
//        Stage subWind=new Stage();
//        subWind.setTitle("Параметры: "+this.getName());
//        VBox root=new VBox();
//        Scene scene=new Scene(root,300,200,Color.DARKCYAN);
//        subWind.setScene(scene);
////        subWind.
//
//        VBox top=new VBox();
//        for(Parameter p:this.getParameters()){
//            top.getChildren().add(p.getLayout());
//        }
//        Tab params=new Tab("Параметры элемента");
//        ScrollPane asd=new ScrollPane(top);
////        asd.setPannable(true);
//        //asd.setFitToWidth(true);
//        asd.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
//        //asd.setPrefViewportHeight(150);
//        //asd.setHvalue(0.5);
//        params.setContent(asd);
//
//        TabPane pane;
//
//        if(this.getInitials().isEmpty()){
//            pane=new TabPane(params);
//            pane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
//            root.getChildren().add(pane);
//        }else{
//            GridPane ttop=new GridPane();
//            ttop.addRow(0, new Label("Переменная"),new Label("Приоритет"),new Label("Значение"));
//            for(int k=0;k<this.getInitials().size();k++){
//                InitParam p=this.getInitials().get(k);
//                List<Node> pn=new ArrayList(p.getLayouts());
//                int siz=pn.size();
//                for(int i=siz-1;i>=0;i--){
//                    ttop.add(pn.get(i),i,k+1);
//                }
//            }
//            Tab inits=new Tab("Начальные условия");
//            ttop.getColumnConstraints().add(new ColumnConstraints(Control.USE_COMPUTED_SIZE));
//            ttop.getColumnConstraints().add(new ColumnConstraints(Control.USE_COMPUTED_SIZE));
//            ttop.getColumnConstraints().add(new ColumnConstraints(Control.USE_COMPUTED_SIZE));
//            ttop.setHgap(2);
//            inits.setContent(ttop);
////            ttop.getChildren().f
//            pane=new TabPane(params,inits);
//            pane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
//            root.getChildren().add(pane);
//        }
//        //---buttns
//        HBox bot=new HBox();
//        Button btn=new Button("Отмена");
//        btn.setOnAction((ActionEvent ae)->{
//            subWind.close();
//        });
//        bot.getChildren().add(btn);
//        btn=new Button("Ок");
//        btn.setOnAction((ActionEvent ae)->{
//            this.getParameters().forEach(data->{
//                data.update();
//            });
//            this.getInitials().forEach(data->{
//                data.update();
//            });
//            subWind.close();
//        });
//
//        bot.setAlignment(Pos.CENTER_RIGHT);
//        bot.getChildren().add(btn);
//        root.getChildren().add(bot);
//        //
//        //subWind.sizeToScene();
//        subWind.show();
//        subWind.setMaxHeight(asd.getContent().getLayoutBounds().getHeight()+bot.getHeight()+pane.getHeight());
//        scene.setOnKeyReleased(ke->{
//            if(ke.getCode()==KeyCode.ENTER){
//                this.getParameters().forEach(data->{
//                    data.update();
//                });
//                this.getInitials().forEach(data->{
//                    data.update();
//                });
//                subWind.close();
//            }
//        });
//        if(this.getParameters().size()>0){
//            this.getParameters().get(0).requestFocus();
//        }
//    }

    @Override
    public void init(){};



    final protected void addHiddenMathContact(char ch){
        if(ch=='i'){
            if(mathInputs==null) mathInputs=new ArrayList();
            MathInPin ic=new MathInPin();
            mathInputs.add(ic);
        }else{
            if(mathOutputs==null) mathOutputs=new ArrayList();
            MathOutPin oc=new MathOutPin();
            mathOutputs.add(oc);
        }
    }

    /**
     *
     * @param type 'i' or 'o'
     */
    final protected void addMathContact(char type){
        if(type=='i'){
            if(mathInputs==null) mathInputs=new ArrayList();
            int num=mathInputs.size();
            MathInPin ic=new MathInPin(0,num*contStep+mathContOffset);
            mathInputs.add(ic);
            viewPane.getChildren().add(ic.getView());
        }else{
            if(mathOutputs==null) mathOutputs=new ArrayList();
            int num=mathOutputs.size();
            MathOutPin oc=new MathOutPin(maxX,num*contStep+mathContOffset);
            mathOutputs.add(oc);
            viewPane.getChildren().add(oc.getView());
        }
    }



    /**
     * @return the inputs
     */
    public List<MathInPin> getInputs() {
        return mathInputs;
    }

    /**
     * @return the outputs
     */
    public List<MathOutPin> getOutputs() {
        return mathOutputs;
    }

}


