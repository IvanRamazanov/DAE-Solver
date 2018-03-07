package MathPack;

import ElementBase.MathInPin;
import MathPackODE.Solver;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Variables: i.N - electric currens; p.N - electric potential; X.N - state;
 * d.X.N - state differ; O.N - output; I.N - input.
 * @author Ivan
 */
public class StringGraph {
    private Uzel root;
    private static final char[] OPERATORS={'+','-','*','/','^'};
    private static final int[] PRYORIITY={1,1,2,2,3};
    static final int MAX_PRIORITY=4;
    private Set variables;

    public StringGraph(StringGraph input){
        this.variables=new HashSet(input.variables);
        this.root=input.root.copy();
    }

    public StringGraph(double val){
        variables=new HashSet();

        root=new Const(val);
    }

    public StringGraph(FuncUzel uz){
        variables=new HashSet();
        root=uz;
        simplify();
    }

    public StringGraph(LeftPart left){
        this(left.toString());
//        variables=new HashSet();
//        root=new FuncUzel();
    }

    public StringGraph(String function){  // Х indexes disappear?
        variables=new HashSet();
        if(function.isEmpty()) {
            root = new Const(0.0);
        }else {
            if (isSimple(function)) {
                if (isDigit(function)) {
                    root = new Const(function);
                } else {
                    root = new Variable(function);
                    variables.add(function);
                }
            } else {
                root = new FuncUzel(function); //recursiv creation
            }
        }
        if(root instanceof FuncUzel)
            simplify();
    }

    static boolean isOperand(String str,int symbolPos){
        boolean flag=false;
        char symbol=str.charAt(symbolPos);
        for(char c:OPERATORS){
            if(symbol==c) flag=true;
        }
        if(flag){
//            if(symbol=='-'){          TODO check this twice!
//                if(symbolPos>0){
//                    char sym=str.charAt(symbolPos-1);
//                    for(char c:OPERATORS){
//                        if(sym==c||sym=='(') return false;
//                    }
//                }else return false;
//            }
            if(symbolPos==0) return flag;
            symbol=str.charAt(symbolPos-1);
            if(symbol=='e'||symbol=='E'){
                if(symbolPos==1) return flag;
                symbol=str.charAt(symbolPos-2);
                if(Character.isDigit(symbol)) flag=false;
            }
        }
        return flag;
    }

    public StringGraph getDiffer(String varName){
        StringGraph out=new StringGraph(this);
        //out.simplify();      //nahooooi???
        out.root=out.root.differ(varName);
        out.simplify();
        return out;
    }

    public StringGraph getFullTimeDiffer(){
        List<String> vars=new ArrayList(this.getVariableSet());
        if(vars.isEmpty()) return new StringGraph(0.0);
        else if(vars.size()==1&&vars.get(0).equals("time")) return this.getDiffer("time");
        StringGraph out=new StringGraph(this.getDiffer(vars.get(0)));
        out.multiplex(new StringGraph("d."+vars.get(0)));
        for(int i=1; i<vars.size();i++){
            StringGraph temp=this.getDiffer(vars.get(i));
            temp.multiplex(new StringGraph("d."+vars.get(i)));
            out.add(temp);
        }
        return out;
    }

    public void linkVariableToWorkSpace(String name, WorkSpace.Variable link){
        root.setVariableLink(name,link);
    }

    @Override
    public String toString(){
        return root.toString();
    }

    final public void simplify(){
//        if(this.root instanceof FuncUzel){
//            if(((FuncUzel)root).getFuncName().equals("*")){
//                List<Uzel> inps = ((FuncUzel)root).getInputs();
//                for(int i=0;i<inps.size();i++){
//                    Uzel uz=inps.get(i);
//                    if(uz instanceof Const){
//                        if(((Const)uz).getValue()==0.0){
//                            this.root=new Const(0.0);
//                            return;
//                        }
//                    }
//                }
//            }
//            ((FuncUzel)this.root).simplify();
//            if(root instanceof FuncUzel){
//                FuncUzel fu=(FuncUzel)root;
//                if(fu.getInputs().size()==1){
//                    if((fu.getFuncName().equals("*")||fu.getFuncName().equals("+"))&&fu.getGain().get(0)==1){
//                        root=fu.getInputs().get(0);
//                    }
//                }
//            }
//        }
        if(root instanceof FuncUzel){
            root=((FuncUzel)root).simplify();
        }
    }

