/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPack;

import Connections.ElectricWire;
import ElementBase.ElemPin;
import ElementBase.SchemeElement;
import Elements.ElectricalReference;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ivan
 */
class Variables{
    private List<ElemPin> pointers;
    private List<Double> values;
    private List<SchemeElement> elems;
    //List<SchemeElement> dElems;
    private List<ElectricWire> brchs;
//        boolean isDynamic=false;

    Variables(List<SchemeElement> elementList, List<ElectricWire> branchList){
        elems=elementList;
        brchs=branchList;
        pointers=new ArrayList();
        values=new ArrayList();
        elems.forEach(element->{
            element.getElemContactList().forEach(contact->{
                if(contact.getWireContact()!=null)
                    this.addVar(contact);
            });
//                element.init();
        });
        this.init();
    }

    void addVar(ElemPin input){
        pointers.add(input);
        values.add(Double.valueOf(0.0));
        values.add(Double.valueOf(0.0));
    }

    void setVector(List<Double> input){
        values.clear();
        values.addAll(input);
    }

    final void init(){
        //проверка на дурака!!!!!!!!!!!!!!!
        List<Integer> index=new ArrayList();
//            for(int i=0;i<this.elems.size();i++){
//                if(this.elems.get(i).isDynamic()){
//                    isDynamic=true;
//                    break;
//                }
//            }
        List<List<Double>> system=createMatr(false);
        List<List<Double>> b=new ArrayList();
        int last=system.get(0).size()-1;
        for(int i=0;i<system.size();i++){
            b.add(new ArrayList());
            b.get(i).add(system.get(i).get(last));
            system.get(i).remove(last);
        }
        List<List<Double>> inits;
        inits = MatrixEqu.parseSLAU(system,b);
        for(int i=1;i<inits.size();i++){
            this.values.set(i,inits.get(i).get(0));
        }
    }

    void reSolve(){
        List<List<Double>> system=createMatr(false); //true!!!
        List<List<Double>> b=new ArrayList();
        int last=system.get(0).size()-1;
        for(int i=0;i<system.size();i++){
            b.add(new ArrayList());
            b.get(i).add(system.get(i).get(last));
            system.get(i).remove(last);
        }
        List<List<Double>> inits=MatrixEqu.parseSLAU(system,b);
        for(int i=0;i<inits.size();i++){
            this.values.set(i, inits.get(i).get(0));
        }
    }

    List<List<Double>> createMatr(boolean reSolve){
        List<List<Double>> output=new ArrayList();
        int row=0;
        //potencials
        for(int i=0;i<this.brchs.size();i++){
            ElectricWire currentBr=this.brchs.get(i);
            ElemPin mainPapa=currentBr.getWireContacts().get(0).getElemContact();
            for(int j=1;j<currentBr.getWireContacts().size();j++){
                ElemPin papa=currentBr.getWireContacts().get(j).getElemContact();
                output.add(new ArrayList());
                for(int m=0;m<pointers.size()*2+1;m++){
                    output.get(row).add(Double.valueOf(0.0));
                }
                output.get(row).set(pointers.indexOf(mainPapa)*2,1.0);
                output.get(row).set(pointers.indexOf(papa)*2,-1.0);
                row++;
            }
        }
        //currents
        for(int i=0;i<this.brchs.size()-1;i++){
            ElectricWire currentBr=this.brchs.get(i);
            output.add(new ArrayList());
            for(int m=0;m<pointers.size()*2+1;m++){
                output.get(row).add(Double.valueOf(0.0));
            }
            for(int j=0;j<currentBr.getWireContacts().size();j++){
                ElemPin papa=currentBr.getWireContacts().get(j).getElemContact();
                output.get(row).set(pointers.indexOf(papa)*2+1,1.0);
            }
            row++;
        }
        //functions of elems
        for(int i=0;i<this.elems.size();i++){
            int index=this.pointers.size()*2;
            for(int j=0;j<this.elems.get(i).getElemContactList().size();j++){
                ElemPin papa=this.elems.get(i).getElemContactList().get(j);
                int ind=this.pointers.indexOf(papa);
                if(ind<index){
                    index=this.pointers.indexOf(papa);}
            }
//                List<List<Double>> k=this.elems.get(i).getK(reSolve);
//                List<List<Double>> c=this.elems.get(i).getC(reSolve);
//                for(int j=0;j<k.size();j++){
//                    output.add(new ArrayList());
//                    for(int m=0;m<pointers.size()*2;m++){
//                        output.get(row).add(new Double(0));
//                    }
//                    for(int m=0;m<k.get(0).size();m++){
//                        output.get(row).set(index*2+m,k.get(j).get(m));
//                    }
//                    output.get(row).add(c.get(j).get(0));
//                    row++;
//                }
        }
        //refference
        int flag=0;
        for(SchemeElement e:this.elems){
            if(e instanceof ElectricalReference){
                flag++;
            }
        }
        switch(flag){
            case 0:
                output.add(new ArrayList());
                for(int m=0;m<pointers.size()*2+1;m++){
                    output.get(row).add(Double.valueOf(0.0));
                }
                output.get(row).set(0,1.0);
                row++;
                break;
            default:
                output.add(new ArrayList());
                for(ElemPin m:pointers){
                    output.get(row).add(Double.valueOf(0.0));
                    if(m.getOwner() instanceof ElectricalReference){
                        output.get(row).add(1.0);
                    }else{
                        output.get(row).add(0.0);
                    }
                }
                output.get(row).add(0.0);
                break;
        }
//            output.forEach(r->{
//               System.out.println(r);
//            });
        return(output);
    }

    void updateElems(boolean init) {
        for(int i=0;i<this.pointers.size();i++){
            //this.pointers.get(i).addVals(this.values.get(2*i), this.values.get(2*i+1));
        }
        this.elems.forEach(ele->{
//                ele.updateVals(init);
        });
    }
}

