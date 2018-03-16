package raschetkz;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Protection {
    private String unitName;
    private double nominalCurrent;

    public Protection(){

    }

    public Protection pickOne(double maxCurr){
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

        unitName=nameList.get(good);
        nominalCurrent=nominalCurr.get(good);

        return this;
    }

    private void parse(List<String> name,List<Double> curr){

        InputStream is=getClass().getClassLoader().getResourceAsStream("raschetkz/catalog.txt");
        Scanner scanner=new Scanner(is);
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
