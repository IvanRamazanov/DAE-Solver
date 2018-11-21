package MathPack;

import java.util.ArrayList;
import java.util.List;

public class CompilerDataODE {
    private List<StringFunctionSystem> funcList;
    private List<int[][]> potM;
    private List<int[][]> currM;
    private int numOfDomains;
    private String logFile;
    private Boolean isLogNeeded;

    public CompilerDataODE(String logFile,Boolean isLogNedded,List<StringFunctionSystem> funcList,
                    int[][]... matrix){
        int len=matrix.length;
        if(len%2!=0 || len<2){
            throw new Error("Wrong number of input matrices! Must be even, now: "+matrix.length);
        }else{
            this.logFile=logFile;
            this.isLogNeeded=isLogNedded;
            numOfDomains=0;
            this.funcList=funcList;
            //potentials of any kind
            potM=new ArrayList<>();
            for(int i=0;i<len;i+=2)
                potM.add(matrix[i]);
            // currents of any kind
            currM=new ArrayList<>();
            for(int i=1;i<len;i+=2) {
                currM.add(matrix[i]);
                numOfDomains++;
            }
        }
    }

    public List<StringFunctionSystem> getFuncList() {
        return funcList;
    }

    public int[][] getPotM(int index) {
        return potM.get(index);
    }

    public int[][] getCurrM(int index) {
        return currM.get(index);
    }

    public int getNumOfDomains() {
        return numOfDomains;
    }

    public String getLogFile() {
        return logFile;
    }

    public Boolean getLogNeeded() {
        return isLogNeeded;
    }
}
