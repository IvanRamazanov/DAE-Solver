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

import ElementBase.Pin;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Ivan
 */
public class ElectricWire extends Wire{
    public static WireMarker activeWireConnect;
    private List<WireMarker> wireContList =new ArrayList<>();

    public static final EventHandler WC_MOUSE_DRAG = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent me) {
            if(ElectricWire.activeWireConnect!=null)
                if(!ElectricWire.activeWireConnect.getIsPlugged().get()){
                    ElectricWire.activeWireConnect.setEndProp(me.getSceneX(), me.getSceneY());
                }
            me.consume();
        }
    };
    public static final EventHandler WC_MOUSE_RELEAS= new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent me){
            ElectricWire.activeWireConnect.toFront();
            ElectricWire.activeWireConnect=null;
            ((Node)me.getSource()).removeEventFilter(MouseEvent.MOUSE_DRAGGED, WC_MOUSE_DRAG);
            ((Node)me.getSource()).removeEventFilter(MouseDragEvent.MOUSE_RELEASED, WC_MOUSE_RELEAS);
            me.consume();
        }
    };

    public ElectricWire(){
        setWireColor("#b87333");
    }

    public ElectricWire(FileInputStream fis, List<ElemPin> ECList) throws IOException{
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
                for(int k=ii-(getDotList().size()-1);k>0;k--)
                    getDotList().add(new ArrayList());
                for(int k=jj-(getDotList().get(ii).size()-1);k>0;k--)
                    getDotList().get(ii).add(null);
                getDotList().get(ii).set(jj,wm.getItsLine().getStartMarker());
            }
            //set up wireCont
            if(indx!=-1){
                ElemPin ECofWC=ECList.get(indx);
                wm.bindElemContact(ECofWC);
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
                        wm.bindElemContact(ECofWM1);
                        ECofWM1.setWirePointer(wm);
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
     * @param EleCont
     * @param meSceneX
     * @param meSceneY
     */
    public ElectricWire(ElemPin EleCont,double meSceneX,double meSceneY){
        this();
        WireMarker wc=new WireMarker(this,EleCont);
        wc.setEndProp(meSceneX,meSceneY);
        activeWireConnect=wc;
    }

    /**
     * Биндит линию к контакту элемента
     * @param elemCont контакт элемента
     */
    public void setEnd(ElemPin elemCont){
        switch(wireContList.size()){
            case 1:
                System.out.println("Set end case 1");
                Pin oldEc=activeWireConnect.getItsConnectedPin();   // начальный O--->
                activeWireConnect.bindElemContact(elemCont);           // --->О цепляем
                WireMarker wcNew=new WireMarker(this); // ? bind?      // <---O новый
                wcNew.bindElemContact(oldEc);
                wcNew.bindStartTo(elemCont.getBindX(),elemCont.getBindY());
                wcNew.hide();
                break;
            case 2:
                System.out.println("Set end case 2");
                if(!wireContList.get(0).isPlugged()&&!wireContList.get(1).isPlugged()) {   // free floating wire case
                    WireMarker loser;
                    if(wireContList.get(0).equals(activeWireConnect))
                        loser=wireContList.get(1);
                    else
                        loser=wireContList.get(0);
                    activeWireConnect.setEndProp(loser.getBindX().get(),loser.getBindY().get());
                    activeWireConnect.bindStartTo(elemCont.getBindX(),elemCont.getBindY());
                    elemCont.setWirePointer(activeWireConnect);
                    activeWireConnect.setItsConnectedPin(elemCont);
                    loser.delete();
                }else{
                    throw new Error("Unexpected case!");
                }
                break;
            default:
                System.out.println("Set end case default");
                activeWireConnect.bindElemContact(elemCont);
        }
    }

    /**
     * Возвращяет число контактов
     * @return
     */
    public int getRank(){
        return(this.wireContList.size());
    }

    void consumeWire(WireMarker eventSource,MouseDragEvent mde){
        double x=mde.getX(),y=mde.getY();
        ElectricWire consumedWire=activeWireConnect.getWire();


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


                        WireMarker wm = new WireMarker(this, x, y);
                        // adjustment
                        List<Cross> row = new ArrayList();
                        row.add(activeWireConnect.getItsLine().getStartMarker());
                        row.add(eventSource.getItsLine().getStartMarker());
                        row.add(wm.getItsLine().getStartMarker());
                        this.getDotList().add(row);
                        bindCrosses();

                        // move before rebind
                        wm.setEndProp(eventSource.getBindX().doubleValue(), eventSource.getBindY().doubleValue());
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
                break;
            case 2:
                System.out.println("Hi, you there!"); // TODO This case is present, when fully unplugged wire connects.

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
                this.getWireContacts().addAll(getWireContacts());
                consumedWire.getWireContacts().clear();
                this.getContContList().addAll(consumedWire.getContContList());
                consumedWire.getContContList().clear();
                consumedWire.delete();

                // replace with crosToCros
                CrossToCrossLine replacementLine = this.addContToCont(sx,sy,x,y);
                getDotList().get((int) p.getX()).set((int)p.getY(),replacementLine.getStartMarker());
                activeWireConnect.delete();

                switch(rank){
                    case 1:
                        break;
                    case 2:
                        List<Cross> row=new ArrayList();
                        row.add(replacementLine.getEndCrossMarker());
                        row.add(this.getWireContacts().get(0).getItsLine().getStartMarker());
                        row.add(this.getWireContacts().get(1).getItsLine().getStartMarker());
                        this.getDotList().add(row);
                        this.bindCrosses();
                        showAll();
                        break;
                    default:
                        this.addContToCont(eventSource.getItsLine().getStartMarker(),replacementLine.getEndCrossMarker());
                }
        }
    }

    public List<WireMarker> getWireContacts(){
        return(wireContList);
    };

    void showAll(){
        getWireContacts().forEach(wc->{
            wc.show();
        });
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
        for(int i=getContContList().size()-1;i>=0;i--){
            if(!getContContList().isEmpty())
                getContContList().get(i).delete();
        }
        getDotList().clear();
    }
}
