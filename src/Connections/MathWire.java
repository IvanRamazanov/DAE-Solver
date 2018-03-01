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

import ElementBase.Pin;
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
    public static MathMarker activeMathMarker;
    private List<MathMarker> mathMarkList =new ArrayList<>();

    private MathMarker sourceMarker;
    private static Shape dragSource;
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
            activeMathMarker.toFront();
            activeMathMarker=null;
            ((Node)me.getSource()).removeEventFilter(MouseEvent.MOUSE_DRAGGED, MC_MOUSE_DRAG);
            ((Node)me.getSource()).removeEventFilter(MouseDragEvent.MOUSE_RELEASED, MC_MOUSE_RELEAS);
            me.consume();
        }
    };

    public MathWire(){
        setWireColor("#000000");
    }

    public MathWire(FileInputStream fis, List<MathPin> ECList) throws IOException{
        this();

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
                            wm=new MathMarker(this,ax,ay,ex,ey,numOfLines,isHorizontal,constraints);
                        }else{
                            wm=new MathMarker(this);
                        }
                        wm.hide();
                        wm.setConnectedPin(ECofWM1);
                        ECofWM1.setItsConnection(wm);
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
        this();
        sourceMarker=new MathMarker(this);
        MathMarker wc=new MathMarker(this);
        if(mathPin instanceof MathOutPin){
            getSourceMarker().setConnectedPin(mathPin);  // link and bind end
            getSourceMarker().bindStartTo(mathPin.getBindX(), mathPin.getBindY()); // zeroLength
            wc.bindStartTo(mathPin.getBindX(), mathPin.getBindY()); // same point
            wc.setEndProp(meSceneX,meSceneY);
            activeMathMarker=wc; // for mouse_drag event
        }else{
            wc.setConnectedPin(mathPin); // link and bind end
            wc.bindStartTo(mathPin.getBindX(), mathPin.getBindY());
            getSourceMarker().bindStartTo(mathPin.getBindX(), mathPin.getBindY());
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
            Pin ec=wc.getItsConnectedPin();
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
            MathInPin pin=(MathInPin)mathMarkList.get(i).getItsConnectedPin();
            if(pin!=null)
                pin.setSource(source);
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
}

