/*
 * The MIT License
 *
 * Copyright 2018 Ivan.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package Connections;

import ElementBase.MathInPin;
import ElementBase.MathOutPin;
import ElementBase.MathPin;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 *
 * @author Ivan
 */
public class MathWire extends Wire{
    static final String COLOR="#000000";
    public static MathMarker activeMathMarker;
    private List<MathMarker> mathMarkList =new ArrayList<>();
    private MathMarker sourceMarker;
    private List<CrossToCrossLine> ContContList =new ArrayList<>();
    List<List<Cross>> dotList=new ArrayList<>();

    private static Shape dragSource;
    //private static MathMarker majorConnect;
    private MathOutPin source;

    public static final EventHandler MC_MOUSE_DRAG = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent me) {
            if(!activeMathMarker.getIsPlugged().get()){
                activeMathMarker.setEndProp(me.getSceneX(), me.getSceneY());
            }
            me.consume();
        }
    };
    public static final EventHandler MC_MOUSE_RELEAS= new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent me) {
            activeMathMarker.eraseDragSource();
            activeMathMarker=null;
            ((Node)me.getSource()).removeEventFilter(MouseEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
            ((Node)me.getSource()).removeEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
            me.consume();
        }
    };

    public MathWire(){

    }

    public MathWire(FileInputStream fis, List<MathPin> ECList) throws IOException{
        //sourceMarker=new MathMarker(this);

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

            MathMarker wm;
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
                wm=new MathMarker(this,ax,ay,ex,ey,numOfLines,isHorizontal,constraints);
            }else{
                wm=new MathMarker(this);
            }
            if(j==0) this.sourceMarker=wm;
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
                MathPin ECofWC=ECList.get(indx);
                wm.setConnectedPin(ECofWC);
                ECofWC.setItsConnection(wm);
                if(redFlag==1){ //?????????? must be no EC
                    wm.setIsPlugged(false);
                }else{
                    //create cont
                    if(subNum==2){
                        // create second one
                        fis.read(temp.array(),0,4);   //index of EC
                        indx=temp.getInt(0);

                        MathPin ECofWM1=ECList.get(indx);
                        wm.bindStartTo(ECofWM1.getArrowX(), ECofWM1.getArrowY());

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
                            wm=new MathMarker(this,ax,ay,ex,ey,numOfLines,isHorizontal,constraints);
                        }else{
                            wm=new MathMarker(this);
                        }
                        wm.hide();
                        wm.setConnectedPin(ECofWM1);
                        ECofWM1.setItsConnection(wm);
                        wm.bindStartTo(ECofWC.getArrowX(), ECofWC.getArrowY());
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
     * @param mathPin
     * @param meSceneX
     * @param meSceneY
     */
    public MathWire(MathPin mathPin,double meSceneX,double meSceneY){
        sourceMarker=new MathMarker(this);
        MathMarker wc=new MathMarker(this);
        if(mathPin instanceof MathOutPin){
            sourceMarker.setConnectedPin(mathPin);  // link and bind end
            sourceMarker.bindStartTo(mathPin.getArrowX(), mathPin.getArrowY()); // zeroLength
            wc.bindStartTo(mathPin.getArrowX(), mathPin.getArrowY()); // same point
            wc.setEndProp(meSceneX,meSceneY);
            activeMathMarker=wc; // for mouse_drag event
        }else{
            wc.setConnectedPin(mathPin); // link and bind end
            wc.bindStartTo(mathPin.getArrowX(), mathPin.getArrowY());
            sourceMarker.bindStartTo(mathPin.getArrowX(), mathPin.getArrowY());
            sourceMarker.setEndProp(meSceneX,meSceneY);
            activeMathMarker=sourceMarker;
        }
    }

//    /**
//     * Биндит линию к контакту элемента
//     * @param elemCont контакт элемента
//     */
//    public void setEnd(MathPin elemCont){
//        if(wireContList.size()==1){
//            MathPin oldEc=activeMathMarker.getElemContact();   // начальный O--->
//            activeMathMarker.setElemContact(elemCont);           // --->О цепляем
//
//            MathMarker wcNew=new MathMarker(this); // ? bind?      // <---O новый
//            wcNew.setElemContact(oldEc);
//
//            elemCont.bindWCstartProp(wcNew);                           // OX--->O  цепляем
//            wcNew.hide();
//        }
//        else{
//            activeMathMarker.setElemContact(elemCont);
//        }
//    }

    public boolean setEnd(MathPin cont){
        boolean success=false;
        if(sourceMarker.equals(activeMathMarker)&&cont instanceof MathOutPin){
            activeMathMarker.setConnectedPin(cont);
            success=true;
        }else if(!sourceMarker.equals(activeMathMarker)&&cont instanceof MathInPin){
            activeMathMarker.setConnectedPin(cont);
            success=true;
        }
        return success;
    }

    /**
     * Возвращяет число контактов
     * @return
     */
    public int getRank(){
        return(this.mathMarkList.size());
    }

    public List<MathMarker> getWireContacts(){
        return(mathMarkList);
    };

    public MathMarker addContact(){
        MathMarker wc=new MathMarker(this);
        return wc;
    }

    public void Save(ByteArrayOutputStream baos,List<MathPin> ECList){
        ByteBuffer temp=ByteBuffer.allocate(8);
        double startPx,startPy,endPx,endPy;
        //num of subwires
        temp.putInt(0, getWireContacts().size());
        baos.write(temp.array(), 0, 4);

        //num of CrToCr
        temp.putInt(0, getContContList().size());
        baos.write(temp.array(), 0, 4);

        // describe each WireMarker
        for(MathMarker wc:getWireContacts()){
            int indx;
            short redFlag;
            MathPin ec=wc.getConnectedPin();
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

    public void addContToCont(MathMarker wc1,MathMarker wc2){
        CrossToCrossLine contToContLine = new CrossToCrossLine(this,wc1,wc2);
        contToContLine.setColor(COLOR);
        contToContLine.activate();
        getContContList().add(contToContLine);
    }

    public CrossToCrossLine addContToCont(double sx,double sy,double ex,double ey){
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
    public MathMarker addContact(MathPin elemCont){//boolean endbind???
        MathMarker wc=new MathMarker(this,elemCont);
        this.mathMarkList.add(wc);
        return wc;
    }

    /**
     * Разбиндивает все узлы
     */
    public void unBindAll(){
        for(MathMarker wc:this.mathMarkList){
            wc.unBindStartPoint();
        }
    }

    /**
     * @return the source
     */
    public MathOutPin getSource() {
        return source;
    }

    /**
     * @param source the source to set. Also set link in each marker!
     */
    public void setSourcePointer(MathOutPin source) {
        this.source = source;
        for(int i=1;i<mathMarkList.size();i++){
            MathInPin pin=(MathInPin)mathMarkList.get(i).getConnectedPin();
            if(pin!=null)
                pin.setSource(source);
        }
//        this.destin.setSource(source);
//        this.source.setSource(destin);
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
        raschetkz.RaschetKz.mathContsList.remove(this);
        if(!mathMarkList.isEmpty()){
            activeMathMarker=mathMarkList.get(0);// for prevent dead loop
            int i=mathMarkList.size()-1;
            for(;i>=0;i--){
                if(!mathMarkList.isEmpty())
                    mathMarkList.get(i).delete();
            }
            activeMathMarker=null;
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

    private class CrossToCrossLine extends ConnectLine{
        private Cross endCrossMarker;
        private MathWire owner;
        /**
         *
         * @param wc1
         * @param wc2 new one
         */
        CrossToCrossLine(MathWire owner,MathMarker wc1,MathMarker wc2){
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
                    MathMarker wm=new MathMarker(owner,x,y);
                    activeMathMarker=wm;
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
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
        CrossToCrossLine(MathWire owner,double sx,double sy,double ex,double ey){
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
                    MathMarker wm=new MathMarker(owner,x,y);
                    activeMathMarker=wm;
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
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

    public class MathMarker extends LineMarker{
        //List<MathMarker> subContacts;
        private MathWire itsWire;

        private MathPin connectedPin;
        //private Polygon startView;



        MathMarker(){
            super();
//            subContacts=new ArrayList();
            view=new Polygon(0,0,0,8.0,6.0,6.0/2.0);
            view.setTranslateX(-2.0);
            view.setTranslateY(-3.0);
            view.layoutXProperty().bind(bindX);
            view.layoutYProperty().bind(bindY);
            raschetkz.RaschetKz.drawBoard.getChildren().add(view);

//            startView=new Polygon(0,0,0,8,6,4);
//            startView.setTranslateX(-2.0);
//            startView.setTranslateY(-3.0);
//            this.itsLines.getStartMarker().centerXProperty().bind(startView.layoutXProperty());
//            this.itsLines.getStartMarker().centerYProperty().bind(startView.layoutYProperty());
//            raschetkz.RaschetKz.drawBoard.getChildren().add(startView);

            /*itsLines.setLineDragDetect(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent mde){
                    if(mde.getButton()==MouseButton.SECONDARY){
                        MathMarker mc=new MathMarker(itsWire,mde.getX(), mde.getY());
                        activeMathMarker=mc;
//                        subContacts.add(mc);
                        mc.view.startFullDrag();
                    }
                }
            });*/
            itsLines.setColor(COLOR);
            itsLines.setLineDragDetect((EventHandler<MouseEvent>)(MouseEvent me)->{
                if(me.getButton()==MouseButton.SECONDARY){
                    if(itsWire.getWireContacts().size()==1){
                        me.consume();
                        return;
                    }
                    if(itsWire.getWireContacts().size()==2){
                        MathMarker newCont=new MathMarker(itsWire,me.getX(), me.getY());
                        activeMathMarker=newCont;
                        adjustCrosses(newCont,
                                itsWire.getWireContacts().get(0),
                                itsWire.getWireContacts().get(1));
                        List<Cross> list=new ArrayList();
                        list.add(newCont.getItsLine().getStartMarker());
                        list.add(itsWire.getWireContacts().get(0).getItsLine().getStartMarker());
                        list.add(itsWire.getWireContacts().get(1).getItsLine().getStartMarker());
                        dotList.add(list);

                        ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
                        ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
                        newCont.startFullDrag();
                        itsWire.getWireContacts().forEach(wc->{
                            wc.show();
                        });
                        me.consume();
                    }
                    else{
                        MathMarker newCont=new MathMarker(itsWire,me.getX(), me.getY());
                        activeMathMarker=newCont;
                        ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
                        ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
                        newCont.startFullDrag();
                        itsWire.addContToCont(this,newCont);
                    }
                    me.consume();
                }
            });

//            itsLines.getEndX().bind(bindX);
//            itsLines.getEndY().bind(bindY);


//            startView.setOnMouseEntered(me->{
//                startView.toFront();
//                startView.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
//                startView.setCursor(Cursor.HAND);
//            });
//            startView.setOnMouseExited(me->{
//                startView.setEffect(null);
//                startView.setCursor(Cursor.DEFAULT);
//                dragSource=null;
//            });
//            startView.setOnDragDetected(me->{
//                activeMathMarker=this;
//                this.pushToBack();
//                startView.startFullDrag();
//                startView.addEventFilter(MouseEvent.MOUSE_DRAGGED, Connections.LineMarker.MC_MOUSE_DRAG);
//                startView.addEventFilter(MouseDragEvent.MOUSE_RELEASED, Connections.LineMarker.MC_MOUSE_RELEAS);
//            });

            view.setOnMouseEntered(me->{
//                view.toFront();
                view.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.AQUA, 2, 1, 0, 0));
                view.setCursor(Cursor.HAND);
            });
            view.setOnMouseExited(me->{
                view.setEffect(null);
                view.setCursor(Cursor.DEFAULT);
            });
            view.setOnDragDetected(me->{
                activeMathMarker=this;
                dragSource=view;
                this.pushToBack();
                view.startFullDrag();
                view.addEventFilter(MouseEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
                view.addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
            });

        }

        MathMarker(MathWire owner){
            this();
            this.itsWire=owner;
            itsWire.getWireContacts().add(this);
            pushToBack();
        }

        MathMarker(MathWire owner,MathPin inp){
            this(owner);
            bindEndTo(inp.getArrowX(), inp.getArrowY());
            inp.setItsConnection(this);
            setConnectedPin(inp);
            pushToBack();
            if(inp instanceof MathInPin){

                connectedPin=(MathInPin)inp;
            }else{

                source=(MathOutPin)inp;
//                inp.hide();
            }
        }

        MathMarker(MathWire owner,MathOutPin start,MathInPin end){
            this(owner);
            itsWire.setSourcePointer(start);
            connectedPin=end;
//            source=start;
            bindX.bind(end.getArrowX());
            bindY.bind(end.getArrowY());

            /*?????????*/itsLines.bindStart(start.getArrowX(), start.getArrowY());


            plugged.set(true);
            //        destin.hide();
//            source.hide();
            end.setMathConnLink(this);
            start.setMathConnLink(this);
            //majorConnect=this;
        }

        MathMarker(MathWire owner,double x,double y){
            this(owner);
            itsLines.setStartXY(x, y);
            bindX.set(x);
            bindY.set(y);
            this.setIsPlugged(false);

            //itsLines.bindStart(majorConnect.getItsLine().getStartMarker()); ?

        }

        MathMarker(MathWire thisWire,double startX,double startY,double endX,double endY,int numOfLines,boolean isHorizontal,List<Double> constrList){
            this(thisWire);
            itsLines.setStartXY(startX, startY);
            bindX.set(endX);
            bindY.set(endY);
            itsLines.rearrange(numOfLines,isHorizontal,constrList);
        }

        public List<Double> getValue(){
            if(getSource()!=null){
                return getSource().getValue();
            }else{
                List<Double> out=new ArrayList();
                out.add(0.0);
                return out;
            }
        }

        /**
         * @return the itsBranch
         */
        public MathWire getWire() {
            return itsWire;
        }

        public DoubleProperty getStartX(){
            return this.itsLines.getStartX();
        }

        public DoubleProperty getStartY(){
            return this.itsLines.getStartY();
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
         * Удаление контакта и линии
         */
        @Override
        void delete(){
            // check if this is sourceMarker!
            if(this.equals(itsWire.sourceMarker)){
                if(connectedPin!=null){
                    connectedPin.clearPin();
                    connectedPin=null;
                }
                itsWire.getWireContacts().remove(this);
                raschetkz.RaschetKz.drawBoard.getChildren().remove(view);
                unbindEndPoint();
                unBindStartPoint();
                itsLines.delete();
                itsLines=null;
                itsWire.delete();
                itsWire=null;
                return;
            }

            //reduce dotList
            Point2D p=MathPack.MatrixEqu.findFirst(dotList, this.getItsLine().getStartMarker());
            if(p!=null&&activeMathMarker==null){
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

            if(connectedPin!=null){
                connectedPin.clearPin();
                connectedPin=null;
            }
            itsWire.getWireContacts().remove(this);
            if(itsWire.getWireContacts().size()<2)
                itsWire.delete();
            itsWire=null;
            raschetkz.RaschetKz.drawBoard.getChildren().remove(view);
            unbindEndPoint();
            unBindStartPoint();
            itsLines.delete();
            itsLines=null;
        }

        public void unPlug(){
            activeMathMarker=this;
            setIsPlugged(false);
            this.bindX.unbind();
            this.bindY.unbind();

            if(connectedPin instanceof MathInPin){  //==? this.connectedPin.clearWireContact();
                connectedPin.clearPin();
                unbindEndPoint();
                view.setVisible(true);
                view.toBack();
            }else{
                source=null;
//                if(connectedPin!=null)
//                    itsWire.setSource(null);
                connectedPin.clearPin();
//                startView.layoutXProperty().unbind();
//                startView.layoutYProperty().unbind();
//                startView.setVisible(true);
//                startView.toBack();
            }


            connectedPin=null;

//            caller.show();
        }

        @Override
        protected void activate(){
            getItsLine().activate();
            this.view.setVisible(false);
//            this.startView.setVisible(false);
        }

        @Override
        protected void diactivate(){
            getItsLine().diactivate();
            //        super.diactivate();
            //        this.startView.setVisible(false);
        }

        public void eraseDragSource(){
            dragSource=null;
        }

        /**
         * @return the destin
         */
        public MathPin getConnectedPin() {
            return connectedPin;
        }

        /**
         * Links and binds end propetry of line
         * @param pin the destin to set
         */
        final public void setConnectedPin(MathPin pin) {
            connectedPin = pin;
            bindEndTo(pin.getArrowX(), pin.getArrowY());
            pin.setItsConnection(this);
            setIsPlugged(true);

            if(pin instanceof MathInPin)
                ((MathInPin)pin).setSource(source);


//            itsWire.getSource().setSource(destin);
        }


        /**
         * Отрисовка линии
         * @param x - координата в scene coord
         * @param y
         */
        @Override
        public void setEndProp(double x,double y){
            Point2D a=new Point2D(x, y);
            a=raschetkz.RaschetKz.drawBoard.sceneToLocal(a);
//            if(dragSource==null)
//                if(!bindX.isBound()){
//                    bindX.set(a.getX());
//                    bindY.set(a.getY());
//                }else{
//                    startView.setLayoutX(a.getX());
//                    startView.setLayoutY(a.getY());
//                }
//            else
//                if(dragSource==view){
//                    bindX.set(a.getX());
//                    bindY.set(a.getY());
//                }else{
//                    startView.setLayoutX(a.getX());
//                    startView.setLayoutY(a.getY());
//                }

            bindX.set(a.getX());
            bindY.set(a.getY());
        }

        @Override
        final public void pushToBack() {
            super.pushToBack();
            //startView.toBack();
        }
    }
}

