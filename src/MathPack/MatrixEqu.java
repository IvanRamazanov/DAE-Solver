/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPack;

import Connections.Wire;
import Connections.Wire.WireMarker;
import ElementBase.ShemeElement;
import ElementBase.ElemPin;
import ElementBase.MathInPin;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;

/**
 *
 * @author Ivan
 */
public class MatrixEqu {
    public static  List<List<Double>> solveSLAU(List<List<Double>> mA,List<List<Double>> B) {
        for(int i=0;i<mA.size();i++){
            if(mA.get(i).get(i)==0){
                for(int j=i+1;j<mA.size();j++){
                    if(mA.get(j).get(i)!=0){
                        for(int k=0;k<mA.size();k++){
                            double temp=mA.get(i).get(k);
                            mA.get(i).set(k,mA.get(j).get(k));
                            mA.get(j).set(k,temp);
                        }
                        double temp=B.get(i).get(0);
                        B.get(i).set(0,B.get(j).get(0));
                        B.get(j).set(0,temp);
                        break;
                    }
                }
            }
            //solver----
            for(int j=0;j<mA.size();j++){
                double k;
                List<Double> row=new ArrayList();
                row.addAll(mA.get(i));
                row.add(B.get(i).get(0));
                if(i!=j){
                    k=-1*mA.get(j).get(i)/mA.get(i).get(i);
                    if(k!=0.0){
                        for(int m=0;m<row.size();m++){
                            row.set(m, row.get(m)*k);
                            if(m<mA.get(j).size()){
                            mA.get(j).set(m, row.get(m)+mA.get(j).get(m));
                            }
                            else{
                                B.get(j).set(0, B.get(j).get(0)+row.get(m));
                            }
                        }
                    }
                }
                else{
                    k=1/mA.get(j).get(i);
                    if(k!=1){
                        for(int m=0;m<row.size();m++){
                            if(m<mA.get(j).size()){
                                mA.get(j).set(m, mA.get(j).get(m)*k);
                            }
                            else{
                                B.get(j).set(0, B.get(j).get(0)*k);
                            }
                        }
                    }
                }
                //??????????
            }
            //--------
        }
        List<List<Double>> output=new ArrayList();
        B.forEach(data->{
            output.add(new ArrayList(data));
        });
        return(output);
    }
    
    /**
     * Euqlid norm of vecrot inp
     * @param inp
     * @return 
     */
    public static double norm(List<Double> inp){
        double out=0;
        for(double x:inp){
            out+=x*x;
        }
        out=Math.sqrt(out);
        return out;
    }
    
    public static <T> Point2D findFirst(List<List<T>> asd,T val){
        Point2D out=null;
        for(int i=0;i<asd.size();i++){
            if(asd.get(i).contains(val)){
                out=new Point2D(i,asd.get(i).lastIndexOf(val));
                break;
            }
        }
        return out;
    }
    
    /**
     * Euqlid norm of vector difference inp1-inp2
     * @param inp1
     * @param inp2
     * @return 
     */
    public static double normOfDiffer(double[] inp1,double[] inp2){
        double out=0;
        for(int i=0;i<inp1.length;i++){
            double add=Math.pow((inp1[i]-inp2[i])/inp1[i],2);
            if(Double.isNaN(add))
                out+=0;
            else
            out+=add;
        }
        out=Math.sqrt(out);
        return out;
    }
    
