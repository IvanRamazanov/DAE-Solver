/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MathPack;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Иван
 */
public class MathFunction {
    private int rank;
    private SimpleFunc function;
    private String name;
    
    public MathFunction(MathFunction input){
        this.rank=input.rank;
        this.function=input.function.copy();
        this.name=input.name;
    }
    
    public MathFunction(String functionName){
        this.name=functionName;
        switch(functionName){
            case "+":
                rank=-1;
                function=new Summa();
                break;
            case "sin":
                rank=1;
                function=new Sin();
                break;
//            case "-":
//                rank=-1;
//                function=new Raznost();
//                break;
            case "*":
                rank=-1;
                function=new Multiplex();
                break;
//            case "/":
//                rank=-1;
//                function=new Divide();
//                break;
            case "arcSin":
                rank=1;
                function=new ArcSin();
                break;
            case "exp":
                rank=1;
                function=new Exp();
                break;
            case "logn":
                rank=1;
                function=new Logn();
                break;
            case "gr":
                rank=2;
                function=new GreatThan();
                break;
            case "if":
                rank=3;
                function=new If();
                break;
            case "inv":
                rank=1;
                function=new Inverse();
                break;
            case "cos":
                rank=1;
                function=new Cos();
                break;
            case "pow":
                rank=2;
                function=new Pow();
                break;
            default:
                throw new Error("Unsupported math function!");
        }
            
        
    }
    
    public void inverse(int index){
        function=function.inverse(index);
    }
    
    public String getFuncName(){
        return this.name;
    }
    
    public String getFuncName(int i){
        return this.function.getName(i);
    }
    
    Uzel simplify(FuncUzel inp){
        return function.simplify(inp);
    }
    
    public double Elavuate(List<Integer> gains,double... input){
        return function.Evaluate(gains,input);
    }
    
    public static double fval(String name, double... inp){
        switch(name){
            case "olo":
                
        }
        return 1;
    }
    
    public int getRank(){
        return rank;
    }
    
    Uzel differ(FuncUzel root,String varName){
        return function.differ(root,varName);
    }
    
    boolean contain(List<Uzel> inp,String varName){
        int[] indexes=this.function.getRequiredIndexes();
        boolean out=false;
        if(indexes[0]!=-1){
            for(int i:indexes){
                if(inp.get(i).contains(varName)) {out=true;break;}
            }
        }else{
            for(Uzel uz:inp){
                if(uz.contains(varName)) {out=true;break;}
            }
        }
        return out;
    }

}

interface SimpleFunc{
    
    double Evaluate(List<Integer> gains,double... input);
    
    /**
     * Return inverse function rely on operand index
     * @param index
     * @return 
     */
    SimpleFunc inverse(int index);
    
    SimpleFunc copy();
    
    String getName(int i);
    
    Uzel simplify(FuncUzel uz);
    
    int[] getRequiredIndexes();
    
    Uzel differ(FuncUzel root,String varName);
}

class Summa implements SimpleFunc{
    
    @Override
    public double Evaluate(List<Integer> gains,double... input) {
        double output=0;
        for(int i=0;i<input.length;i++){
            if(gains.get(i)==1){
                output+=input[i];
            }else{
                output-=input[i];
            }
        }
        return output;
    }
    
    @Override
    public SimpleFunc inverse(int index){
        return new Raznost();
    }
    
