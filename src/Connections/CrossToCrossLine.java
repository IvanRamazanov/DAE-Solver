package Connections;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.List;

import static Connections.ElectricWire.WC_MOUSE_DRAG;
import static Connections.ElectricWire.WC_MOUSE_RELEAS;
import static Connections.ElectricWire.activeWireConnect;

class CrossToCrossLine extends ConnectLine{
    private Cross endCrossMarker;
    private Wire owner;
    /**
     *
     * @param wc1
     * @param wc2 new one
     */
    CrossToCrossLine(Wire owner,Cross wc1,Cross wc2){
        super();

        this.getLines().forEach(extLine->{
            //extLine.setOnKeyReleased(null);
            extLine.setOnKeyReleased(k->{
                if(k.getCode()== KeyCode.DELETE){
                    this.delete();
                }
            });
        });

        this.owner=owner;
        //this.getStartMarker().setVisible(true);
        this.setStartXY(wc1.getCenterX(), wc1.getCenterY());
        endCrossMarker=new Cross(this,wc2.getCenterX(),wc2.getCenterY());
        //endCrossMarker.setVisible(true);
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
                nLine.add(endCrossMarker);
                nLine.add(wc1);
                nLine.add(wc2);
                owner.getDotList().add(nLine);
                owner.bindCrosses();
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
                int len=owner.getDotList().size();
                List<Cross> line=new ArrayList();
                line.add(wm.getItsLine().getStartMarker());
                line.add(this.getEndCrossMarker());
                line.add(newOne.getStartMarker());
                //replace old crTcr end to new end
                Point2D p=MathPack.MatrixEqu.findFirst(owner.getDotList(), this.getEndCrossMarker());
                owner.getDotList().get((int)p.getX()).set((int)p.getY(), newOne.getEndCrossMarker());
                owner.getDotList().add(line);

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
    CrossToCrossLine(Wire owner,double sx,double sy,double ex,double ey){
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
                int len=owner.getDotList().size();
                List<Cross> line=new ArrayList();
                line.add(wm.getItsLine().getStartMarker());
                line.add(this.getEndCrossMarker());
                line.add(newOne.getStartMarker());
                //replace old crTcr end to new end
                Point2D p=MathPack.MatrixEqu.findFirst(owner.getDotList(), this.getEndCrossMarker());
                owner.getDotList().get((int)p.getX()).set((int)p.getY(), newOne.getEndCrossMarker());
                owner.getDotList().add(line);

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