    /**
     * Check if this graph contains only X.i, d.X.i, time.
     * @return
     */
    public boolean isInvariant(){
        getVariableSet();
        List<String> vars=new ArrayList(variables);
        boolean flag=true;
        for(String name:vars){
//            if(!(name.startsWith("d.X.")||name.equals("time")||name.startsWith("X."))){
            if(!name.equals("time")){
                flag=false;
                break;
            }
        }
        return flag;
    }

    static final boolean isSimple(String function){
        try{
            Double.parseDouble(function);
            return true;
        }catch(NumberFormatException ex){
            boolean output=true;
            for(int i=0;i<function.length();i++){
//                char ch=function.toCharArray()[i];
                char ch=function.charAt(i);
                if(isOperand(function,i)){
                    output=false;
                    break;
                }else if(ch=='('){
                    if(i!=0){
                        if(!isOperand(function,i-1)){   //mnogo skobok mb
                            output=false;
                            break;
                        }
                    }
                }
            }
            return output;
        }
    }

    /**
     * Check this out
     * @param function string
     * @param pos index of '('
     * @return
     */
    static final boolean isFunction(String function,int pos){
        boolean output=false;
        if(pos==0){
            return false;
        }else{
            for(int index=pos-1;index>=0;index--){
                char ch=function.charAt(index);
                if(isOperand(function,index)){
                    return false;
                }else if(ch!='('){
                    return true;
                }
            }
        }
        return false;
    }

    static public double doubleValue(String str){
        str=str.replaceAll(" ","");
        str=str.replaceAll("\\[","");
        str=str.replaceAll("]","");
        StringGraph sg=new StringGraph(str);
        if(sg.getRoot() instanceof Const){
            return ((Const)sg.getRoot()).getValue();
        }else
            throw new Error("Can't cast double from: "+str);
    }

    /**
     * Support only simple input.
     * @param input
     * @return
     */
    final static boolean isDigit(String input){
        boolean output;
        try{
            Double.parseDouble(input);
            output=true;
        }catch(NumberFormatException ex){
            output=false;
        }
        return output;
    }

    int canGet(String varType,int numOfVars){
        int varNum=1;
        for(;varNum<=numOfVars;varNum++){
            if(root.numOfContains(varType+Integer.toString(varNum))==1){
                return varNum-1;
            }
        }
        return -1;
    }

    boolean canGet(String varName){
        boolean outFlag=false;
        if(root.numOfContains(varName)==1){
            if(onlySimpleFuncs(findPath(varName).getFuncs()))
                outFlag=true;
        }
        return outFlag;
    }

    private boolean onlySimpleFuncs(List<FuncUzel> inp){
        boolean flag=true;
        for(FuncUzel uz:inp){
            if(!(uz.getFuncName().equals("+")||uz.getFuncName().equals("*"))){
                flag=false;
            }
        }
        return flag;
    }

    boolean getCurrent(LeftPart left,int numOfVars){
        if(this.root.containInstance("i.")){
            int varNum=1,acc=0;
            boolean flag=false;
            String name="";
            for(;varNum<=numOfVars;varNum++){
                if(root.numOfContains("i."+Integer.toString(varNum))==1){
                    acc++;
                    name="i."+Integer.toString(varNum);
                }
            }
            if(acc==1){
                Path path=findPath(name);
                Uzel newRoot=path.uzelPath.get(0);
                int len=path.length();
                for(int i=0;i<path.length();i++){
                    FuncUzel fu=path.uzelPath.get(i);
                    int j=i+1;
                    Uzel nextfu;
                    if(j==len){
                        StringGraph newoot=new StringGraph(left);
                        nextfu=newoot.root;
                    }else{
                        nextfu=path.uzelPath.get(j);
                    }
                    fu.invertUzel(nextfu, path.numberOfOperand.get(i));
                }
                root=newRoot;
                simplify();
                left.clear();
                left.add(name,1);
                return true;
            }else return false;
        }else return false;
    }