    @Override
    public Uzel simplify(FuncUzel input){
        Uzel output;
        List<Uzel> inps=input.getInputs();
        List<Integer> gains=input.getGain();
        int len=inps.size();
        
        //simplify inputs
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                inps.set(i,((FuncUzel)uz).simplify());
            }
        }
        
        //gathering
        for(int i=0;i<len;i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                FuncUzel fuz=(FuncUzel)uz;
                if(fuz.getFuncName().equals("+")){
                    len+=fuz.getInputs().size()-1;
                    for(Uzel innerUz:fuz.getInputs()){
                        inps.add(innerUz);
                    }
                    for(int gain:fuz.getGain()){
                        gains.add(gain*gains.get(i));
                    }
                    inps.remove(i);
                    gains.remove(i);
                    i--;
                }
            }
        }
        
        //collecting
        for(int i=0;i<len;i++){
            if(inps.get(i) instanceof FuncUzel){
                FuncUzel fuz=(FuncUzel)inps.get(i);
                if(fuz.getFuncName().equals("*")){ //mb N*f()*g()+M*f()*g()=(N+M)*f()*g() !!!!!
                    if(fuz.getInputs().size()==2){
                        if((fuz.getInputs().get(0) instanceof Const)^(fuz.getInputs().get(1) instanceof Const)){
                            String var;
                            double mul;
                            int invgan;
                            Uzel link;
                            if(fuz.getInputs().get(0) instanceof Const){
                                var=fuz.getInputs().get(1).toString();
                                mul=Math.pow(((Const)fuz.getInputs().get(0)).getValue(),fuz.getGain().get(0));
                                invgan=fuz.getGain().get(1);
                                link=fuz.getInputs().get(1);
                            }else{
                                var=fuz.getInputs().get(0).toString();
                                mul=Math.pow(((Const)fuz.getInputs().get(1)).getValue(),fuz.getGain().get(1));
                                invgan=fuz.getGain().get(0);
                                link=fuz.getInputs().get(0);
                            }
                            for(int j=i+1;j<len;j++){
                                if(inps.get(j) instanceof FuncUzel){
                                    FuncUzel another=(FuncUzel)inps.get(j);
                                    if(another.getFuncName().equals("*")){
                                        if(another.getInputs().size()==2){
                                            if((another.getInputs().get(0) instanceof Const)
                                                    ^(another.getInputs().get(1) instanceof Const)){
                                                String avar;
                                                double amul;
                                                int agan;
                                                if(another.getInputs().get(0) instanceof Const){
                                                    avar=another.getInputs().get(1).toString();
                                                    amul=Math.pow(((Const)another.getInputs().get(0)).getValue(),another.getGain().get(0));
                                                    agan=another.getGain().get(1);
                                                }else{
                                                    avar=another.getInputs().get(0).toString();
                                                    amul=Math.pow(((Const)another.getInputs().get(1)).getValue(),another.getGain().get(1));
                                                    agan=another.getGain().get(0);
                                                }
                                                if(agan==invgan&&var.equals(avar)){
                                                    if(gains.get(i)*mul+gains.get(j)*amul==0){
                                                        inps.remove(j);
                                                        inps.remove(i);
                                                        gains.remove(j);
                                                        gains.remove(i);
                                                        len-=2;
                                                        i--;
                                                        break;
                                                    }else{
                                                        gains.add(1);
                                                        List<Uzel> newUz=new ArrayList();
                                                        newUz.add(new Const(gains.get(i)*mul+gains.get(j)*amul));
                                                        newUz.add(link);
                                                        List<Integer> newGn=new ArrayList();
                                                        newGn.add(1);
                                                        newGn.add(agan);
                                                        inps.add(new FuncUzel("*",newUz,newGn));
                                                        inps.remove(j);
                                                        inps.remove(i);
                                                        gains.remove(j);
                                                        gains.remove(i);
                                                        len--;
                                                        i--;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }else{
                                        String avar=inps.get(j).toString();
                                        if(invgan==1&&avar.equals(var)){
                                            if(gains.get(j)+gains.get(i)*mul==0){
                                                inps.remove(j);
                                                inps.remove(i);
                                                gains.remove(j);
                                                gains.remove(i);
                                                len-=2;
                                                i--;
                                                break;
                                            }else{
                                                gains.add(1);
                                                inps.add(new FuncUzel("*",inps.get(j),new Const(gains.get(j)+gains.get(i)*mul)));
                                                inps.remove(j);
                                                inps.remove(i);
                                                gains.remove(j);
                                                gains.remove(i);
                                                len--;
                                                i--;
                                                break;
                                            }
                                        }
                                    }
                                }else{
                                    String avar=inps.get(j).toString();
                                    if(invgan==1&&avar.equals(var)){
                                        if(gains.get(j)+gains.get(i)*mul==0){
                                            inps.remove(j);
                                            inps.remove(i);
                                            gains.remove(j);
                                            gains.remove(i);
                                            len-=2;
                                            i--;
                                            break;
                                        }else{
                                            gains.add(1);
                                            inps.add(new FuncUzel("*",inps.get(j),new Const(gains.get(j)+gains.get(i)*mul)));
                                            inps.remove(j);
                                            inps.remove(i);
                                            gains.remove(j);
                                            gains.remove(i);
                                            len--;
                                            i--;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else{
                    String var=inps.get(i).toString();
                    int gan=gains.get(i);
                    for(int j=i+1;j<len;j++){
                        if(inps.get(j) instanceof FuncUzel){
                            FuncUzel another=((FuncUzel)inps.get(j));
                            if(another.getFuncName().equals("*")){
                                if(another.getInputs().size()==2){
                                    if((another.getInputs().get(0) instanceof Const)^
                                            (another.getInputs().get(1) instanceof Const)){
                                        String avar;
                                        double mul;
                                        int agan;
                                        if(another.getInputs().get(0) instanceof Const){
                                            avar=another.getInputs().get(1).toString();
                                            mul=((Const)another.getInputs().get(0)).getValue();
                                            agan=another.getGain().get(1);
                                        }else{
                                            avar=another.getInputs().get(0).toString();
                                            mul=((Const)another.getInputs().get(1)).getValue();
                                            agan=another.getGain().get(0);
                                        }
                                        if(agan==1&&var.equals(avar)){
                                            if(gan+gains.get(j)*mul==0){
                                                inps.remove(j);
                                                inps.remove(i);
                                                gains.remove(j);
                                                gains.remove(i);
                                                len-=2;
                                                i--;
                                                break;
                                            }else{
                                                gains.add(1);
                                                inps.add(new FuncUzel("*",inps.get(i),new Const(gan+gains.get(j)*mul)));
                                                inps.remove(j);
                                                inps.remove(i);
                                                gains.remove(j);
                                                gains.remove(i);
                                                len--;
                                                i--;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }else{
                                if(another.toString().equals(var)){
                                    if(gains.get(j)+gan==0){
                                        inps.remove(j);
                                        gains.remove(j);
                                        inps.remove(i);
                                        gains.remove(i);
                                        len-=2;
                                        i--;
                                        break;
                                    }else{
                                        gains.add(1);
                                        inps.add(new FuncUzel("*",inps.get(i),new Const(gains.get(j)+gan)));
                                        inps.remove(j);
                                        gains.remove(j);
                                        inps.remove(i);
                                        gains.remove(i);
                                        len--;
                                        i--;
                                        break;
                                    }
                                }
                            }
                        }else{
                            if(inps.get(j).toString().equals(var)){
                                if(gan+gains.get(j)==0){
                                    inps.remove(j);
                                    gains.remove(j);
                                    inps.remove(i);
                                    gains.remove(i);
                                    len-=2;
                                    i--;
                                    break;
                                }else{
                                    gains.add(1);
                                    inps.add(new FuncUzel("*",inps.get(i),new Const(gan+gains.get(j))));
                                    inps.remove(j);
                                    gains.remove(j);
                                    inps.remove(i);
                                    gains.remove(i);
                                    len--;
                                    i--;
                                    break;
                                }
                            }
                        }
                    }
                }
            }else{
                if(inps.get(i) instanceof Const) continue;
                String var=inps.get(i).toString();
                int gan=gains.get(i);
                for(int j=i+1;j<len;j++){
                    if(inps.get(j) instanceof FuncUzel){
                        FuncUzel another=((FuncUzel)inps.get(j));
                        if(another.getFuncName().equals("*")){
                            if(another.getInputs().size()==2){
                                if((another.getInputs().get(0) instanceof Const)^
                                        (another.getInputs().get(1) instanceof Const)){
                                    String avar;
                                    double mul;
                                    int agan;
                                    if(another.getInputs().get(0) instanceof Const){
                                        avar=another.getInputs().get(1).toString();
                                        mul=((Const)another.getInputs().get(0)).getValue();
                                        agan=another.getGain().get(1);
                                    }else{
                                        avar=another.getInputs().get(0).toString();
                                        mul=((Const)another.getInputs().get(1)).getValue();
                                        agan=another.getGain().get(0);
                                    }
                                    if(agan==1&&var.equals(avar)){
                                        if(gan+gains.get(j)*mul==0){
                                            inps.remove(j);
                                            inps.remove(i);
                                            gains.remove(j);
                                            gains.remove(i);
                                            len-=2;
                                            i--;
                                            break;
                                        }else{
                                            gains.add(1);
                                            inps.add(new FuncUzel("*",inps.get(i),new Const(gan+gains.get(j)*mul)));
                                            inps.remove(j);
                                            inps.remove(i);
                                            gains.remove(j);
                                            gains.remove(i);
                                            len--;
                                            i--;
                                            break;
                                        }
                                    }
                                }
                            }
                        }else{
                            if(another.toString().equals(var)){
                                if(gains.get(j)+gan==0){
                                    inps.remove(j);
                                    gains.remove(j);
                                    inps.remove(i);
                                    gains.remove(i);
                                    len-=2;
                                    i--;
                                    break;
                                }else{
                                    gains.add(1);
                                    inps.add(new FuncUzel("*",inps.get(i),new Const(gains.get(j)+gan)));
                                    inps.remove(j);
                                    gains.remove(j);
                                    inps.remove(i);
                                    gains.remove(i);
                                    len--;
                                    i--;
                                    break;
                                }
                            }
                        }
                    }else{
                        if(inps.get(j).toString().equals(var)){
                            if(gan+gains.get(j)==0){
                                inps.remove(j);
                                gains.remove(j);
                                inps.remove(i);
                                gains.remove(i);
                                len-=2;
                                i--;
                                break;
                            }else{
                                gains.add(1);
                                inps.add(new FuncUzel("*",inps.get(i),new Const(gan+gains.get(j))));
                                inps.remove(j);
                                gains.remove(j);
                                inps.remove(i);
                                gains.remove(i);
                                len--;
                                i--;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if(inps.isEmpty()){
            return new Const(0);
        }
        
        //convert -1*k=-k
        for(int i=0;i<inps.size();i++){
            if(inps.get(i) instanceof FuncUzel){
                FuncUzel uz=(FuncUzel)inps.get(i);
                if(uz.getFuncName().equals("*")){
                    List<Uzel> uzls=uz.getInputs();
                    for(int j=0;j<uzls.size();j++){
                        if(uzls.get(j) instanceof Const){
                            if(((Const)uzls.get(j)).getValue()<0.0){
                                gains.set(i, -1*gains.get(i));
                                uzls.set(j,new Const(((Const)uzls.get(j)).getValue()*-1.0));
                                inps.set(i, uz.simplify());
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        // decrease consts
        int numOfConsts=0;
        double[] inputs=new double[inps.size()];
        int[]   indxs=new int[inps.size()];
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof Const){
                inputs[numOfConsts]=((Const)uz).getValue();
                indxs[numOfConsts]=i;
                numOfConsts++;
            }
        }
        if(numOfConsts==inps.size()){
            output=new Const(Evaluate(gains, inputs));
        }else if(inps.size()==1){
            if(gains.get(0)==1)
                output=inps.get(0);
            else
                output=input;
        }else if(numOfConsts==1){
            if(((Const)inps.get(indxs[0])).getValue()==0.0){
                inps.remove(indxs[0]);
                gains.remove(indxs[0]);
            }
            output=input;
        }else{
            double val=0;
            for(int i=0;i<numOfConsts;i++){
                val+=gains.get(indxs[i])*inputs[i];
            }
            if(val!=0.0){
                inps.add(new Const(val));
                gains.add(1);
            }
            for(int i=numOfConsts-1;i>=0;i--){
                inps.remove(indxs[i]);
                gains.remove(indxs[i]);
            }
            output=input;
        }
        
        return output;
    }
    
    @Override
    public SimpleFunc copy(){
        return new Summa();
    }
    
    @Override
    public String getName(int i){
        if(i==1){
            return "+";
        }else{
            return "-";
        }
    }
    
    @Override
    public int[] getRequiredIndexes(){
        int[] out={-1};
        return out;
    }
    
    @Override
    public Uzel differ(FuncUzel root,String varName){
        List<Uzel> inp=new ArrayList();
        for(int i=0;i<root.getInputs().size();i++){
            inp.add(root.getInputs().get(i).differ(varName));
        }
        FuncUzel out=new FuncUzel("+",inp,root.getGain());
        return out.simplify();
//        for(int i=root.getInputs().size()-1;i>=0;i--){
//            root.getInputs().set(i,root.getInputs().get(i).differ(varName));
//        }
//        return root.simplify();
    }
}

class Raznost implements SimpleFunc{
    
    @Override
    public double Evaluate(List<Integer> gains,double... input) {
        double output=0;
        for(int i=0;i<input.length;i++){
            if(gains.get(i)==1){
                output-=input[i];
            }else{
                output+=input[i];
            }
        }
        return output;
    }
    
    @Override
    public SimpleFunc inverse(int index){
        if(index==0){
            return new Summa();
        }else{
            return this;
        }
        
    }
    
    @Override
    public SimpleFunc copy(){
        return new Raznost();
    }
    
    @Override
    public Uzel simplify(FuncUzel input){
        Uzel output;
        List<Uzel> inps=input.getInputs();
        List<Integer> gains=input.getGain();
        int len=inps.size();
        
        //simplify inputs
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                inps.set(i,((FuncUzel)uz).simplify());
            }
        }
        
        //gathering
        for(int i=0;i<len;i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                FuncUzel fuz=(FuncUzel)uz;
                if(fuz.getFuncName().equals("-")){
                    len+=fuz.getInputs().size()-1;
                    for(Uzel innerUz:fuz.getInputs()){
                        inps.add(innerUz);
                    }
                    for(int gain:fuz.getGain()){
                        gains.add(gain);
                    }
                    inps.remove(uz);
                }
            }
        }
        
        // decrease consts
        int numOfConsts=0;
        double[] inputs=new double[inps.size()];
        int[]   indxs=new int[inps.size()];
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof Const){
                inputs[numOfConsts]=((Const)uz).getValue();
                indxs[numOfConsts]=i;
                numOfConsts++;
            }
        }
        if(numOfConsts==inps.size()){
            output=new Const(Evaluate(gains, inputs));
        }else if(inps.size()==1){
            if(gains.get(0)==1)
                output=inps.get(0);
            else
                output=input;
        }else if(numOfConsts==1){
            if(gains.get(indxs[0])*inputs[0]==0.0){
                inps.remove(indxs[0]);
                gains.remove(indxs[0]);
            }
            output=input;
        }else{
            double val=0;
            for(int i=0;i<numOfConsts;i++){
                val-=gains.get(indxs[i])*inputs[i];
            }
            inps.add(new Const(val));
            gains.add(1);
            for(int i=numOfConsts-1;i>=0;i--){
                inps.remove(indxs[i]);
                gains.remove(indxs[i]);
            }
            output=input;
        }
        
        return output;
    }
    
    @Override
    public String getName(int i){
        if(i==1){
            return "-";
        }else{
            return "+";
        }
    }
    
    @Override
    public int[] getRequiredIndexes(){
        int[] out={-1};
        return out;
    }
    
    @Override
    public Uzel differ(FuncUzel root,String varName){
        for(int i=root.getInputs().size()-1;i>=0;i--){
            root.getInputs().set(i,root.getInputs().get(i).differ(varName));
        }
        return root;
    }
}

class Multiplex implements SimpleFunc{
    
    @Override
    public double Evaluate(List<Integer> gains,double... input) {
        double output=input[0];
        if(gains.get(0)==-1) output=1/output;
        for(int i=1;i<input.length;i++){
            if(gains.get(i)==1){
                output*=input[i];
            }else{
                output/=input[i];
            }
        }
        return output;
    }
    
    @Override
    public SimpleFunc inverse(int index){
        return new Divide();
    }
    
    @Override
    public SimpleFunc copy(){
        return new Multiplex();
    }
    
    @Override
    public String getName(int i){
        if(i==1){
            return "*";
        }else{
            return "/";
        }
    }
    
    @Override
    public Uzel simplify(FuncUzel input){
        Uzel output;
        List<Uzel> inps=input.getInputs();
        List<Integer> gains=input.getGain();
        int len=inps.size();
        
        //simplify inputs
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                inps.set(i,((FuncUzel)uz).simplify());
            }
        }
                
        //gathering
        for(int i=0;i<len;i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                FuncUzel fuz=(FuncUzel)uz;
                if(fuz.getFuncName().equals("*")){
                    len+=fuz.getInputs().size()-1;
                    for(Uzel innerUz:fuz.getInputs()){
                        inps.add(innerUz);
                    }
                    for(int gain:fuz.getGain()){
                        gains.add(gain*gains.get(i));
                    }
                    inps.remove(i);
                    gains.remove(i);
                    i--;
                }
            }
        }
        
        if(inps.size()==1){
            if(gains.get(0)==1)
                return inps.get(0);
            else{
                return new FuncUzel("inv",inps.get(0));
            }
        }
        
        // decrease consts
        int numOfConsts=0;
        double[] inputs=new double[inps.size()];
        int[]   indxs=new int[inps.size()];
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof Const){
                inputs[numOfConsts]=((Const)uz).getValue();
                indxs[numOfConsts]=i;
                numOfConsts++;
                if(((Const)uz).getValue()==0.0){
                    return new Const(0.0);
                }
            }
        }
        if(numOfConsts==inps.size()){
            output=new Const(Evaluate(gains, inputs));
        }else if(inps.size()==1){
            if(gains.get(0)==1)
                output=inps.get(0);
            else
                output=input;
        }else if(numOfConsts==1){
            if(((Const)inps.get(indxs[0])).getValue()==1.0){
                inps.remove(indxs[0]);
                gains.remove(indxs[0]);
            }
            if(inps.size()>1) output=input;
            else{
                if(gains.get(0)==1)
                    output=inps.get(0);
                else{
                    output=new FuncUzel("inv",inps.get(0));
                }
            }
        }else{
            double val=1;
            for(int i=0;i<numOfConsts;i++){
                val*=Math.pow(inputs[i], gains.get(indxs[i]));
            }
            if(val!=1.0){
                inps.add(new Const(val));
                gains.add(1);
            }
            for(int i=numOfConsts-1;i>=0;i--){
                inps.remove(indxs[i]);
                gains.remove(indxs[i]);
            }
            output=input;
        }
        //expand k*(... + ... +)
        expand:
        if(output instanceof FuncUzel){
            FuncUzel test=(FuncUzel)output;
            if(test.getFuncName().equals("*")){
                List<Uzel> testInps=test.getInputs();
                for(int i=0;i<testInps.size();i++){
                    Uzel uz=testInps.get(i);
                    if(uz instanceof FuncUzel){
                        if(((FuncUzel)uz).getFuncName().equals("+")&&test.getGain().get(i)==1){
                            // create 
                            List<Uzel> newInps=((FuncUzel)uz).getInputs();
                            List<Uzel> outInps=new ArrayList();
                            List<Integer> outGai=new ArrayList();
                            for(int j=0;j<newInps.size();j++){
                                List<Uzel> inp=new ArrayList();
                                List<Integer> gai=new ArrayList();
                                for(int k=0;k<testInps.size();k++){
                                    if(k!=i){
                                        inp.add(testInps.get(k).copy());
                                        gai.add(test.getGain().get(k));
                                    }else{
                                        inp.add(newInps.get(j).copy());
                                        gai.add(1);
                                    }
                                }
                                outInps.add(new FuncUzel("*", inp, gai));
                                outGai.add(((FuncUzel) uz).getGain().get(j));
//                                newInps.set(j, new FuncUzel("*", inp, gai));
                            }
//                            output=((FuncUzel) uz).simplify();
                            output=new FuncUzel("+",outInps,outGai);
                            break expand;
                        }
                    }
                }
            }
        }
        return output;
    }
    
    @Override
    public int[] getRequiredIndexes(){
        int[] out={-1};
        return out;
    }
    
    /**
     * d.a*b+a*d.b
     * @param root Uzel that call differ method
     * @param varName 
     */
    @Override
    public Uzel differ(FuncUzel root,String varName){
        List<Uzel> inps=root.getInputs();
        List<Integer> gains=root.getGain();
        Uzel a,b;
        if(gains.get(0)==-1){
//            List<Uzel> in=new ArrayList();
//            in.add(inps.get(0));
//            List<Integer> gain=new ArrayList();
//            gain.add(-1);
//            a=new FuncUzel("*",in,gain);
            a=new FuncUzel("inv",inps.get(0));
        }else{
            a=inps.get(0);
        }
        if(inps.size()==2){
            if(gains.get(1)==-1){
//                List<Uzel> in=new ArrayList();
//                in.add(inps.get(1));
//                List<Integer> gain=new ArrayList();
//                gain.add(-1);
//                b=new FuncUzel("*",in,gain);
                
                b=new FuncUzel("inv",inps.get(1));
            }else{
                b=inps.get(1);
            }
        }else{
            b=new FuncUzel("*", inps.subList(1, inps.size()),
                    gains.subList(1, inps.size()));
        }
        Uzel left=new FuncUzel("*",a.differ(varName),b);
        Uzel right=new FuncUzel("*",a,b.differ(varName));
        FuncUzel out=new FuncUzel("+",left,right);
        return out.simplify();
    }
}

class Divide implements SimpleFunc{
    
    @Override
    public double Evaluate(List<Integer> gains,double... input) {
        double output=input[0];
        if(gains.get(0)==1) output=1/output;
        for(int i=1;i<input.length;i++){
            if(gains.get(i)==1){
                output/=input[i];
            }else{
                output*=input[i];
            }
        }
        return output;
    }
    
    @Override
    public SimpleFunc inverse(int index){
        if(index==0)
            return new Multiplex();
        else
            return this;
    }
    
    @Override
    public SimpleFunc copy(){
        return new Divide();
    }
    
    @Override
    public Uzel simplify(FuncUzel input){
        Uzel output;
        List<Uzel> inps=input.getInputs();
        List<Integer> gains=input.getGain();
        int len=inps.size();
        
        //simplify inputs
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                inps.set(i,((FuncUzel)uz).simplify());
            }
        }
        
        //gathering
        for(int i=0;i<len;i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                FuncUzel fuz=(FuncUzel)uz;
                if(fuz.getFuncName().equals("/")){
                    len+=fuz.getInputs().size()-1;
                    for(Uzel innerUz:fuz.getInputs()){
                        inps.add(innerUz);
                    }
                    for(int gain:fuz.getGain()){
                        gains.add(gain);
                    }
                    inps.remove(uz);
                }
            }
        }
        
        // decrease consts
        int numOfConsts=0;
        double[] inputs=new double[inps.size()];
        int[]   indxs=new int[inps.size()];
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof Const){
                inputs[numOfConsts]=((Const)uz).getValue();
                indxs[numOfConsts]=i;
                numOfConsts++;
            }
        }
        if(numOfConsts==inps.size()){
            output=new Const(Evaluate(gains, inputs));
        }else if(inps.size()==1){
            if(gains.get(0)==1)
                output=inps.get(0);
            else
                output=input;
        }else{
            double val=1;
            for(int i=0;i<numOfConsts;i++){
                val*=Math.pow(inputs[i], gains.get(indxs[i])*-1);
            }
            inps.add(new Const(val));
            gains.add(1);
            for(int i=numOfConsts-1;i>=0;i--){
                inps.remove(indxs[i]);
                gains.remove(indxs[i]);
            }
            output=input;
        }
        
        return output;
    }
    
    @Override
    public String getName(int i){
        if(i==1){
            return "/";
        }else{
            return "*";
        }
    }
    
    @Override
    public int[] getRequiredIndexes(){
        int[] out={-1};
        return out;
    }
    
    @Override
    public Uzel differ(FuncUzel root,String varName){
        List<Uzel> inps=root.getInputs();
        List<Integer> gains=root.getGain();
        Uzel a,b;
        if(gains.get(0)==1){
            a=new FuncUzel("inv",inps.get(0));
        }else{
            a=inps.get(0);
        }
        if(inps.size()==2){
            if(gains.get(1)==1){
                b=new FuncUzel("inv",inps.get(1));
            }else{
                b=inps.get(1);
            }
        }else{
            b=new FuncUzel("*", inps.subList(1, inps.size()),
                    gains.subList(1, inps.size()));
        }
        Uzel left=new FuncUzel("*",a.differ(varName),b);
        Uzel right=new FuncUzel("*",a,b.differ(varName));
        return new FuncUzel("+",left,right);
    }
}

class Sin implements SimpleFunc{
    
    @Override
    public double Evaluate(List<Integer> gains,double... input) {
        if(gains.get(0)==1){
            return Math.sin(input[0]);
        }else{
            return Math.asin(input[0]);
        }
    }
    
    @Override
    public SimpleFunc inverse(int index){
        return new ArcSin();
    }
    
    @Override
    public SimpleFunc copy(){
        return new Sin();
    }
    
    @Override
    public String getName(int i){
        if(i==1){
            return "sin";
        }else{
            return "asin";
        }
    }
    
    @Override
    public Uzel simplify(FuncUzel input){
        Uzel output;
        List<Uzel> inps=input.getInputs();
        List<Integer> gains=input.getGain();
        
        //simplify inputs
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                inps.set(i,((FuncUzel)uz).simplify());
            }
        }
        
        // decrease consts
        int numOfConsts=0;
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof Const){
                numOfConsts++;
            }
        }
        if(numOfConsts==inps.size()){
            output=new Const(Evaluate(gains, ((Const)inps.get(0)).getValue()));
        }else{
            output=input;
        }
        
        return output;
    }
    
    @Override
    public int[] getRequiredIndexes(){
        int[] out={-1};
        return out;
    }
    
    @Override
    public Uzel differ(FuncUzel root,String varName){
        Uzel a=new FuncUzel("cos",root.getInputs(),root.getGain());
        Uzel b=root.getInputs().get(0).differ(varName);
        return new FuncUzel("*",a,b);
    }
}

class ArcSin implements SimpleFunc{

    @Override
    public double Evaluate(List<Integer> gains,double... input) {
        if(gains.get(0)==1){
            return Math.asin(input[0]);
        }else{
            return Math.sin(input[0]);
        }
    }
    
    @Override
    public SimpleFunc inverse(int index){
        return new Sin();
    }
    
    @Override
    public SimpleFunc copy(){
        return new ArcSin();
    }
    
    @Override
    public String getName(int i){
        if(i==1){
            return "asin";
        }else{
            return "sin";
        }
    }
    
    @Override
    public Uzel simplify(FuncUzel input){
        Uzel output;
        List<Uzel> inps=input.getInputs();
        List<Integer> gains=input.getGain();
        
        //simplify inputs
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                inps.set(i,((FuncUzel)uz).simplify());
            }
        }
        
        // decrease consts
        int numOfConsts=0;
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof Const){
                numOfConsts++;
            }
        }
        if(numOfConsts==inps.size()){
            output=new Const(Evaluate(gains, ((Const)inps.get(0)).getValue()));
        }else{
            output=input;
        }
        
        return output;
    }
    
    @Override
    public int[] getRequiredIndexes(){
        int[] out={-1};
        return out;
    }
    
    @Override
    public Uzel differ(FuncUzel root,String varName){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

class Exp implements SimpleFunc{

    @Override
    public double Evaluate(List<Integer> gains, double... input) {
        if(gains.get(0)==1){
            return Math.exp(input[0]);
        }else{
            return Math.log(input[0]);
        }
    }

    @Override
    public SimpleFunc inverse(int index) {
        return new Logn();
    }

    @Override
    public SimpleFunc copy() {
        return new Exp();
    }

    @Override
    public String getName(int i){
        if(i==1){
            return "exp";
        }else{
            return "logn";
        }
    }
    
    @Override
    public Uzel simplify(FuncUzel input){
        Uzel output;
        List<Uzel> inps=input.getInputs();
        List<Integer> gains=input.getGain();
        
        //simplify inputs
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                inps.set(i,((FuncUzel)uz).simplify());
            }
        }
        
        // decrease consts
        int numOfConsts=0;
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof Const){
                numOfConsts++;
            }
        }
        if(numOfConsts==inps.size()){
            output=new Const(Evaluate(gains, ((Const)inps.get(0)).getValue()));
        }else{
            output=input;
        }
        
        return output;
    }
    
    @Override
    public int[] getRequiredIndexes(){
        int[] out={-1};
        return out;
    }
    
    @Override
    public Uzel differ(FuncUzel root,String varName){
        Uzel b=root.getInputs().get(0).differ(varName);
        return new FuncUzel("*",root,b);
    }
}

