package Connections;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

class CrossToCrossLine extends ConnectLine{
    private Cross endCrossMarker;
    private Wire owner;

    private CrossToCrossLine(Wire owner){
        super(owner);

        this.getLines().forEach(extLine->{
            //extLine.setOnKeyReleased(null);
            extLine.setOnKeyReleased(k->{
                if(k.getCode()== KeyCode.DELETE){
                    this.delete();
                }
            });
        });

        setLineDragOver(de->{
            if(Wire.activeWireConnect!=null){
                if(getWire().getWireContacts().get(0).isProperInstance(Wire.activeWireConnect))
                    if(Wire.activeWireConnect.getWire()!=getWire()){
                        getWire().consumeWire(this,(MouseDragEvent)de);
                    }
            }
        });

        setLineDragDetect((EventHandler<MouseDragEvent>)(MouseDragEvent me)->{
            if(me.getButton().equals(MouseButton.SECONDARY)){
                double x=me.getX(),y=me.getY();
                CrossToCrossLine newOne=getWire().addContToCont(x,y,
                        this.getEndCrossMarker().getCenterX(),this.getEndCrossMarker().getCenterY());
                this.getEndCrossMarker().unbind();
                this.setEndXY(x, y);

                List<Cross> line=new ArrayList();

                //create new ElectricMarker
                Wire.activeWireConnect = new ElectricMarker(getWire(),x,y);
                line.add(Wire.activeWireConnect.getItsLine().getStartMarker());
                getWire().setStaticEventFilters(((Node) me.getGestureSource()));
                Wire.activeWireConnect.startFullDrag();

                // dotList manipulation
                line.add(this.getEndCrossMarker());
                line.add(newOne.getStartMarker());
                //replace old crTcr end to new end
                Point2D p=MathPack.MatrixEqu.findFirst(getWire().getDotList(), this.getEndCrossMarker());
                getWire().getDotList().get((int)p.getX()).set((int)p.getY(), newOne.getEndCrossMarker());
                getWire().getDotList().add(line);

                getWire().bindCrosses();
            }
        });
    }

    /**
     *
     * @param wc1
     * @param wc2 new one
     */
    CrossToCrossLine(Wire owner,Cross wc1,Cross wc2){
        this(owner);

        this.setStartXY(wc1.getCenterX(), wc1.getCenterY());
        endCrossMarker=new Cross(this,wc2.getCenterX(),wc2.getCenterY());
        this.getEndX().bind(endCrossMarker.centerXProperty());
        this.getEndY().bind(endCrossMarker.centerYProperty());
        endCrossMarker.centerXProperty().addListener(super.getPropListen());
        endCrossMarker.centerYProperty().addListener(super.getPropListen());

        //find in dotList line with 'major' list
        for(List<Cross> line:owner.getDotList()){
            Cross rem=wc1;
            int index=line.indexOf(rem);
            if(index!=-1){ // set it's start cross as major
                line.remove(index);    //remove wc1 from list
                line.add(0, getStartMarker()); // set new major cross - CrToCr start one
                // add new line
                List<Cross> nLine=new ArrayList();
                nLine.add(wc2);
                nLine.add(endCrossMarker);
                nLine.add(wc1);
                owner.getDotList().add(nLine);
                owner.bindCrosses();
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
    CrossToCrossLine(Wire owner,double sx,double sy,double ex,double ey){
        this(owner);

        this.setStartXY(sx, sy);
        endCrossMarker=new Cross(this,ex,ey);
        this.getEndX().bind(endCrossMarker.centerXProperty());
        this.getEndY().bind(endCrossMarker.centerYProperty());
        endCrossMarker.centerXProperty().addListener(super.getPropListen());
        endCrossMarker.centerYProperty().addListener(super.getPropListen());
    }

    @Override
    public void delete(){
        deleteQuiet();
        owner.delete();
    }

    void deleteQuiet(){
        super.delete();
        for(List<Cross> row:owner.getDotList()){
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

    @Override
    List<Node> getView(){
        List<Node> out=super.getView();
        out.add(getEndCrossMarker());

        return out;
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

    public void setWire(Wire owner) {
        this.owner = owner;
    }
}
