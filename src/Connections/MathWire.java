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

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
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
//    private List<CrossToCrossLine> ContContList =new ArrayList<>();
//    List<List<Cross>> dotList=new ArrayList<>();

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
                for(int k=ii-(getDotList().size()-1);k>0;k--)
                    getDotList().add(new ArrayList());
                for(int k=jj-(getDotList().get(ii).size()-1);k>0;k--)
                    getDotList().get(ii).add(null);
                getDotList().get(ii).set(jj,wm.getItsLine().getStartMarker());
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
            for(int k=sti-(getDotList().size()-1);k>0;k--)
                getDotList().add(new ArrayList());
            for(int k=stj-(getDotList().get(sti).size()-1);k>0;k--)
                getDotList().get(sti).add(null);
            getDotList().get(sti).set(stj,line.getStartMarker());
            for(int k=eni-(getDotList().size()-1);k>0;k--)
                getDotList().add(new ArrayList());
            for(int k=enj-(getDotList().get(eni).size()-1);k>0;k--)
                getDotList().get(eni).add(null);
            getDotList().get(eni).set(enj, line.getEndCrossMarker());
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
            getSourceMarker().setConnectedPin(mathPin);  // link and bind end
            getSourceMarker().bindStartTo(mathPin.getArrowX(), mathPin.getArrowY()); // zeroLength
            wc.bindStartTo(mathPin.getArrowX(), mathPin.getArrowY()); // same point
            wc.setEndProp(meSceneX,meSceneY);
            activeMathMarker=wc; // for mouse_drag event
        }else{
            wc.setConnectedPin(mathPin); // link and bind end
            wc.bindStartTo(mathPin.getArrowX(), mathPin.getArrowY());
            getSourceMarker().bindStartTo(mathPin.getArrowX(), mathPin.getArrowY());
            getSourceMarker().setEndProp(meSceneX,meSceneY);
            activeMathMarker= getSourceMarker();
        }
    }

    public static Shape getDragSource() {
        return dragSource;
    }

    public static void setDragSource(Shape shape) {
        dragSource=shape;
    }

//    /**
//     * Биндит линию к контакту элемента
//     * @param elemCont контакт элемента
//     */
//    public void setEnd(MathPin elemCont){
//        if(wireContList.size()==1){
//            MathPin oldEc=activeMathMarker.getElemContact();   // начальный O--->
//            activeMathMarker.bindElemContact(elemCont);           // --->О цепляем
//
//            MathMarker wcNew=new MathMarker(this); // ? bind?      // <---O новый
//            wcNew.bindElemContact(oldEc);
//
//            elemCont.bindWCstartProp(wcNew);                           // OX--->O  цепляем
//            wcNew.hide();
//        }
//        else{
//            activeMathMarker.bindElemContact(elemCont);
//        }
//    }

    public boolean setEnd(MathPin cont){
        boolean success=false;
        if(getSourceMarker().equals(activeMathMarker)&&cont instanceof MathOutPin){
            activeMathMarker.setConnectedPin(cont);
            success=true;
        }else if(!getSourceMarker().equals(activeMathMarker)&&cont instanceof MathInPin){
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
            Point2D p=MathPack.MatrixEqu.findFirst(getDotList(), wc.getItsLine().getStartMarker());
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
            Point2D st=MathPack.MatrixEqu.findFirst(getDotList(), lin.getStartMarker());
            Point2D en=MathPack.MatrixEqu.findFirst(getDotList(), lin.getEndCrossMarker());
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
        CrossToCrossLine contToContLine = new CrossToCrossLine(this,wc1.getItsLine().getStartMarker(),wc2.getItsLine().getStartMarker());
        contToContLine.setColor(COLOR);
        contToContLine.activate();
        getContContList().add(contToContLine);
    }

//    public CrossToCrossLine addContToCont(double sx,double sy,double ex,double ey){
//        CrossToCrossLine contToContLine = new CrossToCrossLine(this,sx,sy,ex,ey);
//        contToContLine.setColor(COLOR);
//        contToContLine.activate();
//        getContContList().add(contToContLine);
//        return contToContLine;
//    }

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
        this.setSource(source);
        for(int i=1;i<mathMarkList.size();i++){
            MathInPin pin=(MathInPin)mathMarkList.get(i).getConnectedPin();
            if(pin!=null)
                pin.setSource(source);
        }
//        this.destin.setSource(source);
//        this.source.setSource(destin);
    }