class Logn implements SimpleFunc{
    @Override
    public double Evaluate(List<Integer> gains, double... input) {
        if(gains.get(0)==1){
            return Math.log(input[0]);
        }else{
            return Math.exp(input[0]);
        }
    }

    @Override
    public SimpleFunc inverse(int index) {
        return new Exp();
    }

    @Override
    public SimpleFunc copy() {
        return new Logn();
    }

    @Override
    public String getName(int i){
        if(i==1){
            return "logn";
        }else{
            return "exp";
        }
    }
    
    @Override
    public Uzel simplify(FuncUzel input){
        Uzel output;
        List<Uzel> inps=input.getInputs();
        List<Integer> gains=input.getGain();
        
        //simplify inputs
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                inps.set(i,((FuncUzel)uz).simplify());
            }
        }
        
        // decrease consts
        int numOfConsts=0;
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof Const){
                numOfConsts++;
            }
        }
        if(numOfConsts==inps.size()){
            output=new Const(Evaluate(gains, ((Const)inps.get(0)).getValue()));
        }else{
            output=input;
        }
        
        return output;
    }
    
    @Override
    public int[] getRequiredIndexes(){
        int[] out={-1};
        return out;
    }
    
    /**
     * (ln(x))'=(1/x)*(x')
     * a=1/x, b=x'.
     * @param root
     * @param varName
     * @return 
     */
    @Override
    public Uzel differ(FuncUzel root,String varName){
        Uzel a=new FuncUzel("inv",root.getInputs(),root.getGain());
        Uzel b=root.getInputs().get(0).differ(varName);
        return new FuncUzel("*",a,b);
    }
}