    boolean getPotential(LeftPart left,int numOfVars){
        if(this.root.containInstance("p.")){
            int varNum=1,acc=0;
            boolean flag=false;
            String name="";
            for(;varNum<=numOfVars;varNum++){
                if(root.numOfContains("p."+Integer.toString(varNum))==1){
                    acc++;
                    name="p."+Integer.toString(varNum);
                }
            }
            if(acc==1){
                Path path=findPath(name);
                Uzel newRoot=path.uzelPath.get(0);
                int len=path.length();
                for(int i=0;i<path.length();i++){
                    FuncUzel fu=path.uzelPath.get(i);
                    int j=i+1;
                    Uzel nextfu;
                    if(j==len){
                        StringGraph newoot=new StringGraph(left);
                        nextfu=newoot.root;
                    }else{
                        nextfu=path.uzelPath.get(j);
                    }
                    fu.invertUzel(nextfu, path.numberOfOperand.get(i));
                }
                root=newRoot;
                simplify();
                left.clear();
                left.add(name,1);
                return true;
            }else return false;
        }else return false;
    }

    /**
     * Gets pryority
     * @param ch +,-,... etc.
     * @return
     */
    static int getOperPryor(char ch){
        int out=-1;
        for(int i=0;i<OPERATORS.length;i++){
            if(OPERATORS[i]==ch){
                out=PRYORIITY[i];
                break;
            }
        }
        return out;
    }

    public void add(StringGraph graph){
        if(graph.root instanceof Const){
            if(((Const)graph.root).getValue()==0){
                return;
            }
        }
        if(this.root instanceof Const){
            if(((Const)this.root).getValue()==0){
                this.root=graph.root.copy();
                return;
            }
        }
        root=new FuncUzel("+",root,graph.root);
//        root=((FuncUzel)root).simplify();
        simplify();
    }

    /**
     * Returns this-graph
     * @param graph
     */
    public void sub(StringGraph graph){
        if(graph.root instanceof Const){
            if(((Const)graph.root).getValue()==0){
                return;
            }
        }
        List<Integer> gains=new ArrayList();
        gains.add(1);gains.add(-1);
        List<Uzel> inps=new ArrayList();
        inps.add(root);inps.add(graph.root.copy());
        root=new FuncUzel("+",inps, gains);
        simplify();
    }

    /**
     * this = this-inp
     * @param inp
     */
    public void sub(Uzel inp){
        if(inp instanceof Const){
            if(((Const)inp).getValue()==0){
                return;
            }
        }
        List<Integer> gains=new ArrayList();
        gains.add(1);gains.add(-1);
        List<Uzel> inps=new ArrayList();
        inps.add(root);inps.add(inp.copy());
        root=new FuncUzel("+",inps, gains);
        simplify();
    }

    public StringGraph otnyat(StringGraph graph){
        StringGraph out=new StringGraph(this);

        if(graph.root instanceof Const){
            if(((Const)graph.root).getValue()==0){
                return out;
            }
        }
        if(out.root instanceof Const){
            if(((Const)out.root).getValue()==0){
                out.root=graph.root.copy();
                out.multiplex(-1);
                return out;
            }
        }
        List<Uzel> uzList=new ArrayList();
        uzList.add(root);
        uzList.add(graph.root);
        List<Integer> gains=new ArrayList();
        gains.add(1);
        gains.add(-1);
        out.root=new FuncUzel("+", uzList, gains);
//        root=((FuncUzel)root).simplify();
        return out;
    }

    public void multiplex(StringGraph graph){
        root=new FuncUzel("*",root,graph.root);
//        root=((FuncUzel)root).simplify();
        simplify();
    }

    public void divide(StringGraph graph){
        List<Uzel> uzList=new ArrayList();
        uzList.add(root);
        uzList.add(graph.root);
        List<Integer> gains=new ArrayList();
        gains.add(1);
        gains.add(-1);
        root=new FuncUzel("*",uzList,gains);
//        root=((FuncUzel)root).simplify();
        simplify();
    }

    public void multiplex(double value){
        if(value==1) return;
        Uzel mul=new Const(value);
        root=new FuncUzel("*",root,mul);
//        root=((FuncUzel)root).simplify();
        simplify();
    }

    public static StringGraph mul(StringGraph gr,int value){
        StringGraph out=new StringGraph(gr);
        out.multiplex(value);
        out.simplify();
        return out;
    }

    public static StringGraph mul(StringGraph gr,double value){
        StringGraph out=new StringGraph(gr);
        out.multiplex(value);
        out.simplify();
        return out;
    }

