/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package raschetkz;

import MathPack.MatrixEqu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author ramazanov_im
 */
public class Painter {
    private List<List<Double>> data;
    private List<Double> time;
    private double maxY,natMaxY,minY,natMinY,maxT,natMaxT,minT,natMinT;
    private ContextMenu contMenu;
    private Scene itsScene;
    private BorderPane plotAndAxArea;
    private final double axRightWidth=40,axHeight=40,axLeftWidth=90;
    private xAxis xAx;
    private yAxis yAx;
    private Pane plotter;
    private List<Polyline> lines;
    private Double ax,ay,rx,ry;
    private Line onDragLin1,onDragLinStr,onDragLin2;
    private double M=0.5;
    private int zoomType;
    private ZoomRectangle zoomRect;
    private EventHandler dragEvent;
    final private MeshGrid grid=new MeshGrid();
    static final EventType<ViewDataEvent> AZAZA=new EventType<>(Event.ANY,"AZAZA");

    public Painter(){
        initGui();
        zoomType=0;
        plotter.setLayoutX(axLeftWidth);
//        plotter.setMaxSize(400-axWidth*2.0, 300-axHeight);
//        plotter.setMinSize(400-axWidth*2.0, 300-axHeight);
//        plotter.setMinWidth(400.0-axWidth*2.0);// plotter has offset extra axWidth
//        plotter.setMaxWidth(400.0-axWidth*2.0);
//        plotter.setMinHeight(300.0-axHeight);
//        plotter.setMaxHeight(300.0-axHeight);
        plotter.setStyle("-fx-border-color: #000000");
        plotter.setOnMousePressed(e->{
            if(e.getButton().equals(MouseButton.PRIMARY))
                if(zoomType!=0){
                    ax=e.getX();
                    ay=e.getY();
                }
        });
        plotter.setOnDragDetected(de->{
            if(de.getButton().equals(MouseButton.PRIMARY))
                switch(zoomType){
                    case 1: //horizontal
                        onDragLin1=new Line(ax,ay+10,ax,ay-10);
                        onDragLinStr=new Line(ax,ay,de.getX(),ay);
                        onDragLin2=new Line(de.getX(),ay+10,de.getX(),ay-10);
                        onDragLin2.endXProperty().bind(onDragLinStr.endXProperty());
                        onDragLin2.startXProperty().bind(onDragLinStr.endXProperty());
                        plotter.getChildren().addAll(onDragLin1,onDragLinStr,onDragLin2);
                        plotter.setCursor(Cursor.H_RESIZE);
                        plotter.addEventHandler(MouseEvent.MOUSE_DRAGGED,dragEvent);
                        break;
                    case 2: //vertical
                        onDragLin1=new Line(ax-10,ay,ax+10,ay);
                        onDragLinStr=new Line(ax,ay,ax,de.getY());
                        onDragLin2=new Line(ax-10,de.getY(),ax+10,de.getY());
                        onDragLin2.endYProperty().bind(onDragLinStr.endYProperty());
                        onDragLin2.startYProperty().bind(onDragLinStr.endYProperty());
                        plotter.getChildren().addAll(onDragLin1,onDragLinStr,onDragLin2);
                        plotter.setCursor(Cursor.V_RESIZE);
                        plotter.addEventHandler(MouseEvent.MOUSE_DRAGGED,dragEvent);
                        break;
                    case 3: //border
                        zoomRect.setStart(ax,ay);
                        zoomRect.setEnd(de.getX(),de.getY());
                        plotter.getChildren().addAll(zoomRect.get());
                        plotter.addEventHandler(MouseEvent.MOUSE_DRAGGED,dragEvent);
                }
        });
        plotter.setOnMouseClicked(e->{
            // contextmenu!
            if(e.getButton().equals(MouseButton.SECONDARY))
                contMenu.show(plotter,e.getScreenX(),e.getScreenY());
            else if(e.getButton().equals(MouseButton.PRIMARY))
                contMenu.hide();
        });
        plotter.setOnMouseReleased(de->{
            plotter.setCursor(Cursor.DEFAULT);
            if(ax!=null){


//                if(rx<0.0) rx=0.0;
//                if(ry<0.0) ry=0.0;
                ax=ax*(maxT-minT)/plotter.getWidth()+minT;
                ay=-ay*(maxY-minY)/plotter.getHeight()+maxY;
                switch(zoomType){
                    case 1:
                        rx=onDragLin2.getEndX();
                        ry=onDragLin2.getEndY();
                        rx=rx*(maxT-minT)/plotter.getWidth()+minT;
                        ry=-ry*(maxY-minY)/plotter.getHeight()+maxY;

                        if(rx.doubleValue()!=ax.doubleValue()){ // horizontal drag
                            minT=Math.min(ax, rx)-Math.abs(ax-rx)*0.015;
                            maxT=Math.max(ax, rx)+Math.abs(ax-rx)*0.015;
                            draw();
                        }
                        plotter.getChildren().removeAll(onDragLin1,onDragLinStr,onDragLin2);
                        onDragLin1=null;
                        onDragLinStr=null;
                        onDragLin2=null;
                        break;
                    case 2:
                        rx=onDragLin2.getEndX();
                        ry=onDragLin2.getEndY();
                        rx=rx*(maxT-minT)/plotter.getWidth()+minT;
                        ry=-ry*(maxY-minY)/plotter.getHeight()+maxY;
                        if(ry.doubleValue()!=ay.doubleValue()){
                            maxY=Math.max(ry,ay)+Math.abs(ry-ay)*0.015;
                            minY=Math.min(ry,ay)-Math.abs(ry-ay)*0.015;
                            draw();
                        }
                        plotter.getChildren().removeAll(onDragLin1,onDragLinStr,onDragLin2);
                        onDragLin1=null;
                        onDragLinStr=null;
                        onDragLin2=null;
                        break;
                    case 3:
                        rx=zoomRect.getEndX();
                        ry=zoomRect.getEndY();
                        rx=rx*(maxT-minT)/plotter.getWidth()+minT;
                        ry=-ry*(maxY-minY)/plotter.getHeight()+maxY;
                        if((ax.doubleValue()!=rx.doubleValue())&&(ay.doubleValue()!=ry.doubleValue())){
                            minT=Math.min(ax, rx)-Math.abs(ax-rx)*0.015;
                            maxT=Math.max(ax, rx)+Math.abs(ax-rx)*0.015;
                            maxY=Math.max(ry,ay)+Math.abs(ry-ay)*0.015;
                            minY=Math.min(ry,ay)-Math.abs(ry-ay)*0.015;
                            draw();
                        }
//                        zoomRect=null;
                        plotter.getChildren().removeAll(zoomRect.get());
                }
                ax=null;
                ay=null;
                plotter.removeEventHandler(MouseEvent.MOUSE_DRAGGED, dragEvent);
            }
        });
        plotter.widthProperty().addListener((type,oldV,newV)->{
//            plotter.setMinWidth(newV.doubleValue()-axWidth*2.0);// plotter has offset extra axWidth
//            plotter.setMaxWidth(newV.doubleValue()-axWidth*2.0);
            draw();
        });
        plotter.heightProperty().addListener((type,oldV,newV)->{
//            plotter.setMinHeight(newV.doubleValue()-axHeight-plotter.localToScene(0, 0).getY());
//            plotter.setMaxHeight(newV.doubleValue()-axHeight-plotter.localToScene(0, 0).getY());
            draw();
        });
    }