    public static List<List<Double>> parseSLAU(List<List<Double>> A,List<List<Double>> b) {
        List<Boolean> blacklist=new ArrayList();
        List<List<Double>> out=new ArrayList();
        List<List<Double>> mA=copyMatr(A);
        List<List<Double>> B=copyMatr(b);
        for (int i=0;i<mA.get(0).size();i++) {
            out.add(new ArrayList());
            blacklist.add(false);
        }
        for (List<Double> B1 : out) {
            B1.add(0.0);
        }

        while(true){
            if(mA.isEmpty()){
                break;
            }
            List<Double> niceRow=null;
            for(List<Double> row : mA){
                //get non zeros
                int a=0;
                for(double elem : row){
                    if(elem!=0.0){
                        a++;
                    }
                }
                if(a==1){
                    niceRow=row;
                    break;
                }
            }
            if(niceRow==null){
                for(int i=blacklist.size()-1;i>=0;i--){
                    if(blacklist.get(i)){
                        for(int j=0;j<mA.size();j++){
                            mA.get(j).remove(i);
                        }
//                        B.remove(i);
                    }
                }
                
                //---RESOLVING BAD INIT CONDS-----------(Not final version)
//                if(mA.size()!=mA.get(0).size()){ //строго говоря надо считать детерминант
//                    //set 1 variable
//                }else{
//                    List<List<Double>> res=SolveSLAU(mA,B); //may be res.size < out.size
//                }
                List<List<Double>> res=solveSLAU(mA,B);
                int k=0;
                for(int i=0;i<blacklist.size();i++){
                    if(!blacklist.get(i)){
                        out.get(i).set(0, res.get(k).get(0));
                        k++;
                    }
                }
                break;
            }else{
                int ind=mA.indexOf(niceRow);
                int subInd=0;
                for(double a : niceRow){
                    if(a!=0){
                        break;
                    }else{
                        subInd++;
                    }                                        
                }
                out.get(subInd).set(0, B.get(ind).get(0)/mA.get(ind).get(subInd));
                blacklist.set(subInd, true);
                niceRow.set(subInd, 0.0);
                for(int i=mA.size()-1;i>=0;i--){
                    List<Double> row=mA.get(i);
                    double k=row.get(subInd)*out.get(subInd).get(0);
                    row.set(subInd, 0.0);
                    B.get(mA.indexOf(row)).set(0, B.get(mA.indexOf(row)).get(0)-k);
                    int a=0;
                    for(double val:row){
                        if(val!=0.0){
                            a++;
                        }
                    }
                    if(a==0.0){
                        if(B.get(mA.indexOf(row)).get(0)!=0.0){
                            
                        }else{
                            //vot suda poidee
                        }
                        B.remove(i);
                        mA.remove(i); //vot eti

                    }
                }
            }
            
        }
        return(out);
    }
    
    /**
     * Create copy of A matrix.
     * @param A matrix that will be copied
     * @return new matrix
     */
    public static List<List<Double>> copyMatr(List<List<Double>> A){
        List<List<Double>> out=new ArrayList();
        for(List<Double> row:A){
            out.add(new ArrayList(row));
        }
        return(out);
    }
    
    /**
     * Create copy of A matrix.
     * @param A matrix that will be copied
     * @return new matrix
     */
    public static List<List<Integer>> initIntegerMatr(List<List<Integer>> A){
        List<List<Integer>> out=new ArrayList();
        for(List<Integer> row:A){
            out.add(new ArrayList(row));
        }
        return(out);
    }
    
    public static List<Double> mulMatxToRow(List<List<Double>> A, List<Double> B){
        List<Double> out=new ArrayList(B.size());
        for(int i=0;i<B.size();i++){
            List<Double> row=A.get(i);
            double sum=0;
            for(int j=0;j<row.size();j++){
                sum+=row.get(j)*B.get(j);
            }
            out.add(sum);
        }
        return out;
    }
    
    public static List<StringGraph> mulMatxToRow_s(List<List<StringGraph>> A, List<StringGraph> B){
        List<StringGraph> out=new ArrayList();
        for(int i=0;i<A.size();i++){
            if(A.get(i)!=null){
                List<Uzel> inps=new ArrayList();
                List<Integer> gains=new ArrayList();
                for(int j=0;j<B.size();j++){
                    inps.add(new FuncUzel("*",A.get(i).get(j).getRoot(),B.get(j).getRoot()));
                    gains.add(1);
                }
                out.add(new StringGraph(new FuncUzel("+",inps,gains)));
            }else{
                out.add(null);
            }
        }
        return out;
    }
    
    public static List<StringGraph> mulDMatxToSRow(List<List<Double>> A, List<StringGraph> B){
        List<StringGraph> out=new ArrayList();
        for(int i=0;i<A.size();i++){
            if(A.get(i)!=null){
                List<Uzel> inps=new ArrayList();
                List<Integer> gains=new ArrayList();
                for(int j=0;j<B.size();j++){
                    inps.add(new FuncUzel("*",new Const(A.get(i).get(j)),B.get(j).getRoot()));
                    gains.add(1);
                }
                out.add(new StringGraph(new FuncUzel("+",inps,gains)));
            }else{
                out.add(null);
            }
        }
        return out;
    }
    