    public static StringGraph sum(StringGraph... gr){
        StringGraph out=new StringGraph(gr[0]);
        List<Uzel> inps=new ArrayList();
        inps.add(out.root);
        List<Integer> gains=new ArrayList();
        gains.add(1);
        for(int i=1;i<gr.length;i++){
            inps.add(gr[i].root.copy());
            gains.add(1);
        }
        out.root=new FuncUzel("+",inps,gains);
        out.simplify();
        return out;
    }

    public static StringGraph sum(StringGraph gr1,StringGraph gr2,int gain){
        if(gain!=1&&gain!=-1) throw new Error("Gain: "+gain+". Gain must be equal +/-1");
        StringGraph out=new StringGraph(gr1);
        List<Uzel> inps=new ArrayList();
        inps.add(out.root);
        List<Integer> gains=new ArrayList();
        gains.add(1);
        inps.add(gr2.root.copy());
        gains.add(gain);
        out.root=new FuncUzel("+",inps,gains);
        out.simplify();
        return out;
    }

    public static StringGraph mul(StringGraph gr1,StringGraph gr2,int gain){
        StringGraph out=new StringGraph(gr1);
        List<Uzel> inps=new ArrayList();
        inps.add(out.root);
        inps.add(gr2.root.copy());
        List<Integer> gains=new ArrayList();
        gains.add(1);
        gains.add(gain);
        out.root=new FuncUzel("*", inps, gains);
        out.simplify();
        return out;
    }

    /**
     * Devides value/gr
     * @param value
     * @param gr
     * @return out=value/gr
     */
    public static StringGraph devide(int value,StringGraph gr){
        StringGraph out;
        out=new StringGraph(gr);
        List<Uzel> inps=new ArrayList();
        inps.add(new Const(value));
        inps.add(out.root);
        List<Integer> gains=new ArrayList();
        gains.add(1);
        gains.add(-1);
        out.root=new FuncUzel("*",inps,gains);
        out.simplify();
        return out;
    }

//    public void replaceVariable(String name,Uzel replacement){
//        root.replaceVar(name, replacement);
//    }

    public void replaceVariable(String name,StringGraph replacement){
        if(root instanceof Variable){
            if(((Variable)root).getName().equals(name)){
                root=replacement.root.copy();
            }
        }else root.replaceVar(name, replacement.root);
        simplify();
    }

    public void replaceWithConst(String name,double value){
        Uzel replacement=new Const(value);
        root.replaceVar(name, replacement);
    }

    public boolean contains(String var){
        //return this.variables.contains(var);
        return this.root.contains(var);
    }

    public int numOfContains(String var){
        //return this.variables.contains(var);
        return this.root.numOfContains(var);
    }

    public boolean containInstance(String var){
        return this.root.containInstance(var);
    }

    /**
     * Case: k*X=f(X,u)
     * @param name
     * @param k
     */
    public void getVariable(String name, int k){
        simplify();
        int varGain=k;
        FuncUzel lft=new FuncUzel("+", new Const(k));
        Path path=findPath(name);
        if(path==null){
            System.err.println("netu syka var: "+name+" in: "+this.toString());
        }
        Uzel newRoot=path.uzelPath.get(0);
        int len=path.length();
        for(int i=0;i<path.length();i++){
            FuncUzel fu=path.uzelPath.get(i);
            if(fu.getFuncName().equals("*")){
                int ind=path.numberOfOperand.get(i);
                FuncUzel tmp=new FuncUzel('*');
                for(int m=0;m<fu.getInputs().size();m++){
                    if(m!=ind) tmp.addOperand(fu.getInputs().get(m), fu.getGain().get(m));
                    else tmp.addOperand(new Const(-1), 1);
                }
                lft.addOperand(tmp, 1);
            }

        }
        root.replaceVar(name, new Const(0));
        root=new FuncUzel("*",root,lft);
        ((FuncUzel)root).getGain().set(1, -1);
        simplify();
    }

    public void getVariable(String name,StringGraph left){
        Path path=findPath(name);
        if(path==null){
            System.err.println("netu syka var: "+name+" in: "+this.toString());
        }else if(path.length()==0){
            root=left.getRoot().copy();
        }else{
            Uzel newRoot=path.uzelPath.get(0);
            int len=path.length();
            for(int i=0;i<path.length();i++){
                FuncUzel fu=path.uzelPath.get(i);
                int j=i+1;
                Uzel nextfu;
                if(j==len){
                    nextfu=left.root.copy();
                }else{
                    nextfu=path.uzelPath.get(j);
                }
                fu.invertUzel(nextfu, path.numberOfOperand.get(i));
            }
            root=newRoot;
            simplify();
        }
    }

