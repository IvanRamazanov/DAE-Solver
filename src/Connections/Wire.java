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

import ElementBase.Element;
import ElementBase.Pin;
import Elements.Environment.Subsystem.Subsystem;
import MathPack.Parser;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

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
    private static String[] wireConsumptionBackup=new String[4];
    private Cross eventCross;
    private Subsystem itsSystem;
    private String domainType;
    private final EventHandler<MouseDragEvent> mouseExit=new EventHandler<MouseDragEvent>() {
        @Override
        public void handle(MouseDragEvent event) {
            //restore wires
            eventCross.removeEventFilter(MouseDragEvent.MOUSE_DRAG_EXITED,this);
            eventCross.getOwner().getWire().delete();
            String className=wireConsumptionBackup[1];
            Class<?> clas= null;
            try {
                clas = Class.forName(className);
                Constructor<?> ctor=clas.getConstructor(Subsystem.class);
                Wire w1=(Wire)ctor.newInstance(getItsSystem());
                //RaschetKz.wireList.add(w1);
                w1.configure(wireConsumptionBackup[2]);
                Wire w2=(Wire)ctor.newInstance(getItsSystem());
                //RaschetKz.wireList.add(w2);
                w2.configure(wireConsumptionBackup[3]);

                activeWireConnect=w1.getWireContacts().get(Integer.valueOf(wireConsumptionBackup[0]));
                //activeWireConnect.setEndPoint(event.getX(),event.getY());
                activeWireConnect.pushToBack();
                Node obj=activeWireConnect.getMarker();
                MouseEvent e1=new MouseEvent(obj,obj,MouseDragEvent.MOUSE_PRESSED,0,0,0,0,event.getButton(),1,false,false,false,false,
                        event.isPrimaryButtonDown(),event.isMiddleButtonDown(),event.isSecondaryButtonDown(),event.isSynthesized(),event.isPopupTrigger(),
                        true,new PickResult(obj,event.getSceneX(),event.getSceneY()));
                MouseEvent e2=new MouseEvent(obj,obj,MouseDragEvent.DRAG_DETECTED,0,0,0,0,event.getButton(),1,false,false,false,false,
                        event.isPrimaryButtonDown(),event.isMiddleButtonDown(),event.isSecondaryButtonDown(),event.isSynthesized(),event.isPopupTrigger(),
                        true,event.getPickResult());
//                MouseEvent eve=MouseEvent.copyForMouseDragEvent(event,activeWireConnect.getMarker(),event.getTarget(),MouseDragEvent.ANY,event.getGestureSource(),
//                        event.getPickResult());
//                setStaticEventFilters(activeWireConnect.getMarker());
                activeWireConnect.getMarker().fireEvent(e1);
//                activeWireConnect.getMarker().fireEvent(e2);
//                activeWireConnect.getMarker().setOnMouseMoved(e->{
//                    activeWireConnect.setEndPropInSceneCoordinates(e.getSceneX(),e.getSceneY());
//                });

                System.out.println("wires restored");
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    };
    private final EventHandler<MouseDragEvent> mouseReleased=new EventHandler<MouseDragEvent>() {
        @Override
        public void handle(MouseDragEvent event) {
            eventCross.removeEventFilter(MouseDragEvent.MOUSE_DRAG_EXITED,mouseExit);
            eventCross.removeEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED,this);
        }
    };
    public static final EventHandler WC_MOUSE_DRAG = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent me) {
            if(activeWireConnect!=null)
                if(!activeWireConnect.getIsPlugged().get()){
                    activeWireConnect.setEndPropInSceneCoordinates(me.getSceneX(), me.getSceneY());
                }
            me.consume();
        }
    };
    public static final EventHandler WC_MOUSE_RELEAS= new EventHandler<MouseDragEvent>() {
        @Override
        public void handle(MouseDragEvent me){
            if(activeWireConnect!=null)
                activeWireConnect.toFront();
            activeWireConnect=null;
            ((Node)me.getGestureSource()).setOnMouseDragged(null);
            ((Node)me.getGestureSource()).setOnMouseDragReleased(null);
            me.consume();
        }
    };

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
     * @param wc1 Strat marker of first ElectricMarker
     * @param wc2 Strat marker of second ElectricMarker
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
            major.toFront();
            for(int i=1;i<line.size();i++){
                line.get(i).bindToCross(major);
            }
        }
    }

    public void configure(String info){
        // SubWire cycle
        int cnt=0,stIndex,endIndex;
        String swkey="<SubWire"+cnt+">";
//        endswKey="</SubWire"+cnt+">";
        String wireMarkerInfo;

        while(!(wireMarkerInfo=Parser.getBlock(info,swkey)).isEmpty()){

//            String[] lines = wireMarkerInfo.split("\r\n");

            double[] constraints,startEnd;
            String connectedElem= Parser.getKeyValue(wireMarkerInfo,"<ConnectTo>"),pinIndex=null;
            startEnd=Parser.parseRow(Parser.getKeyValue(wireMarkerInfo,"<StartEnd>"));
            double ax=startEnd[0],
                    ay=startEnd[1],
                    ex=startEnd[2],
                    ey=startEnd[3];

            String dotlistpoint=Parser.getKeyValue(wireMarkerInfo,"<DotListIndex>");

            if(!connectedElem.equals("null")){
                pinIndex=Parser.getKeyValue(wireMarkerInfo,"<PinIndex>");
            }

            boolean isEasy=Boolean.valueOf(Parser.getKeyValue(wireMarkerInfo,"<EasyDraw>")),
                    isHorizontal=Boolean.valueOf(Parser.getKeyValue(wireMarkerInfo,"<Horizontal>"));
            constraints=Parser.parseRow(Parser.getKeyValue(wireMarkerInfo,"<Constraints>"));

            LineMarker wm;

            if(!isEasy){
                wm=addLineMarker(this,ax,ay,ex,ey,isHorizontal, constraints);
            }else{
                wm=addLineMarker(this);
            }
            wm.getBindX().set(ex);
            wm.getBindY().set(ey);
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
                Element elem;
                if(connectedElem.contains("$")){
                    String sysName=connectedElem.substring(0,connectedElem.indexOf("$"));
                    Subsystem sys=(Subsystem) Element.findElement(getItsSystem(), sysName);
                    sysName=connectedElem.substring(connectedElem.indexOf("$")+1);
                    elem=Element.findElement(sys, sysName);

                }else {
                    elem = Element.findElement(getItsSystem(), connectedElem);
                }
                Pin ECofWC = elem.getPin(pinIndex);
                wm.bindElemContact(ECofWC);
                ECofWC.setWirePointer(wm);
            }

            cnt++;
            swkey="<SubWire"+cnt+">";
//            endswKey="</SubWire"+cnt+">";
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
                wm.getBindX().set(ex);
                wm.getBindY().set(ey);
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
        while(!(wireMarkerInfo=Parser.getBlock(info,swkey)).isEmpty()){

//            String[] lines = wireMarkerInfo.split("\r\n");

            double[] startEnd=Parser.parseRow(Parser.getKeyValue(wireMarkerInfo,"<StartEnd>"));
            double ax=startEnd[0],
                    ay=startEnd[1],
                    ex=startEnd[2],
                    ey=startEnd[3];
            boolean easyDraw=Boolean.valueOf(Parser.getKeyValue(wireMarkerInfo,"<EasyDraw>"));

            CrossToCrossLine line=addContToCont(ax,ay,ex,ey);
            if(!easyDraw){
                boolean isHrizon=Boolean.valueOf(Parser.getKeyValue(wireMarkerInfo,"<Horizontal>"));
                double[] constraints=Parser.parseRow(Parser.getKeyValue(wireMarkerInfo,"<Constraints>"));
                line.rearrange(isHrizon, constraints);
            }


            // indexes in dotList
            double[] points=Parser.parseRow(Parser.getKeyValue(wireMarkerInfo,"<DotListIndex>"));
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
        }
        bindCrosses();
    }

    abstract protected LineMarker addLineMarker(Wire wire,double ax,double ay,double ex,double ey,boolean isHorizontal, double[] constraints);

    abstract protected LineMarker addLineMarker(Wire wire,double x,double y);

    abstract protected LineMarker addLineMarker(Wire wire);

    public boolean setEnd(Pin pin){
        switch(getWireContacts().size()){
            case 1:
                Pin oldEc=activeWireConnect.getItsConnectedPin();   // начальный O--->
                activeWireConnect.bindElemContact(pin);           // --->О цепляем
                LineMarker wcNew=addLineMarker(this); // ? bind?      // <---O новый
                wcNew.bindElemContact(oldEc);
                wcNew.bindStartTo(oldEc.getBindX(),oldEc.getBindY());
                break;
            case 2:
                if(!getWireContacts().get(0).isPlugged()&&!getWireContacts().get(1).isPlugged()) {   // free floating wire case
                    LineMarker loser;
                    if(getWireContacts().get(0).equals(activeWireConnect))
                        loser=getWireContacts().get(1);
                    else
                        loser=getWireContacts().get(0);
                    activeWireConnect.setEndPoint(loser.getBindX().get(),loser.getBindY().get());
                    activeWireConnect.bindStartTo(pin.getBindX(),pin.getBindY());
                    pin.setWirePointer(activeWireConnect);
                    activeWireConnect.setItsConnectedPin(pin);
                    loser.delete();
                }else{
                    throw new Error("Unexpected case!");
                }
                break;
            default:
                activeWireConnect.bindElemContact(pin);
        }
        return true;
    }

    protected String getWireColor(){
        return wireColor;
    }

    public Subsystem getItsSystem(){
        return itsSystem;
    }

    public void setItsSystem(Subsystem sys){
        itsSystem=sys;
    }

    protected void setWireColor(String color){
        wireColor=color;
    }

    public void delete(){
        getItsSystem().getWireList().remove(this);
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

    public String save(){
        StringBuilder bw=new StringBuilder();
        String eol="\r\n";

        bw.append("<ClassName>");
        bw.append(getClass().getName());
        bw.append("</ClassName>");bw.append(eol);

//        bw.append("<Subsystem>");
//        bw.append(getItsSystem().getTypeName());
//        bw.append("</Subsystem>");bw.append(eol);

        int i=0;
        for(LineMarker lm:getWireContacts()){
            bw.append("<SubWire"+i+">");bw.append(eol);

            Pin p=lm.getItsConnectedPin();
            Element elem=null;
            int pinIndex=-1;
            if(p!=null) {
                elem = p.getOwner();
                pinIndex = elem.findPin(p);
            }

            bw.append("<ConnectTo>");
            if(elem==null) {
                bw.append("null");
                bw.append("</ConnectTo>");
                bw.append(eol);
            }else{
                Subsystem sys=getItsSystem();
                if(sys.getElementList().indexOf(elem)==-1){
                    Subsystem sys2=elem.getItsSystem();
                    if(sys.getElementList().indexOf(elem.getItsSystem())==-1){
                        throw new Error("Strange, connect to somewhere");
                    }else{
                        bw.append(sys2.getName()+"$"+elem.getName());
                    }
                }else
                    bw.append(elem.getName());
                bw.append("</ConnectTo>");bw.append(eol);

                bw.append("<PinIndex>");
                String pinType=p.getClass().getSimpleName();
                bw.append(pinType+"."+Integer.toString(pinIndex));
                bw.append("</PinIndex>");bw.append(eol);
            }

            bw.append("<StartEnd>");
            String se="["+lm.getStartX().getValue().toString()+" "+
                    lm.getStartY().getValue().toString()+" "+
                    lm.getBindX().getValue().toString()+" "+
                    lm.getBindY().getValue().toString()+"]";
            bw.append(se);
            bw.append("</StartEnd>");bw.append(eol);

            bw.append("<DotListIndex>");
            Point2D dp=MathPack.MatrixEqu.findFirst(getDotList(), lm.getItsLine().getStartMarker());
            if(dp==null)
                bw.append("null");
            else
                bw.append("["+dp.getX()+" "+dp.getY()+"]");
            bw.append("</DotListIndex>");bw.append(eol);

            bw.append("<EasyDraw>");
            boolean easyDraw=lm.getItsLine().isEasyDraw();
            bw.append(Boolean.toString(easyDraw));
            bw.append("</EasyDraw>");bw.append(eol);

            if(!easyDraw){
                bw.append("<Horizontal>");
                bw.append(Boolean.toString(lm.getItsLine().getLines().get(0).isHorizontal()));
                bw.append("</Horizontal>");bw.append(eol);

                // array of constraints
                List<Double> constraints=lm.getItsLine().parseLines();
                bw.append("<Constraints>");
                bw.append(constraints.toString());
                bw.append("</Constraints>");bw.append(eol);
            }

            bw.append("</SubWire"+i+">");bw.append(eol);

            i++;
        }
        // write wire marker owner and index, if exists


        i=0;
        for(CrossToCrossLine lm:getContContList()){
            bw.append("<CrossToCrossLine"+i+">");bw.append(eol);

            bw.append("<StartEnd>");
            String se="["+lm.getStartX().getValue().toString()+" "+
                    lm.getStartY().getValue().toString()+" "+
                    lm.getEndCrossMarker().getCenterX()+" "+
                    lm.getEndCrossMarker().getCenterX()+"]";
            bw.append(se);
            bw.append("</StartEnd>");bw.append(eol);

            bw.append("<EasyDraw>");
            boolean easyDraw=lm.isEasyDraw();
            bw.append(Boolean.toString(easyDraw));
            bw.append("</EasyDraw>");bw.append(eol);

            if(!easyDraw){
                bw.append("<Horizontal>");
                bw.append(Boolean.toString(lm.getLines().get(0).isHorizontal()));
                bw.append("</Horizontal>");bw.append(eol);

                // array of constraints
                List<Double> constraints=lm.parseLines();
                bw.append("<Constraints>");
                bw.append(constraints.toString());
                bw.append("</Constraints>");bw.append(eol);
            }

            bw.append("<DotListIndex>");
            Point2D st=MathPack.MatrixEqu.findFirst(getDotList(), lm.getStartMarker());
            Point2D en=MathPack.MatrixEqu.findFirst(getDotList(), lm.getEndCrossMarker());
            bw.append("["+st.getX()+" "+st.getY()+" "+en.getX()+" "+en.getY()+"]");
            bw.append("</DotListIndex>");bw.append(eol);

            bw.append("</CrossToCrossLine"+i+">");bw.append(eol);
            i++;
        }
        return bw.toString();
    }

    void consumeWire(CrossToCrossLine eventSource,MouseDragEvent mde){
        double x=mde.getX(),y=mde.getY();
        Wire consumedWire=activeWireConnect.getWire();

        wireConsumptionBackup[0]=Integer.toString(consumedWire.getWireContacts().indexOf(activeWireConnect));
        wireConsumptionBackup[1]=getClass().getName();
        wireConsumptionBackup[2]=consumedWire.save();
        wireConsumptionBackup[3]=this.save();

        switch(consumedWire.getRank()) {
            case 1:
                activeWireConnect.setWire(this);

                // add to this wire
                this.getWireContacts().add(activeWireConnect);

                consumedWire.getWireContacts().remove(0);
                consumedWire.delete();  // remove empty wire
                //flip
                activeWireConnect.getItsLine().getStartMarker().unbind();
                activeWireConnect.setStartPoint(x, y);
                activeWireConnect.bindElemContact(activeWireConnect.getItsConnectedPin());
                addContToCont(eventSource.getStartMarker(), activeWireConnect.getItsLine().getStartMarker());

                eventCross = activeWireConnect.getItsLine().getStartMarker();
                activeWireConnect = null;
                break;
            case 2:
                //?? TODO well...
                eventCross = null;
                break;
            default:
                double sx=activeWireConnect.getStartX().get(),
                        sy=activeWireConnect.getStartY().get();
                // merge lists
                Point2D p=MathPack.MatrixEqu.findFirst(consumedWire.getDotList(),activeWireConnect.getItsLine().getStartMarker());
                p=p.add(getDotList().size(),0);
                getDotList().addAll(consumedWire.getDotList());
                consumedWire.getWireContacts().remove(activeWireConnect);
                this.getWireContacts().addAll(consumedWire.getWireContacts());
                for(LineMarker lm:consumedWire.getWireContacts()){
                    lm.setWire(this);
                }
                consumedWire.getWireContacts().clear();
                this.getContContList().addAll(consumedWire.getContContList());
                for(CrossToCrossLine lm:consumedWire.getContContList()){
                    lm.setWire(this);
                }
                consumedWire.getContContList().clear();
                consumedWire.delete();

                // replace with crosToCros
                CrossToCrossLine replacementLine = this.addContToCont(sx,sy,x,y);
                getDotList().get((int) p.getX()).set((int)p.getY(),replacementLine.getStartMarker());
                activeWireConnect.delete();
                activeWireConnect=null;

                this.addContToCont(eventSource.getStartMarker(),replacementLine.getEndCrossMarker());
                eventCross=replacementLine.getEndCrossMarker();

        }

        eventCross.addEventFilter(MouseDragEvent.MOUSE_DRAG_EXITED,mouseExit);
        eventCross.addEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED,mouseReleased);
    }

    void consumeWire(LineMarker eventSource,MouseDragEvent mde){
        MouseDragEvent nEvent=mde.copyFor(mde.getGestureSource(),mde.getTarget(),MouseDragEvent.MOUSE_DRAG_RELEASED);

        double x=mde.getX(),y=mde.getY();
        Wire consumedWire=activeWireConnect.getWire();

        wireConsumptionBackup[0]=Integer.toString(consumedWire.getWireContacts().indexOf(activeWireConnect));
        wireConsumptionBackup[1]=getClass().getName();
        wireConsumptionBackup[2]=consumedWire.save();
        wireConsumptionBackup[3]=this.save();

        switch(consumedWire.getRank()){
            case 1:
                activeWireConnect.setWire(this);

                // add to this wire
                this.getWireContacts().add(activeWireConnect);

                consumedWire.getWireContacts().remove(0);
                consumedWire.delete();  // remove empty wire
                //flip
                activeWireConnect.getItsLine().getStartMarker().unbind();
                activeWireConnect.setStartPoint(x,y);
                activeWireConnect.bindElemContact(activeWireConnect.getItsConnectedPin());

                switch(this.getRank()) {
                    case 1+1:


                        LineMarker wm = addLineMarker(this, x, y);
                        // adjustment
                        List<Cross> row = new ArrayList();
                        row.add(activeWireConnect.getItsLine().getStartMarker());
                        row.add(eventSource.getItsLine().getStartMarker());
                        row.add(wm.getItsLine().getStartMarker());
                        this.getDotList().add(row);
                        bindCrosses();

                        // move before rebind
                        wm.getBindX().set(eventSource.getBindX().doubleValue());
                        wm.getBindY().set(eventSource.getBindY().doubleValue());
                        eventSource.bindElemContact(eventSource.getItsConnectedPin());
                        break;
                    case 2+1:
                        // adjustment
                        row = new ArrayList();
                        row.add(activeWireConnect.getItsLine().getStartMarker());
                        row.add(this.getWireContacts().get(0).getItsLine().getStartMarker());
                        row.add(this.getWireContacts().get(1).getItsLine().getStartMarker());
                        this.getDotList().add(row);
                        bindCrosses();
                        this.showAll();
                        break;
                    default:
                        addContToCont(eventSource.getItsLine().getStartMarker(),activeWireConnect.getItsLine().getStartMarker());
                        break;
                }
                eventCross=activeWireConnect.getItsLine().getStartMarker();
                activeWireConnect=null;
                break;
            case 2:
                System.out.println("Hi, you there!"); // TODO This case is present, when fully unplugged wire connects. Also MathWire case.
                if(consumedWire.getWireContacts().get(0).getItsConnectedPin()==null&&
                        consumedWire.getWireContacts().get(1).getItsConnectedPin()==null) {
                    // fully disconnected case
                }else{
                    // MathWire case?
                    if(consumedWire.getWireContacts().get(1).getItsConnectedPin()!=null){ //make sure that is mathwire case

                    }
                }
                eventCross=null;
                break;
            default:
                double sx=activeWireConnect.getStartX().get(),
                        sy=activeWireConnect.getStartY().get();
                int rank=this.getRank();
                // merge lists
                Point2D p=MathPack.MatrixEqu.findFirst(consumedWire.getDotList(),activeWireConnect.getItsLine().getStartMarker());
                p=p.add(getDotList().size(),0);
                getDotList().addAll(consumedWire.getDotList());
                consumedWire.getWireContacts().remove(activeWireConnect);
                getWireContacts().addAll(consumedWire.getWireContacts());
                for(LineMarker lm:consumedWire.getWireContacts()){
                    lm.setWire(this);
                }
                consumedWire.getWireContacts().clear();
                this.getContContList().addAll(consumedWire.getContContList());
                for(CrossToCrossLine lm:consumedWire.getContContList()){
                    lm.setWire(this);
                }
                consumedWire.getContContList().clear();
                consumedWire.delete();

                // replace with crosToCros
                CrossToCrossLine replacementLine = this.addContToCont(sx,sy,x,y);
                getDotList().get((int) p.getX()).set((int)p.getY(),replacementLine.getStartMarker());
                activeWireConnect.delete();
                activeWireConnect=null;
                switch(rank){
                    case 1:
                        //TODO u know what to do
                        break;
                    case 2:
                        List<Cross> row=new ArrayList();
                        row.add(this.getWireContacts().get(0).getItsLine().getStartMarker());

                        row.add(replacementLine.getEndCrossMarker());
                        row.add(this.getWireContacts().get(1).getItsLine().getStartMarker());
                        this.getDotList().add(row);
                        this.bindCrosses();
                        showAll();
                        break;
                    default:
                        this.addContToCont(eventSource.getItsLine().getStartMarker(),replacementLine.getEndCrossMarker());
                }
                eventCross=replacementLine.getEndCrossMarker();
        }
        eventCross.addEventFilter(MouseDragEvent.MOUSE_DRAG_EXITED,mouseExit);
        eventCross.addEventFilter(MouseDragEvent.MOUSE_DRAG_RELEASED,mouseReleased);

        ((Node)mde.getGestureSource()).fireEvent(nEvent);
    }

    public List<LineMarker> getWireContacts(){
        return wireContList;
    }

    public List<Node> getView(){
        List<Node> out=new ArrayList<>();
        for(LineMarker lm:getWireContacts()){
            out.addAll(lm.getView());
        }
        for(CrossToCrossLine ctcl:getContContList()){
            out.addAll(ctcl.getView());
        }

        return out;
    }

    void showAll(){
        getWireContacts().forEach(wc->{
            wc.show();
        });
    }

    /**
     * Возвращяет число контактов
     * @return
     */
    public final int getRank(){
        return(this.getWireContacts().size());
    }

    public void setStaticEventFilters(Node source) {
        source.setOnMouseDragged(WC_MOUSE_DRAG);
        source.setOnMouseDragReleased( WC_MOUSE_RELEAS);
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }
}