//    /**
//     * Binds all crosses in dotList to first Cross in each line
//     */
//    private void bindCrosses(){
//        for(List<Cross> line:dotList){
//            Cross major=line.get(0);
//            major.setVisible(true);
//            for(int i=1;i<line.size();i++){
//                line.get(i).bindToCross(major);
//            }
//        }
//    }

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
        for(int i=getContContList().size()-1;i>=0;i--){
            if(!getContContList().isEmpty())
                getContContList().get(i).delete();
        }

    }

    public MathMarker getSourceMarker() {
        return sourceMarker;
    }

    public void setSource(MathOutPin source) {
        this.source = source;
    }

//    /**
//     * @return the ContContList
//     */
//    private List<CrossToCrossLine> getContContList() {
//        return ContContList;
//    }

//    private class CrossToCrossLine extends ConnectLine{
//        private Cross endCrossMarker;
//        private MathWire owner;
//        /**
//         *
//         * @param wc1
//         * @param wc2 new one
//         */
//        CrossToCrossLine(MathWire owner,MathMarker wc1,MathMarker wc2){
//            super();
//
//            this.getLines().forEach(extLine->{
//                //extLine.setOnKeyReleased(null);
//                extLine.setOnKeyReleased(k->{
//                    if(k.getCode()==KeyCode.DELETE){
//                        this.delete();
//                    }
//                });
//            });
//
//            this.owner=owner;
//            //this.getStartMarker().setVisible(true);
//            this.setStartXY(wc1.getStartX().get(), wc1.getStartY().get());
//            endCrossMarker=new Cross(this,wc2.bindX.get(),wc2.bindY.get());
//            //endCrossMarker.setVisible(true);
//            this.getEndX().bind(endCrossMarker.centerXProperty());
//            this.getEndY().bind(endCrossMarker.centerYProperty());
//            endCrossMarker.centerXProperty().addListener(super.getPropListen());
//            endCrossMarker.centerYProperty().addListener(super.getPropListen());
//            //find in dotList line with 'major' list
//            for(List<Cross> line:dotList){
//                if(line.contains(wc1.getItsLine().getStartMarker())){ // set it's start cross as major
//                    line.remove(line.indexOf(wc1.getItsLine().getStartMarker()));    //remove wc1 from list
//                    line.add(0, getStartMarker()); // set new major cross - CrToCr start one
//                    // add new line
//                    List<Cross> nLine=new ArrayList();
//                    nLine.add(endCrossMarker);
//                    nLine.add(wc1.getItsLine().getStartMarker());
//                    nLine.add(wc2.getItsLine().getStartMarker());
//                    dotList.add(nLine);
//                    bindCrosses();
//                    break;
//                }
//            }
//            setLineDragDetect((EventHandler<MouseEvent>)(MouseEvent me)->{
//                if(me.getButton().equals(MouseButton.SECONDARY)){
//                    double x=me.getX(),y=me.getY();
//                    CrossToCrossLine newOne=owner.addContToCont(x,y,
//                            this.getEndCrossMarker().getCenterX(),this.getEndCrossMarker().getCenterY());
//                    this.getEndCrossMarker().unbind();
//                    this.setEndXY(x, y);
//                    //create new WireMarker
//                    MathMarker wm=new MathMarker(owner,x,y);
//                    activeMathMarker=wm;
//                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
//                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
//                    wm.startFullDrag();
//                    // dotList manipulation
//                    int len=dotList.size();
//                    List<Cross> line=new ArrayList();
//                    line.add(wm.getItsLine().getStartMarker());
//                    line.add(this.getEndCrossMarker());
//                    line.add(newOne.getStartMarker());
//                    //replace old crTcr end to new end
//                    Point2D p=MathPack.MatrixEqu.findFirst(dotList, this.getEndCrossMarker());
//                    dotList.get((int)p.getX()).set((int)p.getY(), newOne.getEndCrossMarker());
//                    dotList.add(line);
//
//                    owner.bindCrosses();
//                }
//            });
//        }
//
//        /**
//         * Only creates line. Handle dotList by yourself
//         * @param sx
//         * @param sy
//         * @param ex
//         * @param ey
//         */
//        CrossToCrossLine(MathWire owner,double sx,double sy,double ex,double ey){
//            super();
//
//            this.getLines().forEach(extLine->{
//                //extLine.setOnKeyReleased(null);
//                extLine.setOnKeyReleased(k->{
//                    if(k.getCode()==KeyCode.DELETE){
//                        this.delete();
//                    }
//                });
//            });
//
//            this.owner=owner;
//            this.setStartXY(sx, sy);
//            endCrossMarker=new Cross(this,ex,ey);
//            this.getEndX().bind(endCrossMarker.centerXProperty());
//            this.getEndY().bind(endCrossMarker.centerYProperty());
//            endCrossMarker.centerXProperty().addListener(super.getPropListen());
//            endCrossMarker.centerYProperty().addListener(super.getPropListen());
//
//            setLineDragDetect((EventHandler<MouseEvent>)(MouseEvent me)->{
//                if(me.getButton().equals(MouseButton.SECONDARY)){
//                    double x=me.getX(),y=me.getY();
//                    CrossToCrossLine newOne=owner.addContToCont(x,y,
//                            this.getEndCrossMarker().getCenterX(),this.getEndCrossMarker().getCenterY());
//                    this.getEndCrossMarker().unbind();
//                    this.setEndXY(x, y);
//                    //create new WireMarker
//                    MathMarker wm=new MathMarker(owner,x,y);
//                    activeMathMarker=wm;
//                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
//                    ((Node)me.getSource()).addEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
//                    wm.startFullDrag();
//                    // dotList manipulation
//                    int len=dotList.size();
//                    List<Cross> line=new ArrayList();
//                    line.add(wm.getItsLine().getStartMarker());
//                    line.add(this.getEndCrossMarker());
//                    line.add(newOne.getStartMarker());
//                    //replace old crTcr end to new end
//                    Point2D p=MathPack.MatrixEqu.findFirst(dotList, this.getEndCrossMarker());
//                    dotList.get((int)p.getX()).set((int)p.getY(), newOne.getEndCrossMarker());
//                    dotList.add(line);
//
//                    owner.bindCrosses();
//                }
//            });
//        }
//
//        @Override
//        public void delete(){
//            //implement
//            deleteQuiet();
//            owner.delete();
//        }
//
//        void deleteQuiet(){
//            super.delete();
//            for(List<Cross> row:dotList){
//                row.remove(this.getStartMarker());
//                row.remove(this.getEndCrossMarker());
//            }
//            owner.getContContList().remove(this);
//            endCrossMarker.delete();
//        }
//
//        @Override
//        public void setColor(String rgb){
//            super.setColor(rgb);
//            getEndCrossMarker().setFill(Paint.valueOf(rgb));
//        }
//
//        /**
//         * @return the endCrossMarker
//         */
//        Cross getEndCrossMarker() {
//            return endCrossMarker;
//        }
//
//        final void setEndXY(double x,double y){
//            getEndCrossMarker().setCenterX(x);
//            getEndCrossMarker().setCenterY(y);
//        }
//    }


}