    public void getVariable(String name){
        Path path=findPath(name);
        if(path==null){
            System.err.println("netu syka var: "+name+" in: "+this.toString());
        }
        Uzel newRoot=path.uzelPath.get(0);
        int len=path.length();
        for(int i=0;i<path.length();i++){
            FuncUzel fu=path.uzelPath.get(i);
            int j=i+1;
            Uzel nextfu;
            if(j==len){
                nextfu=new Const(0);
            }else{
                nextfu=path.uzelPath.get(j);
            }
            fu.invertUzel(nextfu, path.numberOfOperand.get(i));
        }
        root=newRoot;
        simplify();
    }

    public double evaluate(WorkSpace vars,List<MathInPin> extInput){
        return(root.getValue(vars,extInput));
    }

    Set getVariableSet(){
        variables.clear();
        root.getVariables(variables);
        return variables;
    }

    Path findPath(String varName){
        //levels=0;
        Path path=new Path();
        if(root.contains(path,varName)){
            return path;
        }else return null;
    }

    public Uzel getRoot(){
        return root;
    }

    /**
     * @return the variables
     */
//    private Set getVariables() {
//        return variables;
//    }
}
interface Uzel{
    /**
     * Take value
     * @param vars
     * @param extInput
     * @return
     */
    double getValue(WorkSpace vars,List<MathInPin> extInput);

    void replaceVar(String name,Uzel replace);

    boolean contains(Path path,String varName);

    boolean contains(String varName);

    int numOfContains(String varName);

    boolean containInstance(String varName);

    void setVariableLink(String name, WorkSpace.Variable link);

    int getOrder();

    Uzel differ(String varName);

    void getVariables(Set list);

    void setOrder(int value);

    Uzel copy();

    @Override
    String toString();
}

class Const implements Uzel{
    private double value;
    private int order=-1;

    public Const(double value){
        this.value=value;
    }

    Const(String str){
        this.value=Double.parseDouble(str);
    }

    @Override
    public double getValue(WorkSpace vars,List<MathInPin> extInput){
        return this.value;
    }

    double getValue(){
        return this.value;
    }

    void setValue(double newVal){
        this.value=newVal;
    }

    @Override
    public Uzel differ(String varName){
        return new Const(0.0);
    }

    @Override
    public void replaceVar(String name,Uzel replacement){}

    @Override
    public boolean contains(Path path,String varName){
        return false;
    }

    @Override
    public boolean contains(String varName){
        return false;
    }

    @Override
    public boolean containInstance(String varName){
        return false;
    }

    @Override
    public int getOrder(){
        return order;
    }

    @Override
    public void getVariables(Set inp){

    }

    @Override
    public void setVariableLink(String name, WorkSpace.Variable link){

    }

    @Override
    public int numOfContains(String varName){
        return 0;
    }

    @Override
    public void setOrder(int val){
        order=val;
    }

    @Override
    public Uzel copy(){
        return new Const(this.value);
    }

    @Override
    public String toString(){
        return String.valueOf(this.value);
    }
}

class Variable implements Uzel{
    private String name,shortName;
    private int
            order=-1,
            index=-1,
            secondIndex;
    private WorkSpace.Variable workSpaceLink;

    public Variable(String name){
        if(name.contains(".")){
            this.name=name;
            this.shortName=name.substring(0, name.lastIndexOf(".")+1);
            int lastDot=name.lastIndexOf("."),
            scob;
            if((scob=name.indexOf('['))!=-1){
                index=Integer.parseInt(name.substring(lastDot+1,scob));
                secondIndex=Integer.parseInt(name.substring(scob+1,name.length()-1));
            }else {
                index = Integer.parseInt(name.substring(lastDot + 1));
                secondIndex=0;
            }
        }else
            this.name=this.shortName=name;
    }

    String getName(){
        return this.name;
    }

    String getShortName(){
        return this.shortName;
    }

    @Override
    public void setVariableLink(String name,WorkSpace.Variable wslink){
        if(this.name.equals(name))
            workSpaceLink=wslink;
    }