    /**
     * Determinat of integer matrix
     * @param A integer matrix
     * @return det(A)
     */
    public static double det_i(List<List<Integer>> A){
        int numOfRows=A.size(),numOfCols=A.get(0).size();
        double result;
        switch (numOfRows) {
            case 1:
                result=A.get(0).get(0);
                break;
            case 2:
                result=A.get(0).get(0)*A.get(1).get(1)-A.get(0).get(1)*A.get(1).get(0);            
                break;
        //general case
            default:
                result=0;
                for(int i=0;i<numOfCols;i++){
                    List<List<Integer>> M=new ArrayList();
                    for(int j=1;j<numOfRows;j++){
                        M.add(new ArrayList(A.get(j)));
                        M.get(j-1).remove(i);
                    }
                    if(A.get(0).get(i)!=0.0){
                        result+=Math.pow(-1.0, i)*A.get(0).get(i)*det_i(M);
                    }else{
                        result+=0.0;
                    }
                }
                break;
        }
        return result;
    }
    
    /**
     * Determinant
     * @param A double matrix
     * @return det(A)
     */
    public static double det(List<List<Double>> A){
        int numOfRows=A.size(),numOfCols=A.get(0).size();
        double result;
        switch (numOfRows) {
            case 1:
                result=A.get(0).get(0);
                break;
            case 2:
                result=A.get(0).get(0)*A.get(1).get(1)-A.get(0).get(1)*A.get(1).get(0);            
                break;
        //general case
            default:
                result=0;
                for(int i=0;i<numOfCols;i++){
                    List<List<Double>> M=new ArrayList(numOfRows-1);
                    for(int j=1;j<numOfRows;j++){
                        M.add(new ArrayList(A.get(j)));
                        M.get(j-1).remove(i);
                    }
                    if(A.get(0).get(i)!=0.0){
                        result+=Math.pow(-1.0, i)*A.get(0).get(i)*det(M);
                    }else{
                        result+=0.0;
                    }
                }
                break;
        }
        return result;
    }
    
    /**
     * Determinant
     * @param A symbol matrix
     * @return det(A)
     */
    public static StringGraph det_s(List<List<StringGraph>> A){
        int numOfRows=A.size(),numOfCols=A.get(0).size();
        if(numOfCols!=numOfRows) throw new Error("Matrix dimensions missmatch!");
        StringGraph result;
        switch (numOfRows) {
            case 1:
                result=new StringGraph(A.get(0).get(0));
                break;
            case 2:
                result=StringGraph.sum(StringGraph.mul(A.get(0).get(0), A.get(1).get(1), 1),StringGraph.mul(A.get(0).get(1),A.get(1).get(0),1),-1);            
                break;
        //general case
            default:
                result=new StringGraph("0.0");
                for(int i=0;i<numOfCols;i++){
                    List<List<StringGraph>> M=new ArrayList(numOfRows-1);
                    for(int j=1;j<numOfRows;j++){
                        M.add(new ArrayList(A.get(j)));
                        M.get(j-1).remove(i);
                    }
                    if(A.get(0).get(i).getRoot() instanceof Const){
                        if(((Const)A.get(0).get(i).getRoot()).getValue()!=0.0){
                            double k=1.0-2.0*((double)((i)%2));
                            result.add(StringGraph.mul(StringGraph.mul(A.get(0).get(i), det_s(M), 1), (int) k));
                        }
                    }else{
                        double k=1.0-2.0*((double)((i)%2));
                        result.add(StringGraph.mul(StringGraph.mul(A.get(0).get(i), det_s(M), 1), (int) k));
                    }
                    
                }
                break;
        }
        return result;
    }
    
    /**
     * Converts integer matrix to double
     * @param inp integer matrix
     * @return double matrix
     */
    public static List<List<Double>> int2dbl(List<List<Integer>> inp){
        List<List<Double>> out=new ArrayList();
        int i=0;
        for(List<Integer> row:inp){
            out.add(new ArrayList());
            for(Integer val:row){
                out.get(i).add(val.doubleValue());
            }
            i++;
        }
        return out;
    }
    
    public static List<List<Double>> mul(List<List<Double>> A, List<List<Double>> B) {
        List<List<Double>> output=new ArrayList<>();
        for(int i=0;i<A.size();i++){
            List<Double> row=new ArrayList<>();
            for(int j=0;j<B.get(0).size();j++){
                double c=0;
                for(int k=0;k<A.get(0).size();k++){
                    c=c+A.get(i).get(k)*B.get(k).get(j);
                }
                row.add(c);
            }
            output.add(row);
        }
        return(output);
    }

    /**
     * Multiplex matrix to scalar
     * @param matrixB -matrix
     * @param d - scalar
     * @return matrix
     */
    public static List<List<Double>> mulElWise(List<List<Double>> matrixB, double d) {
        List<List<Double>> output=new ArrayList<>();
        matrixB.forEach(data ->{
            List<Double> row=new ArrayList<>();
            data.forEach(element ->{
                row.add(element*d);
            });
            output.add(row);
        });
        return(output);
    }
    
