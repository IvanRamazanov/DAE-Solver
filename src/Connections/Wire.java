/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connections;

import java.util.ArrayList;
import java.util.List;
import ElementBase.ElemPin;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Transform;
import raschetkz.RaschetKz;


/**
 *
 * @author Ivan
 */
public class Wire{
    static final String COLOR="#b87333";
    public static WireMarker activeWireConnect;
    private List<WireMarker> wireContList =new ArrayList<>();
    private List<CrossToCrossLine> ContContList =new ArrayList<>();
    List<List<Cross>> dotList=new ArrayList<>();
    
    public static final EventHandler WC_MOUSE_DRAG = (EventHandler<MouseEvent>) new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent me) {
            if(!Wire.activeWireConnect.getIsPlugged().get()){
                Wire.activeWireConnect.setEndProp(me.getSceneX(), me.getSceneY());
//                centerX.set(me.getX());
            }
            me.consume();
        }
    };
    public static final EventHandler WC_MOUSE_RELEAS= (EventHandler<MouseEvent>) new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent me) {
            //Wire.activeWireConnect.toFront();
            Wire.activeWireConnect=null;
            ((Node)me.getSource()).removeEventFilter(MouseEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
            ((Node)me.getSource()).removeEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
            me.consume();
        }
    };
    
    public Wire(){
        
    }
    
    public Wire(FileInputStream fis, List<ElemPin> ECList) throws IOException{
        ByteBuffer temp=ByteBuffer.allocate(8);
        //num of SubWires
        fis.read(temp.array(),0,4);
        int subNum=temp.getInt(0);
        //num of CrToCrWires
        fis.read(temp.array(),0,4);
        int CrToCrNum=temp.getInt(0);
//                    Wire w=new Wire();
        for(int j=0;j<subNum;j++){ //num of subMarkers
            //index of EC
            fis.read(temp.array(),0,4);
            int indx=temp.getInt(0);
            //anchor's x
            fis.read(temp.array(),0,8);   
            double ax=temp.getDouble(0);
            //anchor's y
            fis.read(temp.array(),0,8);   
            double ay=temp.getDouble(0);
            //end's x
            fis.read(temp.array(),0,8);
            double ex=temp.getDouble(0);
            //end's y
            fis.read(temp.array(),0,8);
            double ey=temp.getDouble(0);
            //redflag
            fis.read(temp.array(),0,2);
            short redFlag=temp.getShort(0);
            // read point in dotList
            
            WireMarker wm;
                //easyDraw ?
            fis.read(temp.array(),0,4);
            boolean easyDraw = temp.getInt(0)==1;
            
            if(!easyDraw){
                //read num of lines
                fis.read(temp.array(),0,4);
                int numOfLines=temp.getInt(0);
                fis.read(temp.array(),0,4);
                boolean isHorizontal=temp.getInt(0)==1;
                List<Double> constraints=new ArrayList();
                for(int i=1;i<numOfLines-1;i++){
                    fis.read(temp.array(),0,8);
                    constraints.add(temp.getDouble(0));
                }
                wm=new WireMarker(this,ax,ay,ex,ey,numOfLines,isHorizontal,constraints);
            }else{
                wm=new WireMarker(this);
            }
            wm.setEndProp(ex, ey);
            wm.setStartPoint(ax, ay);
            if(subNum>2){  //then exist Cross in dotList
                fis.read(temp.array(),0,4);
                int ii=temp.getInt(0);
                fis.read(temp.array(),0,4);
                int jj=temp.getInt(0);
                for(int k=ii-(dotList.size()-1);k>0;k--)
                    dotList.add(new ArrayList());
                for(int k=jj-(dotList.get(ii).size()-1);k>0;k--)
                    dotList.get(ii).add(null);
                dotList.get(ii).set(jj,wm.getItsLine().getStartMarker());
            }

            //set up wireCont
            if(indx!=-1){
                ElemPin ECofWC=ECList.get(indx);
                wm.setElemContact(ECofWC);
                ECofWC.setWirePointer(wm);
                if(redFlag==1){ //?????????? must be no EC
                    //wm.setElemContact(ECofWC);
                    
                    wm.setIsPlugged(false);
//                    wm.getBindX().set(ex);
//                    wm.getBindY().set(ey);
                }else{
                    //create cont

                    if(subNum==2){
                        
                        // create second one
                        fis.read(temp.array(),0,4);   //index of EC
                        indx=temp.getInt(0);
                        
                        ElemPin ECofWM1=ECList.get(indx);
                        ECofWM1.bindWCstartProp(wm);
                        
                        fis.read(temp.array(),0,8);   //anchor's x
                        ax=temp.getDouble(0);
                        fis.read(temp.array(),0,8);   //anchor's y
                        ay=temp.getDouble(0);
                        fis.read(temp.array(),0,8);   //end's x
                        fis.read(temp.array(),0,8);   //end's y
                        fis.read(temp.array(),0,2);     //redflag
                        fis.read(temp.array(),0,4);
                        easyDraw = temp.getInt(0)==1;
                        if(!easyDraw){
                            //read num of lines
                            fis.read(temp.array(),0,4);
                            int numOfLines=temp.getInt(0);
                            fis.read(temp.array(),0,4);
                            boolean isHorizontal=temp.getInt(0)==1;
                            List<Double> constraints=new ArrayList();
                            for(int i=1;i<numOfLines-1;i++){
                                fis.read(temp.array(),0,8);
                                constraints.add(temp.getDouble(0));
                            }
                            wm=new WireMarker(this,ax,ay,ex,ey,numOfLines,isHorizontal,constraints);
                        }else{
                            wm=new WireMarker(this);
                        }
                        wm.hide();
                        wm.setElemContact(ECofWM1);
                        ECofWM1.setWirePointer(wm);
                        ECofWC.bindWCstartProp(wm);
                        break;
                    }
//                    else{// bound vals cannot be set !!!!!!!!!!!!?????????
//                        wc=addContact(ECofWC,ax,ay);
////                                    wc.setStartXProp(ax);
////                                    wc.setStartYProp(ay);
//                        wc.setIsPlugged(true);
//                    }
                }

            }else{
                //case of O----O
                wm.getItsLine().getStartX().set(ax);
                wm.getItsLine().getStartY().set(ay);
                wm.getBindX().set(ex);
                wm.getBindY().set(ey);
                wm.setIsPlugged(false);
            }
            // drawing
            //wc.getItsLine().setEasyDraw(easyDraw);
        }

        for(int j=0;j<CrToCrNum;j++){
            fis.read(temp.array(),0,8);   //anchor's x
            double ax=temp.getDouble(0);
            fis.read(temp.array(),0,8);   //anchor's y
            double ay=temp.getDouble(0);
            fis.read(temp.array(),0,8);   //end's x
            double ex=temp.getDouble(0);
            fis.read(temp.array(),0,8);   //end's y
            double ey=temp.getDouble(0);
            fis.read(temp.array(),0,4);
            boolean easyDraw=temp.getInt(0)==1;
            CrossToCrossLine line=addContToCont(ax,ay,ex,ey);
            if(!easyDraw){
                fis.read(temp.array(),0,4);
                int numOfLines=temp.getInt(0);
                fis.read(temp.array(),0,4);
                boolean isHrizon=temp.getInt(0)==1;
                List<Double> constraints=new ArrayList();
                for(int i=1;i<numOfLines-1;i++){
                    fis.read(temp.array(),0,8);
                    constraints.add(temp.getDouble(0));
                }
                line.rearrange(numOfLines, isHrizon, constraints);
            }
            // indexes in dotList
            fis.read(temp.array(),0,4);
            int sti=temp.getInt(0);
            fis.read(temp.array(),0,4);
            int stj=temp.getInt(0);
            fis.read(temp.array(),0,4);
            int eni=temp.getInt(0);
            fis.read(temp.array(),0,4);
            int enj=temp.getInt(0);
            // add Crosses in dotList
            for(int k=sti-dotList.size()-1;k==0;k++)
                dotList.add(new ArrayList());
            for(int k=stj-dotList.get(sti).size()-1;k==0;k++)
                dotList.add(new ArrayList());
            dotList.get(sti).set(stj,line.getStartMarker());
            for(int k=eni-dotList.size()-1;k==0;k++)
                dotList.add(new ArrayList());
            for(int k=enj-dotList.get(eni).size()-1;k==0;k++)
                dotList.add(new ArrayList());
            dotList.get(eni).set(enj,line.endCrossMarker);
        }
        bindCrosses();
    }
    