    @Override
    public double getValue(WorkSpace vars,List<MathInPin> extInput){
        if(shortName.equals("time")){
            return Solver.time;
        }else
        if(shortName.equals("I.")){
            return extInput.get(index-1).getValue().get(secondIndex);
        }else{
//            return vars.get(name);
            if(workSpaceLink==null)
                throw new Error(name+" is null");
            return workSpaceLink.getValue();
        }
    }

    @Override
    public void replaceVar(String name,Uzel replacement){
    }

    @Override
    public boolean contains(Path path,String name){
        return this.name.equals(name);
    }

    @Override
    public boolean contains(String name){
        return this.name.equals(name);
    }

    @Override
    public Uzel differ(String varName){
        if(name.equals(varName)) return new Const(1.0);
        else return new Const(0.0);
    }

    @Override
    public boolean containInstance(String varName){
        return shortName.equals(varName);
    }

    @Override
    public int numOfContains(String varName){
        if(name.equals(varName)){
            return 1;
        }else  return 0;
    }

    @Override
    public void getVariables(Set inp){
        inp.add(name);
    };

    @Override
    public int getOrder(){
        return order;
    }

    @Override
    public void setOrder(int val){
        order=val;
    }

    @Override
    public Uzel copy(){
        Variable n=new Variable(this.getName());
        n.workSpaceLink=this.workSpaceLink;
        return n;
    }

    @Override
    public String toString(){
        return getName();
    }
}

class FuncUzel implements Uzel{
    private int order;
    private MathFunction func;
    private List<Uzel> inputs;
    private List<Integer> gain;

    FuncUzel(String function,Uzel... input){
        gain=new ArrayList();
        func=new MathFunction(function);
        inputs=new ArrayList();
        for(Uzel uz:input){
            inputs.add(uz.copy());
            gain.add(1);
        }
        if(function.equals("-")||function.equals("/")){
            throw new Error("AAAAA nizya tak delat!");
            //gain.set(1, -1);
        }
        //simplify();   DO NOT WORK!
    }

    FuncUzel(String function,List<Uzel> input,List<Integer> gains){
        gain=new ArrayList(gains);
        func=new MathFunction(function);
        inputs=new ArrayList();
        for(Uzel uz:input){
            inputs.add(uz.copy());
        }
        simplify();
//        if(function.equals("-")||function.equals("/")){
//            gain.set(1, -1);
//        }
    }

    FuncUzel(FuncUzel input){
        this.order=input.order;
        this.func=new MathFunction(input.func);
        this.inputs=new ArrayList();
        this.gain=new ArrayList(input.getGain());
        for(int i=0;i<input.inputs.size();i++){
            this.inputs.add(input.inputs.get(i).copy());
        }
    }

    FuncUzel(char func){
        gain=new ArrayList();
        this.func=new MathFunction(String.valueOf(func));
        inputs=new ArrayList();
    }

