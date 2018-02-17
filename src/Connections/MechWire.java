/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Connections;

import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import ElementBase.ElemMechPin;
import ElementBase.Pin;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import raschetkz.RaschetKz;

/**
 *
 * @author Ivan
 */
public class MechWire extends Wire{
    static final String COLOR="#949899";
    public static MechMarker activeWireConnect;
    private List<MechMarker> wireContList =new ArrayList<>();
//    private List<CrossToCrossLine> ContContList =new ArrayList<>();
//    List<List<Cross>> dotList=new ArrayList<>();

    public static final EventHandler MeC_MOUSE_DRAG = new EventHandler<MouseEvent>(){
        @Override
        public void handle(MouseEvent me) {
            if(MechWire.activeWireConnect!=null)
                if(!MechWire.activeWireConnect.getIsPlugged().get()){
                    MechWire.activeWireConnect.setEndProp(me.getSceneX(), me.getSceneY());
                }
            me.consume();
        }
    };
    public static final EventHandler MeC_MOUSE_RELEAS= new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent me){
            MechWire.activeWireConnect=null;
            ((Node)me.getSource()).removeEventFilter(MouseEvent.MOUSE_DRAGGED, MeC_MOUSE_DRAG);
            ((Node)me.getSource()).removeEventFilter(MouseDragEvent.MOUSE_RELEASED, MeC_MOUSE_RELEAS);
            me.consume();
        }
    };

    public MechWire(){

    }

    public MechWire(FileInputStream fis, List<ElemMechPin> ECList) throws IOException{
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

            MechMarker wm;
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
                wm=new MechMarker(this,ax,ay,ex,ey,numOfLines,isHorizontal,constraints);
            }else{
                wm=new MechMarker(this);
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
                ElemMechPin ECofWC=ECList.get(indx);
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

                        ElemMechPin ECofWM1=ECList.get(indx);
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
                            wm=new MechMarker(this,ax,ay,ex,ey,numOfLines,isHorizontal,constraints);
                        }else{
                            wm=new MechMarker(this);
                        }
                        wm.hide();
                        wm.bindElemContact(ECofWM1);
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
    public MechWire(ElemMechPin EleCont,double meSceneX,double meSceneY){
        MechMarker wc=new MechMarker(this,EleCont);
        wc.setEndProp(meSceneX,meSceneY);
        activeWireConnect=wc;
    }

    /**
     * Биндит линию к контакту элемента
     * @param elemCont контакт элемента
     */
    public void setEnd(ElemMechPin elemCont){
        switch(wireContList.size()){
            case 1:
                Pin oldEc=activeWireConnect.getItsConnectedPin();   // начальный O--->
                activeWireConnect.bindElemContact(elemCont);           // --->О цепляем

                MechMarker wcNew=new MechMarker(this); // ? bind?      // <---O новый
                wcNew.bindElemContact(oldEc);

                //            elemCont.bindWCstartProp(wcNew);                           // OX--->O  цепляем
                wcNew.bindStartTo(elemCont.getBindX(),elemCont.getBindY());
                wcNew.hide();
                break;
            case 2:
                if(!wireContList.get(0).isPlugged()&&!wireContList.get(1).isPlugged()) {   // free floating wire case
                    MechMarker loser;
                    if(wireContList.get(0).equals(activeWireConnect))
                        loser=wireContList.get(1);
                    else
                        loser=wireContList.get(0);
                    activeWireConnect.setEndProp(loser.getBindX().get(),loser.getBindY().get());
                    activeWireConnect.bindStartTo(elemCont.getBindX(),elemCont.getBindY());
                    elemCont.setWirePointer(activeWireConnect);
                    loser.delete();
                    //activeWireConnect=null;
//                    activeWireConnect.bindElemContact(elemCont);
//                    wireContList.get(1).setElemContact(elemCont);
                }else{
                    throw new Error("Unexpected case!");
                }
                break;
            default:
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

    void consumeWire(MechMarker eventSource,MouseDragEvent mde){
        double x=mde.getX(),y=mde.getY();
        MechWire consumedWire=activeWireConnect.getWire();


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


                        MechMarker wm = new MechMarker(this, x, y);
                        // adjustment
                        List<Cross> row = new ArrayList();
                        row.add(activeWireConnect.getItsLine().getStartMarker());
                        row.add(eventSource.getItsLine().getStartMarker());
                        row.add(wm.getItsLine().getStartMarker());
                        getDotList().add(row);
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
                        getDotList().add(row);
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
                        getDotList().add(row);
                        bindCrosses();
                        showAll();
                        break;
                    default:
                        this.addContToCont(eventSource.getItsLine().getStartMarker(),replacementLine.getEndCrossMarker());
                }
                //bindCrosses();
//                //find in dotList line with 'major' list
//                for(List<Cross> line:owner.dotList){
//                    Cross rem=wc1.getItsLine().getStartMarker();
//                    int index=line.indexOf(rem);
//                    if(index!=-1){ // set it's start cross as major
//                        line.remove(index);    //remove wc1 from list
//                        line.add(0, getStartMarker()); // set new major cross - CrToCr start one
//                        // add new line
//                        List<Cross> nLine=new ArrayList();
//                        nLine.add(endCrossMarker);
//                        nLine.add(wc1.getItsLine().getStartMarker());
//                        nLine.add(wc2.getItsLine().getStartMarker());
//                        owner.dotList.add(nLine);
//                        owner.bindCrosses();
//                        break;
//                    }
//                }
        }

    }

    public List<MechMarker> getWireContacts(){
        return(wireContList);
    };

    MechMarker addContact(){
        MechMarker wc=new MechMarker(this);
        return wc;
    }

    void showAll(){
        getWireContacts().forEach(wc->{
            wc.show();
        });
    }

    public void Save(ByteArrayOutputStream baos,List<ElemMechPin> ECList){
        ByteBuffer temp=ByteBuffer.allocate(8);
        double startPx,startPy,endPx,endPy;
        //num of subwires
        temp.putInt(0, getWireContacts().size());
        baos.write(temp.array(), 0, 4);

        //num of CrToCr
        temp.putInt(0, getContContList().size());
        baos.write(temp.array(), 0, 4);

        // describe each WireMarker
        for(MechMarker wc:getWireContacts()){
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
     *
     * @param wc1 Strat marker of first WireMarker
     * @param wc2 Strat marker of second WireMarker
     */
    void addContToCont(Cross wc1,Cross wc2){
        CrossToCrossLine contToContLine = new CrossToCrossLine(this, wc1, wc2);
        contToContLine.setColor(COLOR);
        contToContLine.activate();
        getContContList().add(contToContLine);
    }

    CrossToCrossLine addContToCont(double sx,double sy,double ex,double ey){
        CrossToCrossLine contToContLine = new CrossToCrossLine(this,sx,sy,ex,ey);
        contToContLine.setColor(COLOR);
        contToContLine.activate();
        getContContList().add(contToContLine);
        return contToContLine;
    }

    /**
     * Creates WireConnect and bind pointers. Bind start?
     * @param elemCont elem cont
     * @return
     */
    MechMarker addContact(ElemMechPin elemCont){//boolean endbind???
        MechMarker wc=new MechMarker(this,elemCont);
        this.wireContList.add(wc);
        return wc;
    }

    /**
     * Разбиндивает все узлы
     */
    public void unBindAll(){
        for(MechMarker wc:this.wireContList){
            wc.unBindStartPoint();
        }
    }

    /**
     * Binds all crosses in dotList to first Cross in each line
     */
    void bindCrosses(){
        for(List<Cross> line:getDotList()){
            Cross major=line.get(0);
            major.setVisible(true);
            for(int i=1;i<line.size();i++){
                line.get(i).bindToCross(major);
            }
        }
    }

    public void delete(){
        RaschetKz.MechWireList.remove(this);
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

//    /**
//     * @return the ContContList
//     */
//    List<CrossToCrossLine> getContContList() {
//        return ContContList;
//    }




}

