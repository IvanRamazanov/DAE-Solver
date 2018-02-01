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
public class SDPM extends ShemeElement{
    Parameter Rs,Ls,J,KsiM,Rp,Zp,Fc;
    
    public SDPM(){
        super();
        addElemCont(new ElemPin(this, 9, 4));
        addElemCont(new ElemPin(this, 25, 4));
        addElemCont(new ElemPin(this, 40, 4));
        Rs=new Parameter("Stator resistance per phase, Rs", 0.18);
        this.parameters.add(Rs);
        Ls=new Parameter("Stator phase inductance, Ls", 8.5e-3);
        this.parameters.add(Ls);
        KsiM=new Parameter("Flux linkage established by magnets", 0.07145);
        this.parameters.add(KsiM);
        J=new Parameter("Момент инерции приведенный к якорю", 0.00062);
        this.parameters.add(J); 
        Zp=new Parameter("Number of permanent magnet pole pairs on the rotor", 4);
        this.parameters.add(Zp);
        Fc=new Parameter("Трение", 0.0);
        this.parameters.add(Fc);
        Rp=new Parameter("Parasitic resistence", 1e7);
        this.parameters.add(Rp);
        this.initials.add(new InitParam("Ток A", 0));
        this.initials.add(new InitParam("Ток B", 0));
        this.initials.add(new InitParam("Скорость", 2*Math.PI*50/4));
        this.initials.add(new InitParam("angle", 0));
        this.addMathContact('o');
        this.addMathContact('i');
        name="СДПМ";
    }
    
    public SDPM(boolean flag){
        super(flag);
    }

    @Override
    public String[] getStringFunction() {
        String M="zp*ksim*(cos(X.4)*X.1+cos(X.4+2/3*pi)*X.2-cos(X.4+4/3*pi)*(X.1+X.2))",
                rp=Rp.getStringValue();
        String[] out={
            "d.X.1=1/(3*ls)*(2*(p.1-p.2)+p.2-p.3-3*rs*X.1+ksim*zp*X.3*(cos(X.4+2/3*pi)-2*cos(X.4)+cos(X.4+4/3*pi)))",
            "d.X.2=1/(3*ls)*(p.2-p.1+p.2-p.3-3*rs*X.2+ksim*zp*X.3*(cos(X.4)-2*cos(X.4+2/3*pi)+cos(X.4+4/3*pi)))",
            "d.X.3=("+M+"-I.1-fc*X.3)/J",
            "d.X.4=X.3*zp",
            "i.1+i.2+i.3=0",
            "O.1=X.3",
            "X.1=i.1",
            "X.2=i.2"
        };
        for(int i=0;i<out.length;i++){
            out[i]=out[i].replace("rs", Rs.getStringValue());
            out[i]=out[i].replace("ls", Ls.getStringValue());
            out[i]=out[i].replace("zp", Zp.getStringValue());
            out[i]=out[i].replace("pi", Double.toString(Math.PI));
            out[i]=out[i].replace("ksim", KsiM.getStringValue());
            out[i]=out[i].replace("J", J.getStringValue());
            out[i]=out[i].replace("fc", Fc.getStringValue());
        }
        return out;
    }
    
    
    
//d.X.1=(4*ls*(p.1-p.2)+4*ls*(p.1-p.3)+4*ms*(p.1-p.2)+4*ms*(p.1-p.3)-12*ls*Rs*X.1-12*ms*Rs*X.1-6*lm*(p.1-p.2)*cos(2*X.4*zp)-6*lm*(p.1-p.3)*cos(2*X.4*zp)+18*lm*Rs*X.1*cos(2*X.4*zp)+6*3^(1/2)*lm*(p.1-p.2)*sin(2*X.4*zp)-6*3^(1/2)*lm*(p.1-p.3)*sin(2*X.4*zp)+18*lm*ksim*X.3*sin(X.4*zp)+12*ls*ksim*X.3*sin(X.4*zp)+12*ms*ksim*X.3*sin(X.4*zp)+18*3^(1/2)*lm^2*X.1*X.3+36*3^(1/2)*lm^2*X.2*X.3+6*3^(1/2)*lm*Rs*X.1*sin(2*X.4*zp)+12*3^(1/2)*lm*Rs*X.2*sin(2*X.4*zp)+36*lm*ls*X.1*X.3*sin(2*X.4*zp)+36*lm*ms*X.1*X.3*sin(2*X.4*zp)-12*3^(1/2)*lm*ls*X.1*X.3*cos(2*X.4*zp)-24*3^(1/2)*lm*ls*X.2*X.3*cos(2*X.4*zp)-12*3^(1/2)*lm*ms*X.1*X.3*cos(2*X.4*zp)-24*3^(1/2)*lm*ms*X.2*X.3*cos(2*X.4*zp))/(-27*lm^2+12*ls^2+24*ls*ms+12*ms^2)
//d.X.2=(8*ls*(p.1-p.2)-4*ls*(p.1-p.3)+8*ms*(p.1-p.2)-4*ms*(p.1-p.3)+6*lm*(p.1-p.2)*cos(2*zp*X.4)-12*lm*(p.1-p.3)*cos(2*zp*X.4)+12*ls*Rs*X.2+12*ms*Rs*X.2+36*3^(1/2)*lm^2*X.1*X.3+18*3^(1/2)*lm^2*X.2*X.3+9*lm*ksim*X.3*sin(zp*X.4)+6*ls*ksim*X.3*sin(zp*X.4)+6*ms*ksim*X.3*sin(zp*X.4)+18*lm*Rs*X.1*cos(2*zp*X.4)+18*lm*Rs*X.2*cos(2*zp*X.4)+6*3^(1/2)*lm*(p.1-p.2)*sin(2*zp*X.4)+36*lm*ms*X.1*X.3*sin(2*zp*X.4)+36*lm*ms*X.2*X.3*sin(2*zp*X.4)+9*3^(1/2)*lm*ksim*X.3*cos(zp*X.4)+6*3^(1/2)*ls*ksim*X.3*cos(zp*X.4)+6*3^(1/2)*ms*ksim*X.3*cos(zp*X.4)-6*3^(1/2)*lm*Rs*X.1*sin(2*zp*X.4)+6*3^(1/2)*lm*Rs*X.2*sin(2*zp*X.4)+36*lm*ls*X.1*X.3*sin(2*zp*X.4)+36*lm*ls*X.2*X.3*sin(2*zp*X.4)+12*3^(1/2)*lm*ls*X.1*X.3*cos(2*zp*X.4)-12*3^(1/2)*lm*ls*X.2*X.3*cos(2*zp*X.4)+12*3^(1/2)*lm*ms*X.1*X.3*cos(2*zp*X.4)-12*3^(1/2)*lm*ms*X.2*X.3*cos(2*zp*X.4))/(27*lm^2-12*ls^2-24*ls*ms-12*ms^2)
//M=(zp*(12*lm*X.2^2*sin(2*X.4*zp)-6*lm*X.1^2*sin(2*X.4*zp)-3*X.1*ksim*sin(X.4*zp)+12*lm*X.1*X.2*sin(2*X.4*zp)+3^(1/2)*X.1*ksim*cos(X.4*zp)+2*3^(1/2)*X.2*ksim*cos(X.4*zp)+6*3^(1/2)*lm*X.1^2*cos(2*X.4*zp)+12*3^(1/2)*lm*X.1*X.2*cos(2*X.4*zp)))/4
}