    FuncUzel(String function){ //implement recursive
        int index=-1,minPryor=1;
        int skobkaNumber=0,size=1;
        //filter "(...)" case
        for(int i=0;i<function.length();i++){
            if(function.charAt(i)=='('){
                skobkaNumber++;
                if(i<size) size=i;
            }else if(function.charAt(i)==')'){
                skobkaNumber--;
                if(skobkaNumber==0 &&
                        size==0 &&
                        i==(function.length()-1)){
                    function=function.substring(1, function.length()-1);
                }else if(skobkaNumber==0){
                    size=function.length();
                }
            }
        }
        skobkaNumber=0; // if not 0, then errnous function
        size=0;
        inputs=new ArrayList();
        while(index==-1){
            List<String> gains=new ArrayList();
            List<Integer> adds=new ArrayList();
            adds.add(1);
            String dump="";
            for(int i=0;i<function.length();i++){
                char c=function.charAt(i);
                if(StringGraph.isOperand(function,i)){
                    int pry=StringGraph.getOperPryor(c)+skobkaNumber*(StringGraph.MAX_PRIORITY);
                    if(minPryor==pry){
                        if(dump.isEmpty()){
                            index=i;
                            if(c=='-'||c=='/'){
                                adds.set(size,-1);
                            }else{
                                adds.set(size, 1);
                            }
                            size++;
                        }else{
                            gains.add(dump);
                            index=i;
                            size++;
                            dump="";
                            if(c=='-'||c=='/'){
                                adds.add(-1);
                            }else{
                                adds.add(1);
                            }
                        }
                    }else{
                        dump+=c;
                    }
                }else{
                    dump+=c;
                }
                //skobka case
                if(c=='('){
                    if(StringGraph.isFunction(function,i)){
                        int pry=StringGraph.MAX_PRIORITY+skobkaNumber*(StringGraph.MAX_PRIORITY);
                        if(minPryor==pry){
                            String temp="";
                            index=i;
                            for(int j=0;j<function.length();j++){
                                char cc=function.charAt(j);
                                if(cc!='(') temp+=cc;
                                else break;
                            }
                            this.func=new MathFunction(temp);
                            this.gain=new ArrayList();   //??????????????????????
                            int inputsNum=this.func.getRank();
                            String vhod=function.substring(temp.length()+1, function.length()-1); // case "f(...)"
                            for(int k=0;k<inputsNum;k++){
                                String vhodTmp="";
                                int skobk=0;
                                for(int m=0;m<vhod.length();m++){
                                    char ch=vhod.charAt(m);
                                    if(ch==','&&skobk==0){
                                        vhod=vhod.substring(m+1);
                                        break;
                                    }else{
                                        vhodTmp+=ch;
                                        if(ch=='(') skobk++;
                                        if(ch==')') skobk--;
                                    }
                                }
                                if(StringGraph.isSimple(vhodTmp)){
                                    if(StringGraph.isDigit(vhodTmp)){
                                        inputs.add(new Const(vhodTmp));
                                    }else{
                                        inputs.add(new Variable(vhodTmp));
//                                            getVariables().add(vhodTmp);
                                    }
                                }else{
                                    inputs.add(new FuncUzel(vhodTmp)); //recursiv creation
                                }
                                this.gain.add(1);
                            }


                            break;
                        }
                        skobkaNumber++;
                        //
                    }else{
                        skobkaNumber++;
                    }
                }else if(c==')'){
                    // check if this is a function!
                    skobkaNumber--;
                }

            }


            //SKOBKA
            if(size!=0){    //IF FIND SOMETHG
                if(!dump.isEmpty()){
                    gains.add(dump);
                }
                if(function.substring(index,index+1).equals("+")||function.substring(index,index+1).equals("-")){
                    this.func=new MathFunction("+");   //create uzel
                }else{
                    this.func=new MathFunction("*");
                }
                for(int k=0;k<gains.size();k++){
                    String part=gains.get(k);
                    if(StringGraph.isSimple(part)){
                        if(StringGraph.isDigit(part)){
                            inputs.add(new Const(part));
                        }else{
                            inputs.add(new Variable(part));
//                                getVariables().add(part);
                        }
                    }else{
                        inputs.add(new FuncUzel(part)); //recursiv creation
                    }
                }
                this.gain=new ArrayList(adds);
            }
            minPryor++;
        }
    }

    @Override
    public boolean contains(Path path,String name){
//            int ownLevel=levels;
        int i=0;
        for(Uzel uz:this.getInputs()){
            if(uz.contains(path,name)){
                path.addPoint(this, i);
                return true;
            }
            i++;
        }
        return false;
    }

    @Override
    public void setVariableLink(String name,WorkSpace.Variable link){
        for(Uzel uz:getInputs()){
            uz.setVariableLink(name, link);
        }
    }

    @Override
    public boolean contains(String name){
        return this.func.contain(getInputs(),name);
    }

    @Override
    public boolean containInstance(String name){
        for(Uzel uz:this.getInputs()){
            if(uz.containInstance(name)){
                return true;
            }
        }
        return false;
    }

    String getFuncName(){
        return func.getFuncName();
    }

    @Override
    public void getVariables(Set inp){
        for(Uzel uz:getInputs()){
            uz.getVariables(inp);
        }
    };

    @Override
    public Uzel differ(String varName){

        return func.differ(this,varName);
    }

    @Override
    public int numOfContains(String varName){
        int out=0;
        for(Uzel uz:getInputs()){
            out+=uz.numOfContains(varName);
        }
        return out;
    }
    void removeInp(int ind){
        this.getInputs().remove(ind);
        this.getGain().remove(ind);
//            this.order--;
    }

