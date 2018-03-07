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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ElementBase.ElemPin;
import ElementBase.Element;
import ElementBase.Pin;
import MathPack.Parser;
import javafx.geometry.Point2D;

/**
 *
 * @author Ivan
 */
public abstract class Wire{
    private String wireColor;
    private List<LineMarker> wireContList =new ArrayList<>();
    private List<CrossToCrossLine> ContContList =new ArrayList<>();
    private List<List<Cross>> dotList=new ArrayList<>();
    public static LineMarker activeWireConnect;

    public List<CrossToCrossLine> getContContList() {
        return ContContList;
    }

    public void setContContList(List<CrossToCrossLine> contContList) {
        ContContList = contContList;
    }

    public List<List<Cross>> getDotList() {
        return dotList;
    }

    public void setDotList(List<List<Cross>> dotList) {
        this.dotList = dotList;
    }

    /**
     *
     * @param wc1 Strat marker of first WireMarker
     * @param wc2 Strat marker of second WireMarker
     */
    protected void addContToCont(Cross wc1,Cross wc2){
        CrossToCrossLine contToContLine = new CrossToCrossLine(this, wc1, wc2);
        contToContLine.setColor(getWireColor());
        contToContLine.activate();
        getContContList().add(contToContLine);
    }

    protected CrossToCrossLine addContToCont(double sx,double sy,double ex,double ey){
        CrossToCrossLine contToContLine = new CrossToCrossLine(this,sx,sy,ex,ey);
        contToContLine.setColor(getWireColor());
        contToContLine.activate();
        getContContList().add(contToContLine);
        return contToContLine;
    }

    /**
     * Binds all crosses in dotList to first Cross in each line
     */
    protected void bindCrosses(){
        for(List<Cross> line:getDotList()){
            Cross major=line.get(0);
            major.setVisible(true);
            for(int i=1;i<line.size();i++){
                line.get(i).bindToCross(major);
            }
        }
    }

