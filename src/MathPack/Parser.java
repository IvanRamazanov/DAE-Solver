/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPack;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

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
        String out=null,keyFormatted="";

        // format
        if(key.startsWith("<")&&key.endsWith(">"))
            keyFormatted=key;
        else
            keyFormatted="<"+key+">";

        Scanner sc=new Scanner(lines);
        sc.useDelimiter("\r\n");
        while(sc.hasNext()){
            String line=sc.next();
            if(line.startsWith(keyFormatted)){
                out=line.substring(keyFormatted.length(),line.lastIndexOf("</"));
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

    public static String rgbToHash(String rgb){
        String R,G,B;
        int r,g,b;
        char divider='.';
        int i=rgb.indexOf(divider);
        if(i==-1) {
            divider = ',';
            if ((i = rgb.indexOf(divider)) == -1)
                throw new Error("Wrong RGB formatting in: " + rgb);
        }
        R=rgb.substring(0,i);
        G=rgb.substring(i+1);
        i=G.indexOf(divider);
        if(i==-1)
            throw new Error("Wrong RGB formatting in:" +rgb);
        B=G.substring(i+1);
        G=G.substring(0,i);
        try{
            r=Integer.parseInt(R);
            g=Integer.parseInt(G);
            b=Integer.parseInt(B);
            StringBuilder out=new StringBuilder("#");
            //R
            if(r<16)
                out.append("0"+Integer.toHexString(r));
            else if(r<256)
                out.append(Integer.toHexString(r));
            else
                throw new Error("R in rgb greater than 255!: "+rgb);

            //G
            if(g<16)
                out.append("0"+Integer.toHexString(g));
            else if(g<256)
                out.append(Integer.toHexString(g));
            else
                throw new Error("G in rgb greater than 255!: "+rgb);

            //B
            if(b<16)
                out.append("0"+Integer.toHexString(b));
            else if(b<256)
                out.append(Integer.toHexString(b));
            else
                throw new Error("B in rgb greater than 255!: "+rgb);

            //output
            return out.toString();
        }catch(Exception ex){
            System.err.print(ex.getMessage());
        }
        return null;
    }

    public static Shape getShape(String shape){
        if(shape.equals("Circle")){
            return new Circle(4);
        }else if(shape.equals("Rectangle")){
            return new Rectangle(4,4);
        }
        else{
            return new Polygon(parseRow(shape));
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
                        out.append(removeTab(line)+"\r\n"); //remove one \t char
                    }else
                    if(line.equals(endKey)){
                        if(cnt==0)
                            return out.toString();
                        else{
                            out.append(removeTab(line)+"\r\n");
                            cnt--;
                        }
                    }else{
                        out.append(removeTab(line)+"\r\n");
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
                        out.append(removeTab(line)+"\r\n"); //remove one \t char
                    }else
                    if(line.equals(endKey)){
                        if(cnt==0)
                            return out.toString();
                        else {
                            out.append(removeTab(line)+"\r\n");
                            cnt--;
                        }
                    }else{
                        out.append(removeTab(line)+"\r\n");
                    }
                }
            }
        }
        return null;
    }

    public static List<String> getBlockList(String stream){
        Scanner sc=new Scanner(stream);
        StringBuilder out=new StringBuilder();
        List<String> output=new ArrayList<>(2);
        sc.useDelimiter("\r\n");
        while(sc.hasNext()){
            String blockName=sc.next();
            if(blockName.startsWith("\t")){
                throw new Error("Wrong block formatting!");
            }
            String endKey=blockName.substring(0,1)+"/"+blockName.substring(1);

            //for each block
            while(sc.hasNext()){
                String line=sc.next();
                if(line.equals(endKey)){
                    output.add(out.toString());
                    out.setLength(0);
                    break;
                }else{
                    out.append(removeTab(line)+"\r\n"); //remove one \t char
                }
            }
        }
        return output;
    }

    public static String removeTab(String in){
        if(in.startsWith("\t")){
            return in.substring(1);
        }else if(in.startsWith("    ")){
            return in.substring(4);
        }else{
            throw new Error("No tabulation in: "+in);
        }
    }

    public static String getBlock(java.io.InputStream stream,String key) throws IOException{
        Scanner sc=new Scanner(stream);
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
                        out.append(removeTab(line)+"\r\n"); //remove one \t char
                    }else
                    if(line.equals(endKey)){
                        if(cnt==0)
                            return out.toString();
                        else {
                            out.append(removeTab(line)+"\r\n");
                            cnt--;
                        }
                    }else{
                        out.append(removeTab(line)+"\r\n");
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