    public static List<Double> getColumn(List<List<Double>> A,int i){
        List<Double> out=new ArrayList(A.size());
        for(int j=0;j<A.size();j++)
            out.add(A.get(j).get(i));
        return out;
    }
    
    /**
     * Multiplex matrix to scalar
     * @param matrixB -matrix
     * @param d - scalar
     * @return matrix
     */
    public static List<List<StringGraph>> mulElWise_s(List<List<StringGraph>> matrixB, StringGraph d) {
        List<List<StringGraph>> output=new ArrayList<>();
        matrixB.forEach(data ->{
            List<StringGraph> row=new ArrayList<>();
            data.forEach(element ->{
                row.add(StringGraph.mul(element,d,1));
            });
            output.add(row);
        });
        return(output);
    }
    
    public static List<List<Double>> pinv(List<List<Double>> input){
        return mul(invMatr(mul(transpose(input), input)), transpose(input));
    }
    
    public static double[][] invMatr(double[][] A) {
        int len=A.length;
        if(len!=A[0].length){
            return(null);
        }        
        double[][] dumpA=new double[len][len];
        double[][] E=new double[len][len];
        for(int i=0;i<len;i++){
            System.arraycopy(A[i], 0, dumpA[i], 0, len);
            E[i][i]=1;
        }
        //----solver-------
        for(int i=0;i<len;i++){
            if(dumpA[i][i]==0){
                double[] tempA=dumpA[i];
                double[] tempE=E[i];
                for(int j=i+1;j<len;j++){
                    if(dumpA[j][i]!=0){
                        dumpA[i]=dumpA[j];
                        dumpA[j]=tempA;
                        E[i]=E[j];
                        E[j]=tempE;
                        break;
                    }
                }
            }
            if(dumpA[i][i]<0){
                for(int j=0;j<len;j++){
                    dumpA[i][j]=-1*dumpA[i][j];
                    E[i][j]= -1*E[i][j];
                }
            }
            
            //solver----
            double[] row=new double[len*2];
            System.arraycopy(dumpA[i], 0, row, 0, len);
            System.arraycopy(E[i], 0, row, len, len);
            for(int j=0;j<dumpA.length;j++){
                double k; 
                //row.addAll(dumpA.get(i));
                if(i!=j){
                    k=-1*dumpA[j][i]/row[i];
                    if(k!=1){
                        for(int m=0;m<row.length;m++){
                            if(m<dumpA[0].length)
                                dumpA[j][m]=k*row[m]+dumpA[j][m];
                            else
                                E[j][m-dumpA.length]=row[m]*k+E[j][m-dumpA.length];
                        }
                    }
                }else{
                    k=1/dumpA[j][i];
                    if(k!=1){
                        for(int m=0;m<row.length;m++){
                            if(m<dumpA.length)
                                dumpA[j][m]=dumpA[j][m]*k;
                            else
                                E[j][m-dumpA.length]=E[j][m-dumpA.length]*k;
                        }
                    }
                }
            }
            //??????????
        }
        //--------
        return(E);        
    }
    
