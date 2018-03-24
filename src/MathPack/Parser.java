/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Иван
 */
public class Parser {
    public static double[][] parse(String str){
        double[][] out=null;
        List<String> lines=new ArrayList<>();
        if(str.charAt(0)=='['){
            if(str.charAt(str.length()-1)==']'){
                str=str.substring(1,str.length()-1);
                String temp="";
                for(int i=0;i<str.length();i++){
                    char c=str.charAt(i);
                    if(c!=';')
                        temp+=c;
                    else {
                        lines.add(temp);
                        temp="";
                    }
                }
                if(!temp.isEmpty())
                    lines.add(temp);
            }else{
                throw new Error("Corrupted parameter! "+str);
            }
            int numberOfRows=lines.size();

            // now we have row of lines
            int i=0;
            Integer numberOfCols=null;
            for(String row:lines){
                String temp="";
                List<String> cols=new ArrayList<>();
                for(int j=0;j<row.length();j++){
                    char c=row.charAt(j);
                    if(c!=','&&c!=' '){
                        temp+=c;
                    }else{
                        cols.add(temp);
                        temp="";
                    }
                }
                if(!temp.isEmpty())
                    cols.add(temp);

                if(numberOfCols!=null){
                    if(!numberOfCols.equals(cols.size()))
                        throw new Error("Dimensions mismatch!\n Parameter:  line: "+cols.toString()+"\n in "+str);
                }else{
                    numberOfCols=Integer.valueOf(cols.size());
                    out=new double[numberOfRows][numberOfCols];
                }

                // cast to array
                int j=0;
                for(String col:cols){
                    out[i][j]= StringGraph.doubleValue(col);
                    j++;
                }
                i++;
            }

        }else{
            // scalar case
            out=new double[1][1];
            out[0][0]=StringGraph.doubleValue(str);
        }
//        if(this instanceof ScalarParameter){
//            if(out.length!=1&&out[0].length!=1)
//                throw new Error("Dimensions mismatch in . Must be a scalar.");
//        }else if(this instanceof VectorParameter){
//            if(out.length>1&&out[0].length>1) {
//                throw new Error("Dimensions mismatch in . Must be a vector (but matrix).");
//            }else {
//                if(out.length>1){
//                    // transpose
//                    out=MatrixEqu.transpose(out);
//                }
//            }
////                    if(out[0].length!=1)
////                        throw new Error("Dimensions mismatch in "+name+". Must be a vector.");
//        }
        return out;
    }

    public static double[] parseRow(String str){
        double[][] out=null;
        if(str==null)
            return null;
        if(str.isEmpty())
            return null;

        List<String> lines=new ArrayList<>();
        if(str.charAt(0)=='['){
            if(str.charAt(str.length()-1)==']'){
                str=str.substring(1,str.length()-1);
                if(str.isEmpty()){
                    return new double[0];
                }
                String temp="";
                for(int i=0;i<str.length();i++){
                    char c=str.charAt(i);
                    if(c!=';')
                        temp+=c;
                    else {
                            lines.add(temp);  // TODO "; " case!
                            temp = "";
                    }
                }
                if(!temp.isEmpty())
                    lines.add(temp);
            }else{
                throw new Error("Corrupted parameter! "+str);
            }
            int numberOfRows=lines.size();

            // now we have row of lines
            int i=0;
            Integer numberOfCols=null;
            for(String row:lines){
                StringBuilder temp= new StringBuilder();
                List<String> cols=new ArrayList<>();
                for(int j=0;j<row.length();j++){
                    char c=row.charAt(j);
                    if(c!=','&&c!=' '){
                        temp.append(c);
                    }else{
                        if(temp.length()!=0) {
                            cols.add(temp.toString());
                            temp = new StringBuilder();
                        }
                    }
                }
                String stmp=temp.toString();
                if(!stmp.isEmpty())
                    cols.add(stmp);

                if(numberOfCols!=null){
                    if(!numberOfCols.equals(cols.size()))
                        throw new Error("Dimensions mismatch!\n Parameter:  line: "+cols.toString()+"\n in "+str);
                }else{
                    numberOfCols=Integer.valueOf(cols.size());
                    out=new double[numberOfRows][numberOfCols];
                }

                // cast to array
                int j=0;
                for(String col:cols){
                    out[i][j]= StringGraph.doubleValue(col);
                    j++;
                }
                i++;
            }

        }else{
            // scalar case
            out=new double[1][1];
            out[0][0]=StringGraph.doubleValue(str);
        }

        if(out.length>1&&out[0].length>1) {
            throw new Error("Dimensions mismatch in . Must be a vector (but matrix).");
        }else {
            if(out.length>1){
                // transpose
                out=MatrixEqu.transpose(out);
            }
        }
//                    if(out[0].length!=1)
//                        throw new Error("Dimensions mismatch in "+name+". Must be a vector.");

        return out[0];
    }