    public void plot(Stage paintStage,List<List<Double>> data,List<Double> time,double minY,double maxY){
        this.data = data;
        this.time = time;
        paintStage.setScene(itsScene);
        if(data.isEmpty()){
            this.minT=0;
            this.maxT=1;
            this.minY=0;
            this.maxY=1;
            yAx.updateAxes();
            xAx.updateAxes();
        }else{
            this.minT = time.get(0);
            this.maxT = time.get(time.size()-1);
            if(maxY-minY<0.000001){
                this.maxY = maxY+1.0;
                this.minY = minY-1.0;
            }else{
                this.maxY = maxY+(maxY-minY)*0.015;
                this.minY = minY-(maxY-minY)*0.015;
            }

            plotter.getChildren().clear();
            lines.clear();
            draw();
            plotter.getChildren().addAll(lines);
        }
        this.natMinT = this.minT;
        this.natMaxT = this.maxT;
        this.natMinY = this.minY;
        this.natMaxY = this.maxY;
    }

    void save(){
        PrinterJob pj=javafx.print.PrinterJob.createPrinterJob(Printer.getDefaultPrinter());
        pj.showPrintDialog(contMenu);
        pj.printPage(plotAndAxArea);
        pj.endJob();
    }

    void draw(){
//        double maxT=time.get(time.size()-1),minT=0;
        double signalMax,signalMin;
        double xMinRestriction=0.0,
                xMaxRestriction=plotter.getWidth(),
                yMinRestriction=0.0,
                yMaxRestriction=plotter.getHeight();
//        if(maxY==minY){
//            signalMax=maxY+1;
//            signalMin=minY-1;
//        }else{
//            signalMax=maxY*1.05;
//            signalMin=minY*1.05;
//        }
//        maxT*=0.5;
        double yScale=(yMaxRestriction-yMinRestriction)/(maxY-minY),
                xScale=(xMaxRestriction-xMinRestriction)/(maxT-minT),
                maxY_M=maxY*yScale,
                minY_M=minY*yScale,
                dY=((maxY_M+minY_M)/2.0-(yMinRestriction-yMaxRestriction)/2.0),
                dX=minT*xMaxRestriction/(maxT-minT);
        for(int k=0;k<data.size();k++){
            if(lines.size()<=k){
                Polyline line=new Polyline();
                lines.add(line);
                switch(k){
                    case 0:
                        line.setStroke(Color.RED);
                        break;
                    case 1:
                        line.setStroke(Color.GREEN);
                        break;
                    case 2:
                        line.setStroke(Color.BLUE);
                        break;
                    default:
                        line.setStroke(Color.hsb((111.0+k*11.0)%360.0, 1.0, 1.0));
                        break;
                }
            }else{
                lines.get(k).getPoints().clear();
            }
            int oldX=-1,newX;
            int oldY=-1,newY;

            for(int i=0;i<data.get(k).size();i++){
                double y=data.get(k).get(i)*-1.0;
                double x=time.get(i);
//                y=(-y+maxY/M)*(maxy.doubleValue()-axHeight)/(maxY-minY)*M;
                // масштабирование
                y=y*yScale;
                x=x*xScale;
                //смещение
                y=y+dY;
                x=x-dX;
                newX=(int) x;
                newY=(int) y;
                if(oldX!=newX){
//                    if(plotter.contains(x, y)){
                    if(newX>xMinRestriction){
                        lines.get(k).getPoints().add(x);
                        lines.get(k).getPoints().add(y);
                    }
                    if(newX>xMaxRestriction) break;
                    oldX=newX;
                }
//                else if(oldY!=newY){
//                    // check Y
//                    lines.get(k).getPoints().add(x);
//                    lines.get(k).getPoints().add(y);
//                    oldY=newY;
//                }
            }
        }

        xAx.updateAxes();
        yAx.updateAxes();
    }