    public static List<List<Double>> invMatr(List<List<Double>> A) {
        if(A.size()!=A.get(0).size()) throw new Error("Не квадратная матрица! "+A.size()+"x"+A.get(0).size());
        if(A.size()==1){
            List<List<Double>> out=new ArrayList();
            out.add(new ArrayList());
            out.get(0).add(1/A.get(0).get(0));
            return out;
        }else{
            return mulElWise(adjMatr(A),1/det(A));
        }
        
        /*
        int len=A.size();
        if(len!=A.get(0).size()){
            return(null);
        }        
        List<List<Double>> dumpA=copyMatr(A);
        List<List<Double>> E=new ArrayList(len);
        for(int i=0;i<len;i++){
            E.add(new ArrayList());
            for(int j=0;j<len;j++) E.get(i).add(0.0);
            E.get(i).set(i,1.0);
        }
        //----solver-------
        for(int i=0;i<len;i++){
            if(dumpA.get(i).get(i)==0){
                List<Double> tempA=new ArrayList(dumpA.get(i));
                List<Double> tempE=new ArrayList(E.get(i));
                for(int j=i+1;j<len;j++){
                    if(dumpA.get(j).get(i)!=0){
                        dumpA.set(i,dumpA.get(j));
                        dumpA.set(j,tempA);
                        E.set(i,E.get(j));
                        E.set(j,tempE);
                        break;
                    }
                }
            }
            if(dumpA.get(i).get(i)<0){
                for(int j=0;j<len;j++){
                    dumpA.get(i).set(j,-1*dumpA.get(i).get(j));
                    E.get(i).set(j,-1*E.get(i).get(j));
                }
            }
            
            //solver----
            List<Double> row=new ArrayList(dumpA.get(i));
            for(Double d:E.get(i)) row.add(d);
            for(int j=0;j<dumpA.size();j++){
                double k; 
                //row.addAll(dumpA.get(i));
                if(i!=j){
                    k=-1*dumpA.get(j).get(i)/row.get(i);
                    if(k!=1){
                        for(int m=0;m<row.size();m++){
                            if(m<dumpA.get(0).size())
                                dumpA.get(j).set(m,k*row.get(m)+dumpA.get(j).get(m));
                            else
                                E.get(j).set(m-dumpA.size(),row.get(m)*k+E.get(j).get(m-dumpA.size()));
                        }
                    }
                }else{
                    k=1/dumpA.get(j).get(i);
                    if(k!=1){
                        for(int m=0;m<row.size();m++){
                            if(m<dumpA.size())
                                dumpA.get(j).set(m,dumpA.get(j).get(m)*k);
                            else
                                E.get(j).set(m-dumpA.size(),E.get(j).get(m-dumpA.size())*k);
                        }
                    }
                }
            }
            //??????????
        }
        //--------
        return(E); */
        
        
    }
    
    public static List<List<StringGraph>> invMatrSymb(List<List<StringGraph>> input) {
        if(input.size()==1&&input.get(0).size()==1){
            List<List<StringGraph>> out=new ArrayList();
            out.add(new ArrayList());
            out.get(0).add(StringGraph.devide(1,input.get(0).get(0)));
            return out;
        }else
            return mulElWise_s(adjMatr_s(input),StringGraph.devide(1,det_s(input)));
//        if(input.size()==1){
//            List<List<StringGraph>> out=new ArrayList();
//            out.add(new ArrayList());
//            out.get(0).add(StringGraph.devide(1, input.get(0).get(0)));
//            return out;
//        }else{        
//            List<List<StringGraph>> A=new ArrayList(),E=new ArrayList();
//            int len=input.size();
//            for(List<StringGraph> row:input){
//                A.add(new ArrayList());
//                E.add(new ArrayList());
//                for(StringGraph a:row){
//                    A.get(A.size()-1).add(new StringGraph(a));
//                    E.get(E.size()-1).add(new StringGraph(0));
//                }
//                E.get(E.size()-1).set(E.size()-1, new StringGraph(1));
//            }
//
//
//            for(int i=0;i<len;i++){
//                //checks
//                if(A.get(i).get(i).getRoot() instanceof Const){
//                    if(((Const)A.get(i).get(i).getRoot()).getValue()==0){
//                        //find non-zero line
//                        List<StringGraph> tempA=A.get(i);
//                        List<StringGraph> tempE=E.get(i);
//                        for(int j=i+1;j<len;j++){
//                            if(A.get(j).get(i).getRoot() instanceof Const){
//                                if(((Const)A.get(j).get(i).getRoot()).getValue()!=0){
////                                    A.set(i,A.get(j));
////                                    A.set(j,tempA);
////                                    E.set(i,E.get(j));
////                                    E.set(j,tempE);
////                                    break;
//                                    for(int m=0;m<len;m++){
//                                        A.get(i).get(m).add(A.get(j).get(m));
//                                        E.get(i).get(m).add(E.get(j).get(m));
//                                    }
//                                }
//                            }else{  // NE FACT!!!!
////                                A.set(i,A.get(j));
////                                A.set(j,tempA);
////                                E.set(i,E.get(j));
////                                E.set(j,tempE);
////                                break;
//                                for(int m=0;m<len;m++){
//                                    A.get(i).get(m).add(A.get(j).get(m));
//                                    E.get(i).get(m).add(E.get(j).get(m));
//                                }
//                            }
//                        }
//                    }
//                }
//
//                List<StringGraph> row=new ArrayList();
//                for(int j=0;j<len;j++){
//                    row.add(new StringGraph(A.get(i).get(j)));
//                }
//                for(int j=0;j<len;j++){
//                    row.add(new StringGraph(E.get(i).get(j)));
//                }
//                for(int j=0;j<len;j++){
//                    StringGraph k; 
//                    //row.addAll(dumpA.get(i));
//                    if(i!=j){
//                        k=StringGraph.mul(StringGraph.mul(A.get(j).get(i), row.get(i), -1),-1);
//                        for(int m=0;m<row.size();m++){
//                            if(m<len)
//                                A.get(j).set(m,StringGraph.sum(StringGraph.mul(row.get(m),k,1),A.get(j).get(m)));
//                            else
//                                E.get(j).set(m-len,StringGraph.sum(StringGraph.mul(row.get(m),k,1),E.get(j).get(m-len)));
//                        }
//    //                    for(int m=i;m<len;m++){
//    //                        A.get(j).set(m,StringGraph.sum(StringGraph.mul(row.get(m),k,1),A.get(j).get(m)));
//    //                        E.get(j).set(m,StringGraph.sum(StringGraph.mul(row.get(m+len),k,1),E.get(j).get(m)));
//    //                    }
//                    }else{
//                        k=StringGraph.devide(1,A.get(j).get(i));
//    //                    if(k. instanceof ){// k!=1
//    //                        
//    //                    }  
//                        for(int m=0;m<row.size();m++){
//                            if(m<len)
//                                A.get(j).set(m,StringGraph.mul(A.get(j).get(m),k,1));
//                            else
//                                E.get(j).set(m-len,StringGraph.mul(E.get(j).get(m-len),k,1));
//                        }
//    //                    for(int m=i;m<len;m++){
//    //                        A.get(j).set(m,StringGraph.mul(A.get(j).get(m),k,1));
//    //                        E.get(j).set(m,StringGraph.mul(E.get(j).get(m),k,1));
//    //                    }
//                    }
//                }
//            }
//
//            for(List<StringGraph> row:E)
//                for(StringGraph ph:row)
//                    ph.simplify();
//
//            return E;
//        }
    }
    