class GreatThan implements SimpleFunc{

    @Override
    public double Evaluate(List<Integer> gains, double... input) {
        if(input[0]>input[1]) return 1;
        else return 0;
    }

    @Override
    public SimpleFunc inverse(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SimpleFunc copy() {
        return new GreatThan();
    }

    @Override
    public String getName(int i) {
        return "gr";
    }
    
    @Override
    public Uzel simplify(FuncUzel input){
        Uzel output;
        List<Uzel> inps=input.getInputs();
        List<Integer> gains=input.getGain();
        
        //simplify inputs
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                inps.set(i,((FuncUzel)uz).simplify());
            }
        }
        
        // decrease consts
        int numOfConsts=0;
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof Const){
                numOfConsts++;
            }
        }
        if(numOfConsts==inps.size()){
            output=new Const(Evaluate(gains, ((Const)inps.get(0)).getValue()));
        }else{
            output=input;
        }
        
        return output;
    }
    
    @Override
    public int[] getRequiredIndexes(){
        int[] out={-1};
        return out;
    }
    
    @Override
    public Uzel differ(FuncUzel root,String varName){
        return root;
    }
}

class If implements SimpleFunc{

    @Override
    public double Evaluate(List<Integer> gains, double... input) {
        if(input[0]==0){
            return input[2];
        }else{
            return input[1];
        }
    }

