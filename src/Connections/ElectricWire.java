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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Transform;
import raschetkz.RaschetKz;

/**
 *
 * @author Ivan
 */
public class ElectricWire{
    static final String COLOR="#b87333";
    public static WireMarker activeWireConnect;
    private List<WireMarker> wireContList =new ArrayList<>();
    private List<CrossToCrossLine> ContContList =new ArrayList<>();
    List<List<Cross>> dotList=new ArrayList<>();
    
    public static final EventHandler WC_MOUSE_DRAG = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent me) {
            if(!ElectricWire.activeWireConnect.getIsPlugged().get()){
                ElectricWire.activeWireConnect.setEndProp(me.getSceneX(), me.getSceneY());
            }
            me.consume();
        }
    };
    public static final EventHandler WC_MOUSE_RELEAS= new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent me){
            ElectricWire.activeWireConnect=null;
            ((Node)me.getSource()).removeEventFilter(MouseEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
            ((Node)me.getSource()).removeEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
            me.consume();
        }
    };
    
    public ElectricWire(){
        
    }
    
    public ElectricWire(FileInputStream fis, List<ElemPin> ECList) throws IOException{
        ByteBuffer temp=ByteBuffer.allocate(8);
        //num of SubWires
        fis.read(temp.array(),0,4);
        int subNum=temp.getInt(0);
        //num of CrToCrWires
        fis.read(temp.array(),0,4);
        int CrToCrNum=temp.getInt(0);
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
                    wm.setIsPlugged(false);
                }else{
                    //create cont
                    if(subNum==2){
                        // create second one
                        fis.read(temp.array(),0,4);   //index of EC
                        indx=temp.getInt(0);
                        
                        ElemPin ECofWM1=ECList.get(indx);
//                        ECofWM1.bindWCstartProp(wm);
                        wm.bindStartTo(ECofWM1.getBindX(), ECofWM1.getBindY());
                        
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
//                        ECofWC.bindWCstartProp(wm);
                        wm.bindStartTo(ECofWC.getBindX(), ECofWC.getBindY());
                        break;
                    }
                }
            }else{
                //case of O----O
                wm.getItsLine().getStartX().set(ax);
                wm.getItsLine().getStartY().set(ay);
                wm.getBindX().set(ex);
                wm.getBindY().set(ey);
                wm.setIsPlugged(false);
            }
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
            for(int k=sti-(dotList.size()-1);k>0;k--)
                dotList.add(new ArrayList());
            for(int k=stj-(dotList.get(sti).size()-1);k>0;k--)
                dotList.get(sti).add(null);
            dotList.get(sti).set(stj,line.getStartMarker());
            for(int k=eni-(dotList.size()-1);k>0;k--)
                dotList.add(new ArrayList());
            for(int k=enj-(dotList.get(eni).size()-1);k>0;k--)
                dotList.get(eni).add(null);
            dotList.get(eni).set(enj, line.getEndCrossMarker());
        }
        bindCrosses();
    }
    
    /**
     * Создает провод и цепляет старт к контакту
     * @param EleCont 
     * @param meSceneX 
     * @param meSceneY 
     */
    public ElectricWire(ElemPin EleCont,double meSceneX,double meSceneY){
        WireMarker wc=new WireMarker(this,EleCont);
        wc.setEndProp(meSceneX,meSceneY);
        activeWireConnect=wc;
    }
    
    /**
     * Биндит линию к контакту элемента
     * @param elemCont контакт элемента
     */
    public void setEnd(ElemPin elemCont){
        if(wireContList.size()==1){
            ElemPin oldEc=activeWireConnect.getElemContact();   // начальный O--->
            activeWireConnect.setElemContact(elemCont);           // --->О цепляем
            
            WireMarker wcNew=new WireMarker(this); // ? bind?      // <---O новый
            wcNew.setElemContact(oldEc);
            
//            elemCont.bindWCstartProp(wcNew);                           // OX--->O  цепляем
            wcNew.bindStartTo(elemCont.getBindX(),elemCont.getBindY());
            wcNew.hide();
        }
        else{
            activeWireConnect.setElemContact(elemCont);
        }
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
            if(wc.isPlugged()){             //!!!do somethng with red wires!!!
                redFlag=0;
            }else{
                redFlag=1;
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
            Point2D en=MathPack.MatrixEqu.findFirst(dotList, lin.getEndCrossMarker());
            temp.putDouble(0, lin.getStartX().doubleValue());   //start x
            baos.write(temp.array(), 0, 8);
            temp.putDouble(0, lin.getStartY().doubleValue());   //start y
            baos.write(temp.array(), 0, 8);
            temp.putDouble(0, lin.getEndCrossMarker().getCenterX());   //end x
            baos.write(temp.array(), 0, 8);
            temp.putDouble(0, lin.getEndCrossMarker().getCenterY());   //end y
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
    
    private void addContToCont(WireMarker wc1,WireMarker wc2){
        CrossToCrossLine contToContLine = new CrossToCrossLine(this,wc1,wc2);
        contToContLine.setColor(COLOR);
        contToContLine.activate();
        getContContList().add(contToContLine);
    }
    
    private CrossToCrossLine addContToCont(double sx,double sy,double ex,double ey){
        CrossToCrossLine contToContLine = new CrossToCrossLine(this,sx,sy,ex,ey);
        contToContLine.setColor(COLOR);
        contToContLine.activate();
        getContContList().add(contToContLine);
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
    
    /**
     * Разбиндивает все узлы
     */
    public void unBindAll(){
        for(WireMarker wc:this.wireContList){
            wc.unBindStartPoint();
        }
    }
    
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
            int i=wireContList.size()-1;
            for(;i>=0;i--){
                if(!wireContList.isEmpty())
                    wireContList.get(i).delete();
            }
            activeWireConnect=null;
        }
        for(int i=ContContList.size()-1;i>=0;i--){
            if(!ContContList.isEmpty())
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

        private ElectricWire itsWire;
        private ElemPin itsElemCont;
        //private ReadOnlyObjectProperty<Transform> eleContTransf;


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
                        ElectricWire.activeWireConnect=newCont;
                        adjustCrosses(newCont,
                                itsWire.getWireContacts().get(0),
                                itsWire.getWireContacts().get(1));
                        List<Cross> list=new ArrayList();
                        list.add(newCont.getItsLine().getStartMarker());
                        list.add(itsWire.getWireContacts().get(0).getItsLine().getStartMarker());
                        list.add(itsWire.getWireContacts().get(1).getItsLine().getStartMarker());
                        dotList.add(list);
                             
                        ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
                        ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
                        newCont.startFullDrag();
                        itsWire.getWireContacts().forEach(wc->{
                            wc.show();
                        });
                        me.consume();
                    }
                    else{
                        WireMarker newCont=new WireMarker(itsWire,me.getX(), me.getY());
                        ElectricWire.activeWireConnect=newCont;
                        ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
                        ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
                        newCont.startFullDrag();
                        itsWire.addContToCont(WireMarker.this,newCont);
                    }
                    me.consume();
                }
            });

            //EVENT ZONE
            EventHandler connDragDetectHandle =(EventHandler<MouseEvent>) (MouseEvent me) -> {
                if(me.getButton()==MouseButton.PRIMARY){
                    this.pushToBack();
                    startFullDrag();
                }
                me.consume();
            };            

            EventHandler dragMouseReleas = (EventHandler<MouseEvent>)(MouseEvent me) -> {
                ElectricWire.activeWireConnect=null;
                me.consume();
            };

            EventHandler enterMouse= (EventHandler<MouseEvent>)(MouseEvent me) ->{
                view.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
                view.setCursor(Cursor.HAND);
            };

            EventHandler exitMouse= (EventHandler<MouseEvent>)(MouseEvent me) ->{
                view.setEffect(null);
                view.setCursor(Cursor.DEFAULT);
            };

            view.addEventHandler(MouseEvent.MOUSE_PRESSED, e->{
                ElectricWire.activeWireConnect=this;
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

        WireMarker(ElectricWire thisWire){
            this();
            this.itsWire=thisWire;
            itsWire.getWireContacts().add(this);
            pushToBack();
        }

        /**
         * Creates contact that starts in (x,y) ends in (x,y)
         * @param x
         * @param y 
         */
        WireMarker(ElectricWire thisWire,double sx,double sy){
            this(thisWire);
            itsLines.setStartXY(sx, sy);
            bindX.set(sx);
            bindY.set(sy);
            this.setIsPlugged(false);
        }

        /**
         * Create WireMarker that goes from 'ec'
         * @param thisWire
         * @param ec 
         */
        WireMarker(ElectricWire thisWire,ElemPin ec){
            this(thisWire);
            this.setIsPlugged(false);
//            ec.bindWCstartProp(this);
            bindStartTo(ec.getBindX(),ec.getBindY());
            ec.setWirePointer(this);
            this.itsElemCont=ec;
        }
        
        WireMarker(ElectricWire thisWire,double startX,double startY,double endX,double endY,int numOfLines,boolean isHorizontal,List<Double> constrList){
            this(thisWire);
            itsLines.setStartXY(startX, startY);
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
            if(p!=null&&activeWireConnect==null){
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
                        major.getItsLine().getStartMarker().setVisible(false);
                        dotList.remove(0);
                        break;
                    default: // case of cont to cont
                        List<Cross> line=dotList.get((int)p.getX());
                        int len=line.size(),ind=(int)p.getY();
                        if(len==3){
                            if(line.get((ind+1)%len).getOwner() instanceof CrossToCrossLine && line.get((ind+2)%len).getOwner() instanceof CrossToCrossLine){
                                
                            }else{ // only one crToCrLine's cross
                                CrossToCrossLine loser;
                                ConnectLine master;
                                Cross reducedOne;
                                if(line.get((ind+1)%len).getOwner() instanceof CrossToCrossLine){
                                    loser=(CrossToCrossLine)line.get((ind+1)%len).getOwner();
                                    master=line.get((ind+2)%len).getOwner();
                                }else{
                                    loser=(CrossToCrossLine)line.get((ind+2)%len).getOwner();
                                    master=line.get((ind+1)%len).getOwner();
                                }
                                if(loser.getStartMarker().equals(line.get(ind))){
                                    reducedOne=loser.getEndCrossMarker();
                                }else{
                                    reducedOne=loser.getStartMarker();
                                }
                                Point2D nP=MathPack.MatrixEqu.findFirst(dotList,reducedOne);
                                dotList.get((int)nP.getX()).set((int)nP.getY(),master.getStartMarker());
                                master.getStartMarker().unbind();
                                master.setStartXY(reducedOne.getCenterX(), reducedOne.getCenterY());
                                
                                dotList.remove((int)p.getX());
                                //deleting
                                loser.deleteQuiet();
                                
                            }
                            bindCrosses();
                        }else if(len>3){
                            throw new Error("Size > 3 not supported yet...");
                        }
                       
                }
                
            }
            
            if(this.itsElemCont!=null){
                this.itsElemCont.clearWireContact();
                itsElemCont=null;
            }
            itsWire.getWireContacts().remove(this);
            if(itsWire.getWireContacts().size()<2){
                if(itsWire.getWireContacts().isEmpty()){
                    itsWire.delete();
                }else{
                    WireMarker wm=itsWire.getWireContacts().get(0);
                    if(wm.isPlugged()){
                        itsWire.delete();
                    }
                }
            }
            itsWire=null;
            RaschetKz.drawBoard.getChildren().remove(view);
            unbindEndPoint();
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
//            eleCont.bindWCendProp(this);
            bindEndTo(eleCont.getBindX(), eleCont.getBindY());
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
            setIsPlugged(false);
            unbindEndPoint();
            this.itsElemCont.clearWireContact();
            this.itsElemCont=null;
            switch(this.itsWire.getRank()){
                case 1:
                    WireMarker wc=new WireMarker(itsWire,this.getStartX().get(),this.getStartY().get());
                    ElectricWire.activeWireConnect=wc;
                    wc.getItsLine().bindStart(bindX,bindY);
                    this.getStartX().bind(wc.getBindX());
                    this.getStartY().bind(wc.getBindY());
                    this.hide();
                    break;
                case 2:
                    WireMarker loser;
                    if(this.itsWire.getWireContacts().get(0)==this){
                        loser=this.itsWire.getWireContacts().get(1);
                    }else{
                        loser=this.itsWire.getWireContacts().get(0);
                    }
                    ElectricWire.activeWireConnect=this;
                    ElemPin temp=loser.itsElemCont;
                    loser.delete();
                    temp.setWirePointer(this);
                    this.itsElemCont=temp;
                    this.show();
                    break;
                default:
                    ElectricWire.activeWireConnect=this;
            }
        }

        /**
         * @return the itsBranch
         */
        public ElectricWire getWire() {
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
        private Cross endCrossMarker;
        private ElectricWire owner;
        /**
         * 
         * @param wc1
         * @param wc2 new one
         */
        CrossToCrossLine(ElectricWire owner,WireMarker wc1,WireMarker wc2){
            super();
            
            this.getLines().forEach(extLine->{
                //extLine.setOnKeyReleased(null);
                extLine.setOnKeyReleased(k->{
                    if(k.getCode()==KeyCode.DELETE){
                        this.delete();
                    }
                });
            });
            
            this.owner=owner;
            //this.getStartMarker().setVisible(true);
            this.setStartXY(wc1.getStartX().get(), wc1.getStartY().get());
            endCrossMarker=new Cross(this,wc2.bindX.get(),wc2.bindY.get());
            //endCrossMarker.setVisible(true);
            this.getEndX().bind(endCrossMarker.centerXProperty());
            this.getEndY().bind(endCrossMarker.centerYProperty());
            endCrossMarker.centerXProperty().addListener(super.getPropListen());
            endCrossMarker.centerYProperty().addListener(super.getPropListen());
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
            setLineDragDetect((EventHandler<MouseEvent>)(MouseEvent me)->{
                if(me.getButton().equals(MouseButton.SECONDARY)){
                    double x=me.getX(),y=me.getY();
                    CrossToCrossLine newOne=owner.addContToCont(x,y,
                            this.getEndCrossMarker().getCenterX(),this.getEndCrossMarker().getCenterY());
                    this.getEndCrossMarker().unbind();
                    this.setEndXY(x, y);
                    //create new WireMarker
                    WireMarker wm=new WireMarker(owner,x,y);
                    activeWireConnect=wm;
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
                    wm.startFullDrag();
                    // dotList manipulation
                    int len=dotList.size();
                    List<Cross> line=new ArrayList();
                    line.add(wm.getItsLine().getStartMarker());
                    line.add(this.getEndCrossMarker());
                    line.add(newOne.getStartMarker());
                        //replace old crTcr end to new end
                    Point2D p=MathPack.MatrixEqu.findFirst(dotList, this.getEndCrossMarker());
                    dotList.get((int)p.getX()).set((int)p.getY(), newOne.getEndCrossMarker());
                    dotList.add(line);
                    
                    owner.bindCrosses();
                }
            });
        }
        
        /**
         * Only creates line. Handle dotList by yourself
         * @param sx
         * @param sy
         * @param ex
         * @param ey 
         */
        CrossToCrossLine(ElectricWire owner,double sx,double sy,double ex,double ey){
            super();
            
            this.getLines().forEach(extLine->{
                //extLine.setOnKeyReleased(null);
                extLine.setOnKeyReleased(k->{
                    if(k.getCode()==KeyCode.DELETE){
                        this.delete();
                    }
                });
            });
            
            this.owner=owner;
            this.setStartXY(sx, sy);
            endCrossMarker=new Cross(this,ex,ey);
            this.getEndX().bind(endCrossMarker.centerXProperty());
            this.getEndY().bind(endCrossMarker.centerYProperty());
            endCrossMarker.centerXProperty().addListener(super.getPropListen());
            endCrossMarker.centerYProperty().addListener(super.getPropListen());
            
            setLineDragDetect((EventHandler<MouseEvent>)(MouseEvent me)->{
                if(me.getButton().equals(MouseButton.SECONDARY)){
                    double x=me.getX(),y=me.getY();
                    CrossToCrossLine newOne=owner.addContToCont(x,y,
                            this.getEndCrossMarker().getCenterX(),this.getEndCrossMarker().getCenterY());
                    this.getEndCrossMarker().unbind();
                    this.setEndXY(x, y);
                    //create new WireMarker
                    WireMarker wm=new WireMarker(owner,x,y);
                    activeWireConnect=wm;
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
                    wm.startFullDrag();
                    // dotList manipulation
                    int len=dotList.size();
                    List<Cross> line=new ArrayList();
                    line.add(wm.getItsLine().getStartMarker());
                    line.add(this.getEndCrossMarker());
                    line.add(newOne.getStartMarker());
                        //replace old crTcr end to new end
                    Point2D p=MathPack.MatrixEqu.findFirst(dotList, this.getEndCrossMarker());
                    dotList.get((int)p.getX()).set((int)p.getY(), newOne.getEndCrossMarker());
                    dotList.add(line);
                    
                    owner.bindCrosses();
                }
            });
        }

        @Override
        public void delete(){
            //implement
            deleteQuiet();
            owner.delete();
        }
        
        void deleteQuiet(){
            super.delete();
            for(List<Cross> row:dotList){
                row.remove(this.getStartMarker());
                row.remove(this.getEndCrossMarker());
            }
            owner.getContContList().remove(this);
            endCrossMarker.delete();
        }

        @Override
        public void setColor(String rgb){
            super.setColor(rgb);
            getEndCrossMarker().setFill(Paint.valueOf(rgb));
        }

        /**
         * @return the endCrossMarker
         */
        Cross getEndCrossMarker() {
            return endCrossMarker;
        }
        
        final void setEndXY(double x,double y){
            getEndCrossMarker().setCenterX(x);
            getEndCrossMarker().setCenterY(y);
        }
    }
}