    public static List<List<Double>> transpose(List<List<Double>> input){
        int m=input.size();
        if(m<1) throw new Error("Empty Matrix(NxM)! N must be greater than 0");
        int n=input.get(0).size();
        if(n<1) throw new Error("Empty Matrix(NxM)! M must be greater than 0");
        List<List<Double>> out=new ArrayList(n);
        for(int i=0;i<n;i++){
            out.add(new ArrayList(m));
            for(int j=0;j<m;j++){
                out.get(i).add(input.get(j).get(i));
            }
        }
        return out;
    }
    
    public static List<List<StringGraph>> transpose_s(List<List<StringGraph>> input){
        int m=input.size();
        if(m<1) throw new Error("Empty Matrix(NxM)! N must be greater than 0");
        int n=input.get(0).size();
        if(n<1) throw new Error("Empty Matrix(NxM)! M must be greater than 0");
        List<List<StringGraph>> out=new ArrayList(n);
        for(int i=0;i<n;i++){
            out.add(new ArrayList(m));
            for(int j=0;j<m;j++){
                out.get(i).add(input.get(j).get(i));
            }
        }
        return out;
    }
    
    public static List<List<Double>> adjMatr(List<List<Double>> inp){
        List<List<Double>> out=new ArrayList(inp.size());
        int nRows=inp.size(),nCols=inp.get(0).size();
        if(nCols!=nRows) throw new Error("Bad matrix! N != M");
        for(int i=0;i<nRows;i++){
            out.add(new ArrayList(nCols));
            for(int j=0;j<nCols;j++){
                double k=1.0-2.0*((double)((i+j)%2));
                List<List<Double>> M=new ArrayList(nRows);
                for(List<Double> row:inp) M.add(new ArrayList(row));
                M.remove(i);
                for(int m=0;m<M.size();m++) M.get(m).remove(j);
                out.get(i).add(k*det(M));
            }
//            System.out.println(out.get(i).toString());
        }
        return transpose(out);
    }
    
    public static List<List<StringGraph>> adjMatr_s(List<List<StringGraph>> inp){
        List<List<StringGraph>> out=new ArrayList(inp.size());
        int nRows=inp.size(),nCols=inp.get(0).size();
        if(nCols!=nRows) throw new Error("Bad matrix! N != M");
        for(int i=0;i<nRows;i++){
            out.add(new ArrayList(nCols));
            for(int j=0;j<nCols;j++){
                double k=1.0-2.0*((double)((i+j)%2));
                List<List<StringGraph>> M=new ArrayList(nRows);
                for(List<StringGraph> row:inp) M.add(new ArrayList(row));
                M.remove(i);
                for(int m=0;m<M.size();m++) M.get(m).remove(j);
                out.get(i).add(StringGraph.mul(det_s(M), (int) k));
            }
//            System.out.println(out.get(i).toString());
        }
        return transpose_s(out);
    }
    