    @Override
    public SimpleFunc inverse(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SimpleFunc copy() {
        return new If();
    }

    @Override
    public String getName(int i) {
        return "if";
    }
    
    @Override
    public Uzel simplify(FuncUzel input){
        Uzel output;
        List<Uzel> inps=input.getInputs();
        List<Integer> gains=input.getGain();
        
        //simplify inputs
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                inps.set(i,((FuncUzel)uz).simplify());
            }
        }
        
        // decrease consts
        if(inps.get(1).toString().equals(inps.get(2).toString())){
            output=inps.get(1).copy();
        }else{
            output=input;
        }
        
        return output;
    }
    
    @Override
    public int[] getRequiredIndexes(){
        int[] out={1,2};
        return out;
    }
    
    @Override
    public Uzel differ(FuncUzel root,String varName){
        List<Uzel> inps=root.getInputs();
        inps.set(1, inps.get(1).differ(varName));
        inps.set(2, inps.get(2).differ(varName));
        return root;
    }
}

class Inverse implements SimpleFunc{

    @Override
    public double Evaluate(List<Integer> gains, double... input) {
        if(gains.get(0)==1) return 1/input[0];
        else return input[0];
    }

    @Override
    public SimpleFunc inverse(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SimpleFunc copy() {
        return new Inverse();
    }

    @Override
    public String getName(int i) {
        return "inv";
    }
    
    @Override
    public Uzel simplify(FuncUzel input){
        Uzel output;
        List<Uzel> inps=input.getInputs();
        List<Integer> gains=input.getGain();
        
        //simplify inputs
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                inps.set(i,((FuncUzel)uz).simplify());
            }
        }
        
