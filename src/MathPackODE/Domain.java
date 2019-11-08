package MathPackODE;

import MathPack.Parser;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public class Domain {
    public static List<Domain> DOMAINS=new ArrayList<>();

    private Connector connector;
    private String wireColour;
    private String typeName;
    private Shape pinShape;

    public Domain(String typeName, String wColour, Shape shp, Connector conn){
        setWireColour(wColour);
        setTypeName(typeName);
        setPinShape(shp);
        setConnector(conn);
    }

    /**
     * Compound domain
     * @param rawData Data from config file
     */
    public Domain(String rawData){
        setWireColour(Parser.getKeyValue(rawData,"WireColor"));
        setTypeName(Parser.getKeyValue(rawData,"Name"));
        setPinShape(Parser.getShape(Parser.getKeyValue(rawData,"PinShape")));

        List<String> conns=Parser.getBlockList(Parser.getBlock(rawData,"<Connectors>"));
        setConnector(new CompoundConnector(conns));

    }

    public String getWireColour() {
        return wireColour;
    }

    public void setWireColour(String wireColour) {
        this.wireColour = wireColour;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Shape getPinShape() {
        return pinShape;
    }

    public void setPinShape(Shape pinShape) {
        if(pinShape==null)
            throw new Error("Wrong shape formatting!");
        this.pinShape = pinShape;
    }

    public static Domain getDomain(String name){
        for (Domain d:DOMAINS) {
            if (d.getTypeName().equals(name)){
                return d;
            }
        }
        return null;
    }


    public Connector getConnector() {
        return connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }
}