    public static int[][] getPotentialsMap(List<Wire> Wires, List<ShemeElement> Elems) throws Exception{
        if(Wires.isEmpty()&&Elems.isEmpty()) return null;
        List<ElemPin> conts=new ArrayList();
        int rowLength=0;
        for(ShemeElement elem:Elems){
            conts.addAll(elem.getElemContactList());
            rowLength+=elem.getElemContactList().size();
        }
        int columnLength=0;
        for(Wire wire:Wires){
            int rank=wire.getRank();
            columnLength+=rank-1;
        }
        boolean flag=true;
        for(ShemeElement elem:Elems){
            if(elem instanceof Elements.ElectricalRefference){
                flag=false;
                break;
//                columnLength++;//????
            }
        }
        if(flag)
            columnLength++;         //for zero poten?
        int[][] out=new int[columnLength][rowLength];
        int row=0;
        for(Wire wire:Wires){
            ElemPin mainPapa=wire.getWireContacts().get(0).getElemContact();
            for(int j=1;j<wire.getWireContacts().size();j++){
                ElemPin papa=wire.getWireContacts().get(j).getElemContact();
                out[row][conts.indexOf(mainPapa)]=1;
                out[row][conts.indexOf(papa)]=-1;
                row++;
            }                    
        }        
        if(flag)
            out[row][0]=1;
//        else{
//            for(ShemeElement shE:Elems){
//                if(shE instanceof Elements.ElectricalRefference){
//                    out[row][conts.indexOf(shE.getElemContactList().get(0))]=1;
//                    row++;
//                }
//            }
//        }
        return(out);
    }
    
    public static int[][] getCurrentsMap(List<Wire> Wires, List<ShemeElement> Elems){
        if(Wires.isEmpty()&&Elems.isEmpty()) return null;
        List<ElemPin> conts=new ArrayList();
        int rowLength=0,columnLength=0;
        
        //Рассчет размерности
        for(ShemeElement elem:Elems){
//            columnLength+=1;
            conts.addAll(elem.getElemContactList());
            rowLength+=elem.getElemContactList().size();
        }        
        columnLength+=Wires.size()-1;
        
        //Есть ли "земли"
        boolean flag=false;
        for(ShemeElement elem:Elems){
            if(elem instanceof Elements.ElectricalRefference){
                flag=true;
            }
        }
        if(flag)
            columnLength++; //strogo govorya xz
        
        
        
        int[][] out=new int[columnLength][rowLength];
        int row=0;
        
        //Сумма узловых токов
        for(Wire wire:Wires.subList(0, Wires.size()-1)){
            for(WireMarker wc:wire.getWireContacts()){
                ElemPin papa=wc.getElemContact();
                out[row][conts.indexOf(papa)]=1;
            }
            row++;
        }
        //Сумма токов в элементах (???????? не факт, например ДПТНВ)
        for(ShemeElement shE:Elems){
//            int len=shE.getElemContactList().size();
            int ind=conts.indexOf(shE.getElemContactList().get(0));            
            if(shE instanceof Elements.ElectricalRefference){
                out[columnLength-1][ind]=1;
            }else{
//                for(int i=0;i<len;i++){
//                    out[row][ind+i]=1;
//                }
//                row++;
            }
        }
        return(out);
    }
    
    /**
     * Index starts from 0.
     * @param row
     * @return ind of solo elem or -1, if elems more than one.
     */
    public static int getSingleInd(List<Integer> row){
        int cnt=0,indx=-1,j=0;
        for(int i:row){
            if(i!=0){
                cnt++;
                indx=j;
                if(cnt==2){
                    return -1;
                }
            }
            j++;
        }
        if(cnt==1){
            return indx;
        }else return -1;
    }
    