        // decrease consts
        int numOfConsts=0;
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof Const){
                numOfConsts++;
            }
        }
        if(numOfConsts==inps.size()){
            output=new Const(Evaluate(gains, ((Const)inps.get(0)).getValue()));
        }else{
            output=input;
        }
        
        return output;
    }

    @Override
    public int[] getRequiredIndexes() {
        int[] out={-1};
        return out;
    }

    @Override
    public Uzel differ(FuncUzel root, String varName) {
        if(root.getGain().get(0)==1){
            Uzel out=new FuncUzel("pow",root.getInputs().get(0),new Const(-2.0));
            out=new FuncUzel("*",out,new Const(-1.0),root.getInputs().get(0).differ(varName));
            return out;
        }else{
            return root.getInputs().get(0).differ(varName);
        }
    }
    
}

class Pow implements SimpleFunc{

    @Override
    public double Evaluate(List<Integer> gains, double... input) {
        return Math.pow(input[0], input[1]);
    }

    @Override
    public SimpleFunc inverse(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SimpleFunc copy() {
        return new Pow();
    }

    @Override
    public String getName(int i) {
        return "pow";
    }
    
    @Override
    public Uzel simplify(FuncUzel input){
        Uzel output;
        List<Uzel> inps=input.getInputs();
        List<Integer> gains=input.getGain();
        
        //simplify inputs
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                inps.set(i,((FuncUzel)uz).simplify());
            }
        }
        