    public void configure(String info){
        // SubWire cycle
        int cnt=0,stIndex,endIndex;
        String swkey="<SubWire"+cnt+">",endswKey="</SubWire"+cnt+">";
        while((stIndex=info.indexOf(swkey))!=-1){
            endIndex=info.lastIndexOf(endswKey);
            String wireMarkerInfo=info.substring(stIndex+swkey.length()+System.lineSeparator().length(),endIndex);

            String[] lines = wireMarkerInfo.split(System.getProperty("line.separator"));

            boolean isEasyDraw;
            double[] constraints,startEnd;
            String connectedElem= Parser.getKeyValue(lines,"<ConnectTo>"),pinIndex=null;
            startEnd=Parser.parseRow(Parser.getKeyValue(lines,"<StartEnd>"));
            double ax=startEnd[0],
                    ay=startEnd[1],
                    ex=startEnd[2],
                    ey=startEnd[3];

            String dotlistpoint=Parser.getKeyValue(lines,"<DotListIndex>");

            if(!connectedElem.equals("null")){
                pinIndex=Parser.getKeyValue(lines,"<PinIndex>");
            }

            boolean isEasy=Boolean.valueOf(Parser.getKeyValue(lines,"<EasyDraw>")),
                    isHorizontal=Boolean.valueOf(Parser.getKeyValue(lines,"<Horizontal>"));
            constraints=Parser.parseRow(Parser.getKeyValue(lines,"<Constraints>"));

            LineMarker wm;

            if(!isEasy){
                wm=addLineMarker(this,ax,ay,ex,ey,isHorizontal, constraints);
            }else{
                wm=addLineMarker(this);
            }
            wm.setEndProp(ex, ey);
            wm.setStartPoint(ax, ay);
            if(!dotlistpoint.equals("null")){  //then exist Cross in dotList
                double[] point=Parser.parseRow(dotlistpoint);
                for(int k=(int)point[0]-(getDotList().size()-1);k>0;k--)
                    getDotList().add(new ArrayList());
                for(int k=(int)point[1]-(getDotList().get((int)point[0]).size()-1);k>0;k--)
                    getDotList().get((int)point[0]).add(null);
                getDotList().get((int)point[0]).set((int)point[1],wm.getItsLine().getStartMarker());
            }

            //set up wireCont
            if(!connectedElem.equals("null")) {
                Element elem=Element.findElement(connectedElem);
                Pin ECofWC = elem.getPin(pinIndex);
                wm.bindElemContact(ECofWC);
                ECofWC.setWirePointer(wm);
            }

            cnt++;
            swkey="<SubWire"+cnt+">";
            endswKey="</SubWire"+cnt+">";
        }

        switch(cnt) { // 2 subWireCase
            case 1:
                // reverse
                LineMarker wm=getWireContacts().get(0);
                wm.unbindEndPoint();
                double[] arr=Parser.parseRow(Parser.getFirstKeyValue(info,"<StartEnd>"));
                double ex=arr[2],
                        ey=arr[3];
                Pin p=wm.getItsConnectedPin();
                wm.bindStartTo(p.getBindX(),p.getBindY());
                wm.setEndProp(ex,ey);
                wm.setIsPlugged(false);
                break;
            case 2:
                LineMarker wm1=getWireContacts().get(0);
                LineMarker wm2=getWireContacts().get(1);
                //wm2.hide();
                Pin wm1Pin=wm1.getItsConnectedPin();
                Pin wm2Pin=wm2.getItsConnectedPin();
                if(wm1Pin==null^wm2Pin==null) { // unplugged MathWire case
                    if(wm1Pin==null){  //connected to mathInPin
                        wm2.bindStartTo(wm2Pin.getBindX(),wm2Pin.getBindY());
                        wm1.bindStartTo(wm2Pin.getBindX(),wm2Pin.getBindY());
                    }else{
                        wm1.bindStartTo(wm1Pin.getBindX(),wm1Pin.getBindY());
                        wm2.bindStartTo(wm1Pin.getBindX(),wm1Pin.getBindY());
                    }
                }else{
                    //case of O----O  and general case too
                    wm1.bindStartTo(wm2.getBindX(),wm2.getBindY());

                    wm2.bindStartTo(wm2.getBindX(),wm2.getBindY());
                    wm2.getItsLine().setEasyDraw(true);
//                wm2.getItsLine().getStartX().set(ax);
//                wm2.getItsLine().getStartY().set(ay);
//                wm2.getBindX().set(ex);
//                wm2.getBindY().set(ey);
//                    wm2.setIsPlugged(false);
                }
//                wm2.bindElemContact(wm1Pin);
//                wm1.getItsConnectedPin().setWirePointer(wm2);
//                wm2.bindStartTo(wm1Pin.getBindX(), wm1Pin.getBindY());
                break;
            default:

        }

        cnt=0;
        swkey="<CrossToCrossLine"+cnt+">";
        endswKey="</CrossToCrossLine"+cnt+">";
        while((stIndex=info.indexOf(swkey))!=-1){
            endIndex=info.lastIndexOf(endswKey);
            String wireMarkerInfo=info.substring(stIndex,endIndex);
            String[] lines = wireMarkerInfo.split(System.getProperty("line.separator"));

            double[] startEnd=Parser.parseRow(Parser.getKeyValue(lines,"<StartEnd>"));
            double ax=startEnd[0],
                    ay=startEnd[1],
                    ex=startEnd[2],
                    ey=startEnd[3];
            boolean easyDraw=Boolean.valueOf(Parser.getKeyValue(lines,"<EasyDraw>"));

            CrossToCrossLine line=addContToCont(ax,ay,ex,ey);
            if(!easyDraw){
                boolean isHrizon=Boolean.valueOf(Parser.getKeyValue(lines,"<Horizontal>"));
                double[] constraints=Parser.parseRow(Parser.getKeyValue(lines,"<Constraints>"));
                line.rearrange(isHrizon, constraints);
            }


            // indexes in dotList
            double[] points=Parser.parseRow(Parser.getKeyValue(lines,"<DotListIndex>"));
            int sti=(int)points[0];
            int stj=(int)points[1];
            int eni=(int)points[2];
            int enj=(int)points[3];
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

            cnt++;
            swkey="<CrossToCrossLine"+cnt+">";
            endswKey="</CrossToCrossLine"+cnt+">";
        }
        bindCrosses();
    }

    abstract protected LineMarker addLineMarker(Wire wire,double ax,double ay,double ex,double ey,boolean isHorizontal, double[] constraints);

    abstract protected LineMarker addLineMarker(Wire wire);

    protected String getWireColor(){
        return wireColor;
    }

    protected void setWireColor(String color){
        wireColor=color;
    }

    public void delete(){
        raschetkz.RaschetKz.wireList.remove(this);
        if(!getWireContacts().isEmpty()){
            activeWireConnect=getWireContacts().get(0);// for prevent dead loop
            int i=getWireContacts().size()-1;
            for(;i>=0;i--){
                if(!getWireContacts().isEmpty())
                    getWireContacts().get(i).delete();
            }
            activeWireConnect=null;
        }
        for(int i=getContContList().size()-1;i>=0;i--){
            if(!getContContList().isEmpty())
                getContContList().get(i).delete();
        }
        getDotList().clear();
    }