    private void initGui(){
        lines=new ArrayList();
//        Pane root=new Pane();
        plotAndAxArea=new BorderPane();

//        root.getChildren().add(plotAndAxArea);
        itsScene=new Scene(plotAndAxArea,400, 300);
        itsScene.getStylesheets().add("raschetkz/mod.css");
        plotter=new Pane();

        contMenu=new ContextMenu();
        MenuItem zoomOut=new MenuItem("Zoom Out");
        zoomOut.setOnAction(e->{
            switch(zoomType){
                case 1:
                    double dx=(maxT-minT)/2;
                    maxT+=dx;
                    minT-=dx;
                    draw();
                    break;
                case 2:
                    double dy=(maxY-minY)/2;
                    maxY+=dy;
                    minY-=dy;
                    draw();
                    break;
                case 3:
                    dx=(maxT-minT)/2;
                    dy=(maxY-minY)/2;
                    maxT+=dx;
                    minT-=dx;
                    maxY+=dy;
                    minY-=dy;
                    draw();
            }

        });
        MenuItem zoomReset=new MenuItem("Reset Scale");
        zoomReset.setOnAction(e->{
            maxT=natMaxT;
            minT=natMinT;
            maxY=natMaxY;
            minY=natMinY;
            draw();
        });
        MenuItem showValue=new MenuItem("Show Value");
        showValue.setOnAction(e->{


        });
        MenuItem print=new MenuItem("Print");
        print.setOnAction(e->{
            save();
        });

        MenuItem importData=new MenuItem("Import data");
        importData.setOnAction(e->{
            try{
                FileChooser fc=new FileChooser();
                fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("text","*.txt"));
                File f=fc.showOpenDialog(null);
                Scanner sc=new Scanner(f);
                sc.useDelimiter("\r\n");

                // init arrays
                String line=sc.next(); // header with names
                int idx=line.indexOf(' ');
                time=new ArrayList<>();
                line=line.substring(idx+1);
                data=new ArrayList<>();
                idx=line.indexOf(' ');
                while(idx!=-1){
                    data.add(new ArrayList<>());
                    line=line.substring(idx+1);
                    idx=line.indexOf(' ');
                }

                maxY=Double.MIN_VALUE;
                minY=Double.MAX_VALUE;
                //read data
                while(sc.hasNext()){
                    line=sc.next();
                    idx=line.indexOf(' ');
                    double t=Double.valueOf(line.substring(0,idx));
                    time.add(t);
                    line=line.substring(idx+1);
                    int i=0;
                    idx=line.indexOf(' ');
                    while(idx!=-1){
                        double val = Double.valueOf(line.substring(0, idx));
                        maxY=StrictMath.max(maxY,val);
                        minY=StrictMath.min(minY,val);
                        data.get(i++).add(val);
                        line=line.substring(idx+1);
                        idx=line.indexOf(' ');
                    }
                }
                double mY= maxY+(maxY-minY)*0.015,
                miY = minY-(maxY-minY)*0.015;
                natMaxY=maxY=mY;
                natMinY=minY=miY;
                natMinT=minT=time.get(0);
                natMaxT=maxT=time.get(time.size()-1);
                plotter.getChildren().clear();
                lines.clear();
                draw();
                plotter.getChildren().addAll(lines);
            }catch(Exception ex){
                System.err.println(ex.getMessage());
            }
        });
        contMenu.getItems().addAll(zoomOut,zoomReset,showValue,print,importData);