    /**
     * for +/- 1 matrix only
     * @param system
     * @param ans
     * @return 
     */
    public static List<StringGraph> solve(List<List<Integer>> system, List<StringGraph> ans){
        List<StringGraph> answer=new ArrayList();
        List<List<Integer>> A = initIntegerMatr(system);
        
        for(int i=0;i<A.size();i++){
            if(A.get(i).get(i)==0){
                for(int j=i+1;j<A.size();j++){
                    if(A.get(j).get(i)!=0){
                        for(int k=0;k<A.size();k++){
                            int temp=A.get(i).get(k);
                            A.get(i).set(k,A.get(j).get(k));
                            A.get(j).set(k,temp);
                        }
                        StringGraph temp=ans.get(i);
                        ans.set(i,ans.get(j));
                        ans.set(j,temp);
                        break;
                    }
                }
            }
            //solver----
            for(int j=0;j<A.size();j++){
                int k;
                List<Integer> row=new ArrayList();
                row.addAll(A.get(i));
                StringGraph right=ans.get(i);
                if(i!=j){
                    k=-1*A.get(j).get(i)/A.get(i).get(i);
                    if(k!=0.0){
                        for(int m=0;m<row.size();m++){
                            row.set(m, row.get(m)*k);
                            A.get(j).set(m, row.get(m)+A.get(j).get(m));
                        }
                        ans.set(j, StringGraph.sum(ans.get(j),StringGraph.mul(right, k)));
                    }
                }else{
                    k=1/A.get(j).get(i);
                    if(k!=1){
                        for(int m=0;m<row.size();m++){
                            A.get(j).set(m, A.get(j).get(m)*k);  
                        }
                        ans.set(j, StringGraph.mul(ans.get(j), k));
                    }
                }
                //??????????
            }
            //--------
        }
        ans.forEach(data->{
            answer.add(new StringGraph(data));
        });
        return answer;
    }
    
    public static List<List<Double>> evalSymbMatr(List<List<StringGraph>> inp, WorkSpace vars,List<MathInPin> extInput){
        List<List<Double>> out=new ArrayList(inp.size());
        for(int i=0;i<inp.size();i++){
            List<StringGraph> row=inp.get(i);
            out.add(new ArrayList(row.size()));
            for(StringGraph sg:row){
                out.get(i).add(sg.evaluate(vars, extInput));
            }
        }
        return out;
    }
    
    public static List<Double> evalSymbRow(List<StringGraph> inp, WorkSpace vars,List<MathInPin> extInput){
        List<Double> out=new ArrayList(inp.size());
        for(int i=0;i<inp.size();i++){
            StringGraph row=inp.get(i);
            out.add(row.evaluate(vars, extInput));
        }
        return out;
    }
    
    public static int rank(List<List<Integer>> inp){
        int nRow=inp.size(),nCol=inp.get(0).size(),rnk=0;
        int[][] A=listToArray(inp);
        for(int i=0;i<nRow;i++){ //main cycle
            if(A[i][i]==0){
                //swap lines
                for(int j=i+1;j<nRow;j++){ //cycle by rows under current
                    if(A[j][i]!=0){
                        for(int q=0;q<nCol;q++){
                            int tmp=A[j][q];
                            A[j][q]=A[i][q];
                            A[i][q]=tmp;
                        }
                        break;
                    }
                }
            }
            for(int j=i+1;j<nRow;j++){ //cycle by rows under current
                if(A[j][i]!=0){
                    int k=A[j][i]/A[i][i];
                    if(A[i][i]>A[j][i]) System.err.println("AAAAAAaa k="+k+"!!");
                    for(int q=i;q<nCol;q++){ //cycle by length of row
                        A[j][q]=A[j][q]-A[i][q]*k;
                    }
                }                    
            }
        }
        //layout
//        for(int i=0;i<nRow;i++){
//            for(int j=0;j<nCol;j++){
//                System.out.print(A[i][j]+"   ");
//            }
//            System.out.print("\n");
//        }
        
        for(int i=0;i<nRow;i++){
            for(int j=i;j<nCol;j++){
                if(A[i][j]!=0){
                    rnk++;
                    break;
                }
            }
        }
        return rnk;
    }
    
    public static int[][] listToArray(List<List<Integer>> inp){
        int nRow=inp.size(),nCol=inp.get(0).size();
        int[][] A=new int[nRow][nCol];
        for(int i=0;i<nRow;i++){
            for(int j=0;j<nCol;j++){
                A[i][j]=inp.get(i).get(j);
            }
        }
        return A;
    }
    
    public static List<List<Integer>> ArrayTolist(int[][] inp){
        int nRow=inp.length,nCol=inp[0].length;
        List<List<Integer>> A=new ArrayList(nRow);
        for(int i=0;i<nRow;i++){
            A.add(new ArrayList(nCol));
            for(int j=0;j<nCol;j++){
                A.get(i).add(inp[i][j]);
            }
        }
        return A;
    }
}