    int getRank(){
        return this.getInputs().size();
    }

//        FuncUzel(MathFunction function,Uzel... input){
//            inputs=new ArrayList();
//            for(Uzel uz:input){
//                inputs.add(uz);
//            }
//            rank=input.length;
//            if(rank<function.getRank()){
//                System.err.println("Not enougth inputs!");
//            }
//            func=function;
//        }

    void invertUzel(Uzel next,int refIndex){
        for(int i=0;i<getInputs().size();i++){
            if(i!=refIndex){
                int add=getGain().get(refIndex);
                getGain().set(i, getGain().get(i)*-1*add);
            }
        }
        getInputs().set(refIndex, next);
        //IT MAY BE A FUNCTION, THEN LIFE NOT SO EASY!

//            if(this.func.flipFlag(refIndex)){
//                int tIndx;
//                if(refIndex==1){
//                    tIndx=0;
//                }else{
//                    tIndx=1;
//                }
//                Uzel temp=this.inputs.get(tIndx);
//                this.inputs.set(refIndex, temp);
//                this.inputs.set(tIndx,next);
//            }else{
//                this.inputs.set(refIndex, next);
//            }
//            this.func.inverse(refIndex);
    }

    Uzel simplify(){
        Uzel output=func.simplify(this);
        return output;

//        //Check if all inps is const
//        boolean flag=true;
//        for(int i=0;i<output.getInputs().size();i++){
//            if(!(this.inputs.get(i) instanceof Const)){
//                flag=false;
//                break;
//            }
//        }
//        if(flag){
////                System.err.println(this.asString());
//            return new Const(this.getValue(0, null, null));
//        }else{
////                System.err.println(this.asString());
//            return this;
//        }

    }

    void addOperand(Uzel oper,int gain){
        this.getInputs().add(oper);
        this.getGain().add(gain);
        this.order++;
    }

    @Override
    public double getValue(WorkSpace vars,List<MathInPin> extInput){
        double result;
        double[] input=new double[getInputs().size()];
        for(int i=0;i<getInputs().size();i++){
            input[i]=getInputs().get(i).getValue(vars,extInput);
        }
        result=this.func.Elavuate(this.getGain(),input);
        return result;
    }

    @Override
    public void replaceVar(String name,Uzel replacement){
        for(int i=0;i<this.getInputs().size();i++){
            if(getInputs().get(i) instanceof Variable){
                if(((Variable)getInputs().get(i)).getName().equals(name)){
                    getInputs().set(i, replacement.copy());

                }
            }else{
                getInputs().get(i).replaceVar(name, replacement);
            }
        }
        this.simplify();
    }

    @Override
    public int getOrder(){
        return order;
    }

    @Override
    public void setOrder(int val){
        order=val;
    }

    @Override
    public FuncUzel copy(){
        return new FuncUzel(this);
    }

    @Override
    public String toString(){
        String output="";
        for(int i=0;i<this.getInputs().size();i++){
            Uzel inp=this.getInputs().get(i);
            if(inp instanceof FuncUzel){
                String funcnam=func.getFuncName(this.getGain().get(i));
                if(!((funcnam.equals("+")||funcnam.equals("*"))&&i==0))
                    output+=funcnam;
                output+="(";            //CASE F(x), NOT F(x1,x2,....)!!!!!!
                output+=this.getInputs().get(i).toString();
                output+=")";
            }else{
                String funcnam=func.getFuncName(this.getGain().get(i));
                if(!((funcnam.equals("+")||funcnam.equals("*"))&&i==0))
                    output+=funcnam+"(";
                output+=this.getInputs().get(i).toString();
                if(!((funcnam.equals("+")||funcnam.equals("*"))&&i==0))
                    output+=")";
            }

        }
        return output;
    }

    /**
     * @return the inputs
     */
    public List<Uzel> getInputs() {
        return inputs;
    }

    /**
     * @return the gain
     */
    List<Integer> getGain() {
        return gain;
    }
}

class Path{
    List<FuncUzel> uzelPath;
    List<Integer> numberOfOperand;
    Path(){
        uzelPath=new ArrayList();
        numberOfOperand=new ArrayList();
    }

    void addPoint(FuncUzel func,int numOfOper){
        this.uzelPath.add(func);
        this.numberOfOperand.add(numOfOper);
    }

    int length(){
        return uzelPath.size();
    }

    List<FuncUzel> getFuncs(){
        return uzelPath;
    }
}