    public static String getKeyValue(String[] lines,String key){
        String out=null;
        for(String str:lines){
            if(str.startsWith(key)){
                out=str.substring(key.length(),str.lastIndexOf("</"));
                break;
            }
        }
        return out;
    }

    public static String getKeyValue(String lines,String key){
        String out=null;
        Scanner sc=new Scanner(lines);
        sc.useDelimiter("\r\n");
        while(sc.hasNext()){
            String line=sc.next();
            if(line.startsWith(key)){
                out=line.substring(key.length(),line.lastIndexOf("</"));
                break;
            }
        }
        return out;
    }

    @Deprecated
    public static String getFirstKeyValue(String data,String key){
        String out=null,
                endKey="</"+key.substring(1);
        int start=data.indexOf(key)+key.length(),
        end=data.indexOf(endKey);
        if(end!=-1) {
            out = data.substring(start, end);
            return out;
        }else{
            return out;
        }
    }

    public static String getBlock(String data,String key){
        Scanner sc=new Scanner(data);
        sc.useDelimiter("\r\n");
        StringBuilder out=new StringBuilder();
        String endKey="</"+key.substring(1);
        while(sc.hasNext()){
            String line=sc.next();
            if(line.equals(key)){
                int cnt=0;
                while(sc.hasNext()){
                    line=sc.next();
                    if(line.equals(key)) {
                        cnt++;
                        out.append(line.substring(1)+"\r\n"); //remove one \t char
                    }else
                    if(line.equals(endKey)){
                        if(cnt==0)
                            return out.toString();
                        else{
                            out.append(line.substring(1)+"\r\n");
                            cnt--;
                        }
                    }else{
                        out.append(line.substring(1)+"\r\n");
                    }
                }
            }
        }
        return "";
    }

    public static String getBlock(java.nio.file.Path path,String key) throws IOException{
        Scanner sc=new Scanner(path);
        StringBuilder out=new StringBuilder();
        String endKey="</"+key.substring(1);
        sc.useDelimiter("\r\n");
        while(sc.hasNext()){
            String line=sc.next();
            if(line.equals(key)){
                int cnt=0;
                while(sc.hasNext()){
                    line=sc.next();
                    if(line.equals(key)) {
                        cnt++;
                        out.append(line.substring(1)+"\r\n"); //remove one \t char
                    }else
                    if(line.equals(endKey)){
                        if(cnt==0)
                            return out.toString();
                        else {
                            out.append(line.substring(1)+"\r\n");
                            cnt--;
                        }
                    }else{
                        out.append(line.substring(1)+"\r\n");
                    }
                }
            }
        }
        return null;
    }

    static public String formatBlock(String block){
        Scanner sc=new Scanner(block);
        StringBuilder sb=new StringBuilder();
        sc.useDelimiter("\r\n");
        int cnt=0;
        while(sc.hasNext()){
            String line=sc.next()+"\r\n";

            if(line.startsWith("</")){
                cnt--;
            }
            for(int i=0;i<cnt;i++){ //append
                line="\t"+line;
            }
            if(!line.contains("</")){
                cnt++;
            }

            sb.append(line);
        }
        return sb.toString();
    }
}

