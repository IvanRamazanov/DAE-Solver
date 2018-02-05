/*
 * The MIT License
 *
 * Copyright 2017 Иван.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package Elements;

import ElementBase.ElemPin;
import ElementBase.ShemeElement;

/**
 *
 * @author Иван
 */
public class NPNtrans extends ShemeElement{
    Parameter Is,bf,br,Vt;
    public NPNtrans(){
        super();
        addElemCont(new ElemPin(this, 32, 9)); //C
        addElemCont(new ElemPin(this, 32, 84)); //E
        addElemCont(new ElemPin(this, 6, 46)); //B
    }
    
    public NPNtrans(boolean catalog){
        super(catalog);
    }

    @Override
    public String[] getStringFunction() {
        String is=Is.getStringValue(),
                Bf=Double.toString(bf.getDoubleValue()),
                Br=Double.toString(br.getDoubleValue()),
                vt=Vt.getStringValue();
                
        double q=1.602176*Math.pow(10,-19),k=1.3806503*Math.pow(10,-23);
        String qk=Double.toString(q/k);
        
        String[] str={  
                        "i.1="+is+"*((exp((p.3-p.2)*"+qk+"/"+vt+")-exp((p.3-p.1)*"+qk+"/"+vt+"))*(1-(p.3-p.1)/200)-(exp((p.3-p.1)*"+qk+"/"+vt+")-1)/"+Br+")", //C
                        //"i.2=1*"+is+"*(exp((p.3-p.2)/"+vt+")-exp((p.3-p.1)/"+vt+")+"+Bf+"*(exp((p.3-p.2)/"+vt+")-1))",   //E
                        "i.3="+is+"*((exp((p.2-p.3)*"+qk+"/"+vt+")-1)/"+Bf+"+(exp((p.3-p.1)*"+qk+"/"+vt+")-1)/"+Br+")", //B
                        "i.1+i.2+i.3=0"
        };
        return str;
    }
    
    @Override
    protected void setParams(){
        Is=new Parameter("Saturation current",1e-14);
        parameters.add(Is);
        bf=new Parameter("Forward common emitter current gain",100);
        parameters.add(bf);
        br=new Parameter("Reverse common emitter current gain",1);
        parameters.add(br);
        Vt=new Parameter("Thermal voltage",25.0+273.15);
        parameters.add(Vt);
        
        setName("Bipolar transistor\nNPN");
    }
}
