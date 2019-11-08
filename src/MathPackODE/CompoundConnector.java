package MathPackODE;

import ElementBase.Element;
import MathPack.Parser;
import java.util.List;

public class CompoundConnector extends Connector{
        private Connector[] childs;

        public CompoundConnector(String name,Connector[] connectors){
            setName(name);
            setChilds(connectors);
        }

        public CompoundConnector(List<String> rawData){
            int i=rawData.size();
            childs=new Connector[i];

            for(int j=0;j<i;j++){
                String data=rawData.get(j);

                String cName=Parser.getKeyValue(data,"<Name>");

                String cType=Parser.getKeyValue(data,"Type");

                //childs[j]=new Connector(cName,Domain.getDomain(cType)); //TODO

            }
        }

        public Connector[] getChilds() {
            return childs;
        }

        public void setChilds(Connector[] childs) {
            this.childs = childs;
        }

        @Override
        public String[] getVars(){
            int length=0;
            for (Connector c:getChilds()) {
                String[] res=c.getVars();
                length+=res.length;
            }
            String[] out=new String[length];
            length=0;
            for(Connector c:getChilds()){
                String[] res=c.getVars();
                for(int j=0;j<res.length;j++) {
                    out[length] =getName()+"."+ res[j];
                    length++;
                }
            }
            return out;
        }
}