    public void save(BufferedWriter bw) throws IOException{
        bw.write("<ClassName>");
        bw.write(getClass().getName());
        bw.write("</ClassName>");bw.newLine();

        int i=0;
        for(LineMarker lm:getWireContacts()){
            bw.write("<SubWire"+i+">");bw.newLine();

            Pin p=lm.getItsConnectedPin();
            Element elem=null;
            int pinIndex=-1;
            if(p!=null) {
                elem = p.getOwner();
                 pinIndex = elem.findPin(p);
            }

            bw.write("<ConnectTo>");
            if(elem==null) {
                bw.write("null");
                bw.write("</ConnectTo>");
                bw.newLine();
            }else{
                bw.write(elem.getName());
                bw.write("</ConnectTo>");bw.newLine();

                bw.write("<PinIndex>");
                String pinType=p.getClass().getSimpleName();
                bw.write(pinType+"."+Integer.toString(pinIndex));
                bw.write("</PinIndex>");bw.newLine();
            }

            bw.write("<StartEnd>");
            String se="["+lm.getStartX().getValue().toString()+" "+
                    lm.getStartY().getValue().toString()+" "+
                    lm.getBindX().getValue().toString()+" "+
                    lm.getBindY().getValue().toString()+"]";
            bw.write(se);
            bw.write("</StartEnd>");bw.newLine();

            bw.write("<DotListIndex>");
            Point2D dp=MathPack.MatrixEqu.findFirst(getDotList(), lm.getItsLine().getStartMarker());
            if(dp==null)
                bw.write("null");
            else
                bw.write("["+dp.getX()+" "+dp.getY()+"]");
            bw.write("</DotListIndex>");bw.newLine();

            bw.write("<EasyDraw>");
            boolean easyDraw=lm.getItsLine().isEasyDraw();
            bw.write(Boolean.toString(easyDraw));
            bw.write("</EasyDraw>");bw.newLine();

            if(!easyDraw){
                bw.write("<Horizontal>");
                bw.write(Boolean.toString(lm.getItsLine().getLines().get(0).isHorizontal()));
                bw.write("</Horizontal>");bw.newLine();

                // array of constraints
                List<Double> constraints=lm.getItsLine().parseLines();
                bw.write("<Constraints>");
                bw.write(constraints.toString());
                bw.write("</Constraints>");bw.newLine();
            }

//            //get num of lines
//            temp.putInt(0,wc.getItsLine().getLines().size());
//            baos.write(temp.array(), 0, 4);

            bw.write("</SubWire"+i+">");bw.newLine();

            i++;
        }
        // write wire marker owner and index, if exists


        i=0;
        for(CrossToCrossLine lm:getContContList()){
            bw.write("<CrossToCrossLine"+i+">");bw.newLine();

            bw.write("<StartEnd>");
            String se="["+lm.getStartX().getValue().toString()+" "+
                    lm.getStartY().getValue().toString()+" "+
                    lm.getEndCrossMarker().getCenterX()+" "+
                    lm.getEndCrossMarker().getCenterX()+"]";
            bw.write(se);
            bw.write("</StartEnd>");bw.newLine();

            bw.write("<EasyDraw>");
            boolean easyDraw=lm.isEasyDraw();
            bw.write(Boolean.toString(easyDraw));
            bw.write("</EasyDraw>");bw.newLine();

            if(!easyDraw){
                bw.write("<Horizontal>");
                bw.write(Boolean.toString(lm.getLines().get(0).isHorizontal()));
                bw.write("</Horizontal>");bw.newLine();

                // array of constraints
                List<Double> constraints=lm.parseLines();
                bw.write("<Constraints>");
                bw.write(constraints.toString());
                bw.write("</Constraints>");bw.newLine();
            }

            bw.write("<DotListIndex>");
            Point2D st=MathPack.MatrixEqu.findFirst(getDotList(), lm.getStartMarker());
            Point2D en=MathPack.MatrixEqu.findFirst(getDotList(), lm.getEndCrossMarker());
            bw.write("["+st.getX()+" "+st.getY()+" "+en.getX()+" "+en.getY()+"]");
            bw.write("</DotListIndex>");bw.newLine();

            bw.write("</CrossToCrossLine"+i+">");bw.newLine();
            i++;
        }
    }

    public List<LineMarker> getWireContacts(){
        return wireContList;
    }

    /**
     * Возвращяет число контактов
     * @return
     */
    public final int getRank(){
        return(this.getWireContacts().size());
    }
}