//    public Wire(ElemPin conn1, ElemPin conn2){
//        wireContList.add(new WireMarker(this,conn1));
//        wireContList.add(new WireMarker(this,conn2));
//    }
    
    /**
     * Создает провод и цепляет старт к контакту
     * @param EleCont 
     * @param meSceneX 
     * @param meSceneY 
     */
    public Wire(ElemPin EleCont,double meSceneX,double meSceneY){
        WireMarker wc=new WireMarker(this,EleCont);
        wc.setEndProp(meSceneX,meSceneY);
        //wireContList.add(wc);
        activeWireConnect=wc;
    }
    
    /**
     * Биндит линию к контакту элемента
     * @param elemCont контакт элемента
     */
    public void setEnd(ElemPin elemCont){
        if(wireContList.size()==1){
            ElemPin oldEc=activeWireConnect.getElemContact();   // начальный O--->
            //elemCont.bindWCendProp(activeWireConnect);                 // --->О цепляем
            //elemCont.setPointersBidirect(activeWireConnect);
            activeWireConnect.setElemContact(elemCont);
            
            WireMarker wcNew=new WireMarker(this); // ? bind?      // <---O новый
            wcNew.setElemContact(oldEc);
            
            elemCont.bindWCstartProp(wcNew);                           // OX--->O  цепляем
            //oldEc.bindWCendProp(wcNew);
            //wireContList.add(wcNew);
            //wcNew.isPlugged.set(true);
            wcNew.hide();
        }
        else{
            activeWireConnect.setElemContact(elemCont);
//            elemCont.setWirePointer(activeWireConnect);
//            elemCont.bindWCendProp(activeWireConnect); 
        }
        //elemCont.setOpacity(1);
           //activeWireConnect.getIsPlugged().set(true);
        //activeWireContact=null;
    }
    
    /**
     * Возвращяет число контактов
     * @return 
     */    
    public int getRank(){
        return(this.wireContList.size());
    }
    
    public List<WireMarker> getWireContacts(){
        return(wireContList);
    };
    
    public WireMarker addContact(){
        WireMarker wc=new WireMarker(this);
        //this.wireContList.add(wc);
        return wc;
    }
    
    public void Save(ByteArrayOutputStream baos,List<ElemPin> ECList){
        ByteBuffer temp=ByteBuffer.allocate(8);
        double startPx,startPy,endPx,endPy;
        //num of subwires
        temp.putInt(0, getWireContacts().size());
        baos.write(temp.array(), 0, 4);

        //num of CrToCr
        temp.putInt(0, getContContList().size());
        baos.write(temp.array(), 0, 4);
        
        // describe each WireMarker
        for(WireMarker wc:getWireContacts()){
            int indx;
            short redFlag;
            ElemPin ec=wc.getElemContact();
            indx=ECList.indexOf(ec);
            startPx=wc.getStartX().doubleValue();
            startPy=wc.getStartY().doubleValue();
            endPx=wc.getBindX().doubleValue();
            endPy=wc.getBindY().doubleValue();
            if(wc.isFine()){             //!!!do somethng with red wires!!!
                redFlag=1;
            }else{
                redFlag=0;
            }

            //write
            temp.putInt(0, indx);   //index of ElemConts
            baos.write(temp.array(), 0, 4);
            temp.putDouble(0,startPx);
            baos.write(temp.array(), 0, 8);
            temp.putDouble(0,startPy);
            baos.write(temp.array(), 0, 8);
            temp.putDouble(0,endPx);
            baos.write(temp.array(), 0, 8);
            temp.putDouble(0,endPy);
            baos.write(temp.array(), 0, 8);
            temp.putShort(0, redFlag);   //detect if line is red
            baos.write(temp.array(), 0, 2);
            
            
            
            //easyDraw ?
            if(wc.getItsLine().isEasyDraw()){
                temp.putInt(0, 1);
                baos.write(temp.array(), 0, 4);
            }else{
                temp.putInt(0, 0);
                baos.write(temp.array(), 0, 4);
                //get num of lines
                temp.putInt(0,wc.getItsLine().getLines().size());
                baos.write(temp.array(), 0, 4);
                //is the first line horizontal
                if(wc.getItsLine().getLines().get(0).isHorizontal()){
                    temp.putInt(0,1);
                    baos.write(temp.array(), 0, 4);
                }else{
                    temp.putInt(0,0);
                    baos.write(temp.array(), 0, 4);
                }
                // array of constraints
                List<Double> constraints=wc.getItsLine().parseLines();
                for(Double d:constraints){
                    temp.putDouble(0, d);
                    baos.write(temp.array(), 0, 8);
                }
            }
            // indexes of Crosses (May be corruptions due to dotList)
            Point2D p=MathPack.MatrixEqu.findFirst(dotList, wc.getItsLine().getStartMarker());
            if(p!=null){
                int tmp=(int)p.getX();
                temp.putInt(0,tmp);
                baos.write(temp.array(), 0, 4);
                tmp=(int)p.getY();
                temp.putInt(0,tmp);
                baos.write(temp.array(), 0, 4);
            }
            
        }
        //Cross to Cross
        for(CrossToCrossLine lin:getContContList()){
            Point2D st=MathPack.MatrixEqu.findFirst(dotList, lin.getStartMarker());
            Point2D en=MathPack.MatrixEqu.findFirst(dotList, lin.endCrossMarker);
            temp.putDouble(0, lin.getStartX().doubleValue());   //start x
            baos.write(temp.array(), 0, 8);
            temp.putDouble(0, lin.getStartY().doubleValue());   //start y
            baos.write(temp.array(), 0, 8);
            temp.putDouble(0, lin.endCrossMarker.getCenterX());   //end x
            baos.write(temp.array(), 0, 8);
            temp.putDouble(0, lin.endCrossMarker.getCenterY());   //end y
            baos.write(temp.array(), 0, 8);
            
            //easyDraw ?
            if(lin.isEasyDraw()){
                temp.putInt(0, 1);
                baos.write(temp.array(), 0, 4);
            }else{
                temp.putInt(0, 0);
                baos.write(temp.array(), 0, 4);
                //get num of lines
                temp.putInt(0,lin.getLines().size());
                baos.write(temp.array(), 0, 4);
                //is the first line horizontal
                if(lin.getLines().get(0).isHorizontal()){
                    temp.putInt(0,1);
                    baos.write(temp.array(), 0, 4);
                }else{
                    temp.putInt(0,0);
                    baos.write(temp.array(), 0, 4);
                }
                // array of constraints
                List<Double> constraints=lin.parseLines();
                for(Double d:constraints){
                    temp.putDouble(0, d);
                    baos.write(temp.array(), 0, 8);
                }
            }
            // indexes of crosses
            int tmp=(int)st.getX();
            temp.putInt(0,tmp);
            baos.write(temp.array(), 0, 4);
            tmp=(int)st.getY();
            temp.putInt(0,tmp);
            baos.write(temp.array(), 0, 4);
            tmp=(int)en.getX();
            temp.putInt(0,tmp);
            baos.write(temp.array(), 0, 4);
            tmp=(int)en.getY();
            temp.putInt(0,tmp);
            baos.write(temp.array(), 0, 4);
        }
        
    }
    
    public void addContToCont(WireMarker wc1,WireMarker wc2){
        CrossToCrossLine contToContLine = new CrossToCrossLine(wc1,wc2);
        contToContLine.setColor(COLOR);
        contToContLine.activate();
        
        
        
        getContContList().add(contToContLine);
//        getCrosses().add(contToContLine.getEndCr());
//        getCrosses().add(contToContLine.getStartCr());
    }
    
    public CrossToCrossLine addContToCont(double sx,double sy,double ex,double ey){
        CrossToCrossLine contToContLine = new CrossToCrossLine(sx,sy,ex,ey);
        contToContLine.setColor(COLOR);
        contToContLine.activate();
        getContContList().add(contToContLine);
//        getCrosses().add(contToContLine.getEndCr());
//        getCrosses().add(contToContLine.getStartCr());
        return contToContLine;
    }
    
    /**
     * Creates WireConnect and bind pointers. Bind start?
     * @param elemCont
     * @return 
     */
    public WireMarker addContact(ElemPin elemCont){//boolean endbind???
        WireMarker wc=new WireMarker(this,elemCont);
        this.wireContList.add(wc);
        return wc;
    }
    
