package raschetkz;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Protection {
    private String unitName;
    private double nominalCurrent;

    public Protection(String unitName,double nominalCurrent){
        this.unitName=unitName;
        this.nominalCurrent=nominalCurrent;
    }

    public static Protection pickOne(double maxCurr){
        List<String> nameList=new ArrayList<>();
        List<Double> nominalCurr=new ArrayList<>();
        parse(nameList,nominalCurr);

        // picking
        int good=-1;
        double diff=Double.MAX_VALUE;
        for(int i=0;i<nominalCurr.size();i++){
            double cVal=nominalCurr.get(i);
            double newDiff=maxCurr-cVal;
            if(newDiff>0&&newDiff<diff) {
                diff = newDiff;
                good=i;
            }
        }

        return new Protection(nameList.get(good),nominalCurr.get(good));
    }

    private static void parse(List<String> name,List<Double> curr){
        try {
            String filePath = new File("").getAbsolutePath();
            File f = new File(filePath+"/src/raschetkz/catalog.txt");
            FileReader fr=new FileReader(f);
            Scanner scanner=new Scanner(fr);
            while (scanner.hasNextLine()){
                String str=scanner.nextLine();

                //read unit name
                int nameIndx=str.indexOf(',');
                String unitname=str.substring(0,nameIndx);
                str=str.substring(nameIndx+1);

                //read nominal cur
                String curVal=str.replaceAll(" ","");

                // add to lists
                name.add(unitname);
                curr.add(Double.parseDouble(curVal));
            }
        }catch (IOException ex){
            ex.printStackTrace(System.err);
        }
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public double getNominalCurrent() {
        return nominalCurrent;
    }

    public void setNominalCurrent(double nominalCurrent) {
        this.nominalCurrent = nominalCurrent;
    }
}
