package MathPackODE;

public class Domain {
    private String potential[];
    private String flow[];
    private String wireColour;
    private String name;

    public Domain(String name, String wColour, String[] pot, String[] curr){
        potential=pot;
        flow=curr;
        setWireColour(wColour);
        setName(name);
    }

    public String[] getPotential() {
        return potential;
    }

    public String[] getFlow() {
        return flow;
    }

    public String getWireColour() {
        return wireColour;
    }

    public void setWireColour(String wireColour) {
        this.wireColour = wireColour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