//    public WireMarker addContact(ElemPin elemCont,double startX,double startY){//boolean endbind???
//        WireMarker wc=new WireMarker(this);
//        this.wireContList.add(wc);
//        elemCont.bindWCendProp(wc);
//        elemCont.setPointersBidirect(wc);
//        wc.setStartPoint(startX, startY);
//        return wc;
//    }
    
    /**
     * Разбиндивает все узлы
     */
    public void unBindAll(){
        for(WireMarker wc:this.wireContList){
            wc.unBindStartPoint();
        }
    }
    
//    public void adjustCrosses(List<Cross> inp){
//            inp.get(0).setVisible(true);
//            inp.get(0).toFront();
//            for(int i=1;i<inp.size();i++){
//                inp.get(i).setVisible(false);
//                inp.get(i).centerXProperty().unbind();
//                inp.get(i).centerYProperty().unbind();
//                inp.get(i).centerXProperty().bind(inp.get(0).centerXProperty());
//                inp.get(i).centerYProperty().bind(inp.get(0).centerYProperty());
//            }
//        }
    
//    public void reBindContacts(){
//        List<Point2D> points=new ArrayList();
//        //list of all crosses
//        for(Cross anch:this.getCrosses()){
//            points.add(new Point2D(anch.getCenterX(),anch.getCenterY()));
//        }
//        for(int i=0;i<points.size();i++){
//            //test for equals anchors
//            Point2D majorPoint=points.get(i);
//            int ind;
//            boolean a=false;
//            if(majorPoint!=null){
//                List<Cross> wcs=new ArrayList();
//                while((ind=points.lastIndexOf(majorPoint))!=-1){
//                    if(i!=ind){
//                        wcs.add(this.getCrosses().get(ind));
//                        points.set(ind, null);
//                        a=true;
//                    }else{
//                        if(a)
//                            wcs.add(this.getCrosses().get(ind));
//                        points.set(ind, null);
//                    }                                    
//                }
//                if(!wcs.isEmpty())
//                    adjustCrosses(wcs);
//            }
//        }
//    }
    
    /**
     * Binds all crosses in dotList to first Cross in each line
     */
    private void bindCrosses(){
        for(List<Cross> line:dotList){
            Cross major=line.get(0);
            major.setVisible(true);
            for(int i=1;i<line.size();i++){
                line.get(i).bindToCross(major);
            }
        }
    }

    public void delete(){
        raschetkz.RaschetKz.BranchList.remove(this);
        if(!wireContList.isEmpty()){
            activeWireConnect=wireContList.get(0);// for prevent dead loop
            for(int i=wireContList.size()-1;i>=0;i--){
                wireContList.get(i).delete();
            }
            activeWireConnect=null;
        }
        for(int i=ContContList.size()-1;i>=0;i--){
            ContContList.get(i).delete();
        }
        
    }
    
    /**
     * @return the ContContList
     */
    private List<CrossToCrossLine> getContContList() {
        return ContContList;
    }
    
    public class WireMarker extends LineMarker{

        private Wire itsWire;
        private ElemPin itsElemCont;
        private ReadOnlyObjectProperty<Transform> eleContTransf;


        WireMarker(){
            super();
            view=new Circle();
            view.layoutXProperty().bind(bindX);
            view.layoutYProperty().bind(bindY);
            ((Circle)view).setRadius(4);
            itsLines.setColor(COLOR);
            itsLines.setLineDragDetect((EventHandler<MouseEvent>)(MouseEvent me)->{
                if(me.getButton()==MouseButton.SECONDARY){
                    if(itsWire.getWireContacts().size()==1){
                        me.consume();
                        return;
                    }
                    if(itsWire.getWireContacts().size()==2){
                        WireMarker newCont=new WireMarker(itsWire,me.getX(), me.getY());
                        Wire.activeWireConnect=newCont;
                        adjustCrosses(newCont,
                                itsWire.getWireContacts().get(0),
                                itsWire.getWireContacts().get(1));
                        List<Cross> list=new ArrayList();
                        list.add(newCont.getItsLine().getStartMarker());
                        list.add(itsWire.getWireContacts().get(0).getItsLine().getStartMarker());
                        list.add(itsWire.getWireContacts().get(1).getItsLine().getStartMarker());
                        dotList.add(list);
                        
                        
                        ((Line)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
                        ((Line)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
                        newCont.view.startFullDrag();
                        itsWire.getWireContacts().forEach(wc->{
                            wc.show();
                        });
                        me.consume();
                    }
                    else{
    //                    itsWire.getWireContacts().forEach(wc->{
    //                        wc.show();
    //                    });
                        WireMarker newCont=new WireMarker(itsWire,me.getX(), me.getY());
                        Wire.activeWireConnect=newCont;
                        ((Line)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
                        ((Line)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
                        newCont.view.startFullDrag();
                        itsWire.addContToCont(WireMarker.this,newCont);
    //                    itsWire.reBindContacts()
                    }
                    me.consume();
                }
            });

            //((Circle)view).setOpacity(0); //Or opacity???????????
            itsLines.getEndX().bind(bindX);
            itsLines.getEndY().bind(bindY);

            //EVENT ZONE
            EventHandler connDragDetectHandle = (EventHandler<MouseEvent>) (MouseEvent me) -> {
                if(me.getButton()==MouseButton.PRIMARY){
                    this.pushToBack();
                    view.startFullDrag();
                }
                me.consume();
            };            

            EventHandler dragMouseReleas = (EventHandler<MouseEvent>) (MouseEvent me) -> {
                Wire.activeWireConnect=null;
                me.consume();
            };

            EventHandler enterMouse= (EventHandler<MouseEvent>) (MouseEvent me) ->{
                view.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
                view.setCursor(Cursor.HAND);
            };

            EventHandler exitMouse= (EventHandler<MouseEvent>) (MouseEvent me) ->{
                view.setEffect(null);
                view.setCursor(Cursor.DEFAULT);
            };

            view.addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
                Wire.activeWireConnect=this;
                view.toFront();
                e.consume();
            });
            view.addEventHandler(MouseDragEvent.DRAG_DETECTED, connDragDetectHandle);
            view.addEventHandler(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
            view.addEventHandler(MouseDragEvent.MOUSE_RELEASED, dragMouseReleas);
            view.addEventHandler(MouseEvent.MOUSE_ENTERED, enterMouse);
            view.addEventHandler(MouseEvent.MOUSE_EXITED, exitMouse);
            //-------------

            raschetkz.RaschetKz.drawBoard.getChildren().add(view);
        }

        WireMarker(Wire thisWire){
            this();
            this.itsWire=thisWire;
            itsWire.getWireContacts().add(this);
            //itsLines=new ConnectLine(this,0,0);
    //        thisWire.getCrosses().add(itsLines.getStartPoint());
            pushToBack();
        }

        /**
         * Creates contact that starts in (x,y) ends in (x,y)
         * @param x
         * @param y 
         */
        WireMarker(Wire thisWire,double sx,double sy){
            this(thisWire);
            //itsLines=new ConnectLine(this,x,y);
            itsLines.setCrossMarkerXY(sx, sy);
            bindX.set(sx);
            bindY.set(sy);
    //        thisWire.getCrosses().add(itsLines.getStartPoint());
            this.setIsPlugged(false);
        }

        /**
         * Create WireMarker that goes from 'ec'
         * @param thisWire
         * @param ec 
         */
        WireMarker(Wire thisWire,ElemPin ec){
            this(thisWire);
            //itsLines=new ConnectLine(this,ec.localToScene(ec.getCenterX(), ec.getCenterY()));
    //        thisWire.getCrosses().add(itsLines.getStartPoint());
            this.setIsPlugged(false);
            ec.bindWCstartProp(this);
            //ec.setPointersBidirect(this);
            ec.setWirePointer(this);
            this.itsElemCont=ec;
        }
        
        WireMarker(Wire thisWire,double startX,double startY,double endX,double endY,int numOfLines,boolean isHorizontal,List<Double> constrList){
            this(thisWire);
            itsLines.setCrossMarkerXY(startX, startY);
            bindX.set(endX);
            bindY.set(endY);
            itsLines.rearrange(numOfLines,isHorizontal,constrList);
            
        }

        /**
         * Удаление контакта и линии
         */
        @Override
        void delete(){
            //reduce dotList
            Point2D p=MathPack.MatrixEqu.findFirst(dotList, this.getItsLine().getStartMarker());
            if(p!=null){
                switch(dotList.size()){
                    case 1: //triple line Wire
                        dotList.get(0).remove((int)p.getY());
                        LineMarker major=dotList.get(0).get(0).getOwner().marker;
                        LineMarker minor=dotList.get(0).get(1).getOwner().marker;
                        major.getItsLine().getStartX().bind(minor.getBindX());
                        major.getItsLine().getStartY().bind(minor.getBindY());
                        minor.getItsLine().getStartX().bind(major.getBindX());
                        minor.getItsLine().getStartY().bind(major.getBindY());
                        minor.hide();
                        dotList.remove(0);
                        break;
                    default: // case of cont to cont
                        
                }
                
            }
            
    //        if(Wire.activeWireConnect!=null){
                if(this.itsElemCont!=null){
                    this.itsElemCont.clearWireContact();
                    itsElemCont=null;
                }
    //            itsWire.getCrosses().remove(itsLines.getStartPoint());
                itsWire.getWireContacts().remove(this);
                if(itsWire.getWireContacts().isEmpty())
                    itsWire.delete();
                itsWire=null;
                RaschetKz.drawBoard.getChildren().remove(view);
                unBindEndPoint();
                unBindStartPoint();
                itsLines.delete();
                itsLines=null;

        }

        public ElemPin getElemContact(){
            return(this.itsElemCont);
        }
        
        /**
         * 
         * @param eleCont 
         */
        public void setElemContact(ElemPin eleCont){
            this.itsElemCont=eleCont;
            eleCont.bindWCendProp(this);
            eleCont.setWirePointer(this);
            this.setIsPlugged(true);
        }

        

        public void show(){
            this.itsLines.show();
        }

        /**
         * push to front of view
         */
        public void toFront(){
            this.view.toFront();
        }

        /**
         * Unplug (usually active one) wire contact. If Rank==2 delete second WireCont.
         */
        public void unPlug(){
            this.isPlugged.set(false);
            this.bindX.unbind();
            this.bindY.unbind();
            this.itsElemCont.clearWireContact();
            this.itsElemCont=null;
            switch(this.itsWire.getRank()){
                case 1:
                    WireMarker wc=new WireMarker(itsWire,this.getStartX().get(),this.getStartY().get());
                    Wire.activeWireConnect=wc;
                    wc.getItsLine().bindStart(bindX,bindY);
                    this.getStartX().bind(wc.getBindX());
                    this.getStartY().bind(wc.getBindY());
    //                itsLines.bindCross(wc.getCenterX(), wc.getCenterY());
    //                this.itsElemCont=null;
                    this.hide();
                    break;
                case 2:
                    WireMarker loser;
                    if(this.itsWire.getWireContacts().get(0)==this){
                        loser=this.itsWire.getWireContacts().get(1);
                    }else{
                        loser=this.itsWire.getWireContacts().get(0);
                    }
                    Wire.activeWireConnect=this;
                    ElemPin temp=loser.itsElemCont;
    //                this.itsElemCont.clearWireContact();
                    loser.delete();
                    temp.setWirePointer(this);
                    this.itsElemCont=temp;
                    this.show();
                    break;
                default:
                    Wire.activeWireConnect=this;
            }
    //        this.show();
        }

        /**
         * @return the itsBranch
         */
        public Wire getWire() {
            return itsWire;
        }

        public DoubleProperty getStartX(){
            return this.itsLines.getStartX();
        }

        public DoubleProperty getStartY(){
            return this.itsLines.getStartY();
        }
    }
    
    private class CrossToCrossLine extends ConnectLine{
        Cross endCrossMarker;

        /**
         * 
         * @param wc1
         * @param wc2 new one
         */
        CrossToCrossLine(WireMarker wc1,WireMarker wc2){
            super();
            this.getStartMarker().setVisible(true);
            this.setCrossMarkerXY(wc1.getStartX().get(), wc1.getStartY().get());
            endCrossMarker=new Cross(this,wc2.bindX.get(),wc2.bindY.get());
            endCrossMarker.setVisible(true);
            this.getEndX().bind(endCrossMarker.centerXProperty());
            this.getEndY().bind(endCrossMarker.centerYProperty());
            endCrossMarker.centerXProperty().addListener(super.getPropListen());
            endCrossMarker.centerYProperty().addListener(super.getPropListen());
              // list of (in general two) another crosses 
//            if(wc1.getItsLine().getStartMarker().isMajor()){
//                major=wc1.getItsLine().getStartMarker().getBindMinors();
//            }else{
//                major=wc1.getItsLine().getStartMarker().getBindSource().getBindMinors();
//                wc1.getItsLine().getStartMarker().getBindSource().bindToCross(getStartMarker()); //make wc1 is a minor
//            }

            //find in dotList line with 'major' list
            for(List<Cross> line:dotList){
                if(line.contains(wc1.getItsLine().getStartMarker())){ // set it's start cross as major
                    line.remove(line.indexOf(wc1.getItsLine().getStartMarker()));    //remove wc1 from list
                    line.add(0, getStartMarker()); // set new major cross - CrToCr start one
                    // add new line
                    List<Cross> nLine=new ArrayList();
                    nLine.add(endCrossMarker);
                    nLine.add(wc1.getItsLine().getStartMarker());
                    nLine.add(wc2.getItsLine().getStartMarker());
                    dotList.add(nLine);
                    bindCrosses();
                    break;
                }
            }
        }
        
        /**
         * Only creates line. Handle dotList by yourself
         * @param sx
         * @param sy
         * @param ex
         * @param ey 
         */
        CrossToCrossLine(double sx,double sy,double ex,double ey){
            super();
            this.setCrossMarkerXY(sx, sy);
            endCrossMarker=new Cross(this,ex,ey);
            this.getEndX().bind(endCrossMarker.centerXProperty());
            this.getEndY().bind(endCrossMarker.centerYProperty());
            endCrossMarker.centerXProperty().addListener(super.getPropListen());
            endCrossMarker.centerYProperty().addListener(super.getPropListen());
            
    //        endCr=new Cross(ex,ey);
    //        super.getStartPoint().setCenterX(sx);
    //        super.getStartPoint().setCenterY(sy);
    //        endCr.setFill(Paint.valueOf("#b87333"));
    //        super.getLines().get(2).endXProperty().bind(endCr.centerXProperty());
    //        super.getLines().get(2).endYProperty().bind(endCr.centerYProperty());
    //        endCr.centerXProperty().addListener(super.getPropListen());
    //        endCr.centerYProperty().addListener(super.getPropListen());
    //        raschetkz.RaschetKz.drawBoard.getChildren().addAll(endCr);
        }

        @Override
        public void delete(){
            //implement
        }

        @Override
        public void setColor(String rgb){
            super.setColor(rgb);
            endCrossMarker.setFill(Paint.valueOf(rgb));
        }
    }
}