        plotAndAxArea.setCenter(plotter);
        Pane left=new Pane();
        left.setMaxWidth(axLeftWidth);
        left.setMinWidth(axLeftWidth);
        Rectangle rect=new Rectangle(axLeftWidth, 0, Color.WHITE);
        rect.heightProperty().bind(left.heightProperty());
        left.getChildren().add(rect);

        Pane right=new Pane();
        right.setMaxWidth(axRightWidth);
        right.setMinWidth(axRightWidth);
        rect=new Rectangle(axRightWidth, 0, Color.WHITE);
        rect.heightProperty().bind(right.heightProperty());
        right.getChildren().add(rect);
        Pane top=new Pane();
        top.setMaxHeight(axHeight);
        top.setMinHeight(axHeight);
        rect=new Rectangle(0, axHeight, Color.WHITE);
        rect.widthProperty().bind(top.widthProperty());
//        root.getChildren().add(new Menushka());
        top.getChildren().addAll(rect,new Menushka());
        Pane bot=new Pane();
        bot.setMaxHeight(axHeight);
        bot.setMinHeight(axHeight);
        rect=new Rectangle(0, axHeight, Color.WHITE);
        rect.widthProperty().bind(bot.widthProperty());
        bot.getChildren().add(rect);
        xAx=new xAxis();
        bot.getChildren().add(xAx);
        yAx=new yAxis();
        left.getChildren().add(yAx);
        plotAndAxArea.setBottom(bot);
        plotAndAxArea.setLeft(left);
        plotAndAxArea.setRight(right);
        plotAndAxArea.setTop(top);
        dragEvent=(EventHandler<MouseEvent>)(MouseEvent e) -> {
            double x=e.getX();
            if(x<0.0)
                x=0.0;
            else if(x>plotter.getWidth())
                x=plotter.getWidth();
            double y=e.getY();
            if(y<0.0)
                y=0.0;
            else if(y>plotter.getHeight())
                y=plotter.getHeight();
            switch(zoomType){
                case 1:
                    onDragLinStr.setEndX(x);
                    break;
                case 2:
                    onDragLinStr.setEndY(y);
                    break;
                case 3:
                    zoomRect.setEnd(x,y);
            }
        };
        zoomRect=new ZoomRectangle(0,0,0,0);
    }

    private List<Double> getValue(double x0){
        double difference=Double.MAX_VALUE;
        List<Double> out=MatrixEqu.getColumn(data, 0);

        for(int i=0;i<time.size();i++){
            double t=time.get(i);
            if(Math.abs(x0-t)<difference){
                difference=Math.abs(x0-t);
                out=MatrixEqu.getColumn(data, i);
            }else{
                break;
            }
        }
        return out;
    }

    private class xAxis extends Pane{
        //        final private Pane root;
//        private ReadOnlyDoubleProperty rootHeight,rootWidth;
        final private List<Line> axLines=new ArrayList();
        final private List<Label> axLabels=new ArrayList();
        final private int dashLen=5;

        xAxis(){
//            root=parent;
        }

        void updateAxes(){
            this.getChildren().removeAll(axLines);
            this.getChildren().removeAll(axLabels);
            axLabels.clear();
            axLines.clear();
            createTick(minT);
            createTick(maxT);
            this.getChildren().addAll(axLabels);
            this.getChildren().addAll(axLines);
        }

        void createTick(double val){
            double x=plotter.getWidth()/(maxT-minT)*(val-minT);
            x+=axLeftWidth;
            axLines.add(new Line(x,0,x,dashLen));
            Label lbl=new Label(String.format("%.3f", val));
            lbl.setLayoutX(x-lbl.getWidth()/2.0); // - len of string/2 !!!!!!!
            lbl.setLayoutY(dashLen+4);
            axLabels.add(lbl);
        }
    }

    private class yAxis extends Pane{
        //        Pane root;
//        ReadOnlyDoubleProperty rootHeight,rootWidth;
        final List<Line> axLines=new ArrayList();
        final List<Label> axLabels=new ArrayList();
        final int dashLen=5;

        yAxis(){
        }

        void updateAxes(){
            this.getChildren().removeAll(axLines);
            this.getChildren().removeAll(axLabels);
            axLabels.clear();
            axLines.clear();
            grid.updateY();
            this.getChildren().addAll(axLabels);
            this.getChildren().addAll(axLines);
        }

        double createTick(double val){
            double y=plotter.getHeight()/(maxY-minY)*(maxY-val);
            axLines.add(new Line(axLeftWidth-dashLen,y,axLeftWidth,y));
            Label lbl=new Label(String.format("%6.4e", val));
            lbl.setLayoutX(0.0); // - len of string/2 !!!!!!!
            lbl.setLayoutY(y-lbl.getHeight()/2.0);
            axLabels.add(lbl);
            return y;
        }
    }

    private class ValueRepresenter{
        VBox layout;
        Line line;
        double x,pX,y,pY;
        EventHandler<ViewDataEvent> mpressed;

        ValueRepresenter(){
            layout=new VBox();
            line=new Line();
        }

        void init(){
            mpressed= (vde)->{
                if(!data.isEmpty()){
                    pX=vde.getX();
                    pY=vde.getY();

                    double mX=vde.getX(),xMinRestriction=0.0,xMaxRestriction=plotter.getWidth(),
                            xScale=(maxT-minT)/(xMaxRestriction-xMinRestriction),
                            dX=(maxT-minT)/xMaxRestriction;
                    line.setStartX(mX);
                    line.setEndX(mX);
                    line.setStartY(plotter.getHeight());
                    line.setEndY(0);


                    mX=mX*xScale+minT;
                    x=mX;

                    List<Double> y=getValue(mX);
                    show(mX, y);

                }
            };

            plotter.addEventFilter(AZAZA, mpressed);

            plotter.getChildren().add(line);
            plotter.getChildren().add(layout);

            update();
        }

        void show(double x,List<Double> y){
            layout.setLayoutX(pX);
            layout.setLayoutY(pY);

            layout.getChildren().clear();
            layout.getChildren().add(new Label("time: "+x));
            for(int i=0;i<y.size();i++){
                layout.getChildren().add(new Label("In"+(i+1)+": "+y.get(i)));
            }
        }

        void update(){
            //Rescale
            double xMinRestriction=0.0,xMaxRestriction=plotter.getWidth(),
                    xx=(x-minT)*(xMaxRestriction-xMinRestriction)/(maxT-minT);
            line.setEndX(xx);
            line.setStartX(xx);
            line.setStartY(plotter.getHeight());

            layout.setLayoutX(xx);
        }

        void delete(){
            plotter.addEventFilter(AZAZA, mpressed);
            plotter.getChildren().removeAll(line,layout);
        }

    }

    private class MeshGrid{
        List<Line> horizLines;

        MeshGrid(){
            horizLines=new ArrayList();
        }

        void updateY(){
            plotter.getChildren().removeAll(horizLines);
            horizLines.clear();
            double y=yAx.createTick(minY);
            addHorizLine(y);
//            yStep.add(y);

            if(maxY>0.0&&minY<0.0){
                y=yAx.createTick(0.0);
                addHorizLine(y);
            }
            y=yAx.createTick(maxY);
            addHorizLine(y);

            plotter.getChildren().addAll(horizLines);
        }

        void addHorizLine(double yPos){
            horizLines.add(new Line(0, yPos, plotter.getWidth(), yPos));
        }
    }

    private class Menushka extends MenuBar{

        public Menushka(){
            Menu zoom=new Menu("Zoom");

            ToggleGroup tg=new ToggleGroup();
            final RadioMenuItem rmi1=new RadioMenuItem("Horizontal");
            rmi1.setToggleGroup(tg);
            zoom.getItems().add(rmi1);
            final RadioMenuItem rmi2=new RadioMenuItem("Vertical");
            rmi2.setToggleGroup(tg);
            zoom.getItems().add(rmi2);
            RadioMenuItem rmi3=new RadioMenuItem("Area");
            rmi3.setToggleGroup(tg);
            zoom.getItems().add(rmi3);
            MenuItem mi=new MenuItem("Cancel");
            mi.setOnAction((n)->{
                zoomType=0;
                tg.selectToggle(null);
            });
            zoom.getItems().add(new SeparatorMenuItem());
            zoom.getItems().add(mi);
            tg.selectedToggleProperty().addListener((type,oldV,newV)->{
                zoomType=tg.getToggles().indexOf(newV)+1;
            });

            //     Data
            Menu value=new Menu("Data");
            RadioMenuItem rmi=new RadioMenuItem("Press to show values");
            value.getItems().add(rmi);
            ValueRepresenter vr=new ValueRepresenter();
            EventHandler<MouseEvent> mdrag=(me)->{
                plotter.fireEvent(new ViewDataEvent(vr,me.getX(),me.getY()));
            };
            EventHandler<MouseEvent> mpres=(me)->{
                plotter.addEventFilter(MouseEvent.MOUSE_DRAGGED, mdrag);
                plotter.fireEvent(new ViewDataEvent(vr,me.getX(),me.getY()));
            };


            rmi.selectedProperty().addListener((t,o,n)->{
                if(n){
                    vr.init();
                    plotter.addEventFilter(MouseEvent.MOUSE_PRESSED, mpres);
                    plotter.addEventFilter(MouseEvent.MOUSE_RELEASED, me->{
                        plotter.removeEventFilter(MouseEvent.MOUSE_DRAGGED, mdrag);
                    });
                    plotter.heightProperty().addListener((ty,ol,ne)->{
                        vr.update();
                    });
                    plotter.widthProperty().addListener((ty,ol,ne)->{
                        vr.update();
                    });
                }else{
                    plotter.removeEventFilter(MouseEvent.MOUSE_PRESSED, mpres);
                    vr.delete();
                }
            });
            // Data end

            this.getMenus().addAll(zoom,value);
        }

    }

    private class ZoomRectangle{
        List<Line> shape;
        SimpleDoubleProperty stX,stY,enX,enY;

        ZoomRectangle(double sX,double sY,double eX,double eY){
            shape=new ArrayList();
            stX=new SimpleDoubleProperty(sX);
            stY=new SimpleDoubleProperty(sY);
            enX=new SimpleDoubleProperty(eX);
            enY=new SimpleDoubleProperty(eY);
            //1
            Line lin=new Line();
            lin.startXProperty().bind(stX);
            lin.startYProperty().bind(stY);
            lin.endXProperty().bind(stX);
            lin.endYProperty().bind(enY);
            lin.setStyle("-fx-stroke-dash-array: 4 4");
            shape.add(lin);
            //2
            lin=new Line();
            lin.startXProperty().bind(stX);
            lin.startYProperty().bind(enY);
            lin.endXProperty().bind(enX);
            lin.endYProperty().bind(enY);
            lin.setStyle("-fx-stroke-dash-array: 4 4");
            shape.add(lin);
            //3
            lin=new Line();
            lin.startXProperty().bind(enX);
            lin.startYProperty().bind(enY);
            lin.endXProperty().bind(enX);
            lin.endYProperty().bind(stY);
            lin.setStyle("-fx-stroke-dash-array: 4 4");
            shape.add(lin);
            //4
            lin=new Line();
            lin.startXProperty().bind(enX);
            lin.startYProperty().bind(stY);
            lin.endXProperty().bind(stX);
            lin.endYProperty().bind(stY);
            lin.setStyle("-fx-stroke-dash-array: 4 4");
            shape.add(lin);
        }

        List<Line> get(){
            return shape;
        }

        double getEndX(){
            return enX.doubleValue();
        }

        double getEndY(){
            return enY.doubleValue();
        }

        void setEnd(double eX,double eY){
            enX.set(eX);
            enY.set(eY);
        }

        void setStart(double sX,double sY){
            stX.set(sX);
            stY.set(sY);
        }
    }

    private class ViewDataEvent extends Event{
        double x,y;


        ViewDataEvent(ValueRepresenter vr) {
            super(AZAZA);


        }


        ViewDataEvent(ValueRepresenter vr,double x,double y) {
            super(AZAZA);
            this.x=x;
            this.y=y;

        }

        double getX(){
            return x;
        }

        double getY(){
            return y;
        }

    }
}