        // decrease consts
        int numOfConsts=0;
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof Const){
                numOfConsts++;
            }
        }
        if(numOfConsts==inps.size()){
            double[] vals=new double[inps.size()];
            for(int i=0;i<inps.size();i++){
                vals[i]=((Const)inps.get(i)).getValue();
            }
            output=new Const(Evaluate(gains, vals));
        }else{
            output=input;
        }
        
        return output;
    }

    @Override
    public int[] getRequiredIndexes() {
        int[] out={0};
        return out;
    }

    @Override
    public Uzel differ(FuncUzel root, String varName) {
        double oldval=((Const)root.getInputs().get(1)).getValue();
        Uzel out=new FuncUzel("pow",root.getInputs(),root.getGain());
        ((FuncUzel)out).getInputs().set(1,new Const(oldval-1.0));
        out=new FuncUzel("*",out,new Const(oldval),root.getInputs().get(0).differ(varName));
        return out;
    }
    
}

class Cos implements SimpleFunc{

    @Override
    public double Evaluate(List<Integer> gains, double... input) {
        return Math.cos(input[0]);
    }

    @Override
    public SimpleFunc inverse(int index) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SimpleFunc copy() {
        return new Cos();
    }

    @Override
    public String getName(int i) {
        return "cos";
    }
    
    @Override
    public Uzel simplify(FuncUzel input){
        Uzel output;
        List<Uzel> inps=input.getInputs();
        List<Integer> gains=input.getGain();
        
        //simplify inputs
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof FuncUzel){
                inps.set(i,((FuncUzel)uz).simplify());
            }
        }
        
        // decrease consts
        int numOfConsts=0;
        for(int i=0;i<inps.size();i++){
            Uzel uz=inps.get(i);
            if(uz instanceof Const){
                numOfConsts++;
            }
        }
        if(numOfConsts==inps.size()){
            output=new Const(Evaluate(gains, ((Const)inps.get(0)).getValue()));
        }else{
            output=input;
        }
        
        return output;
    }

    @Override
    public int[] getRequiredIndexes() {
        int[] out={0};
        return out;
    }

    @Override
    public Uzel differ(FuncUzel root, String varName) {
        Uzel out=new FuncUzel("sin",root.getInputs(),root.getGain());
        out=new FuncUzel("*",out,new Const(-1.0));
        return out;
    }
    
}