package MathPackODE;

import MathPack.StringGraph;

public class Connector {
    private String name;
    private String type;
    private String flow;
    private String force;
    private String[] vars;

    public Connector(){}

    public Connector(String name,Connector image){
        setName(name);
        setType(image.getType());
        setFlow(image.getFlow());
        setForce(image.getForce());
    }

    public Connector(String name, String type, String flowName, String forceName){
        setName(name);
        setType(type);
        setFlow(flowName);
        setForce(forceName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getForce() {
        return force;
    }

    public void setForce(String force) {
        this.force = force;
    }

    public StringGraph[] connect(Connector conn){
        //new StringGraph[]
        return null;
    }

    public String[] getVars(){
        return vars;
    }

    private void setVars(){
        vars=new String[]{name+"."+flow,name+"."+force};
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
