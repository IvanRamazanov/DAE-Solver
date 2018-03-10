/*
 * The MIT License
 *
 * Copyright 2017 ramazanov_im.
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

import ElementBase.ElectricPin;
import ElementBase.SchemeElement;
import Elements.Environment.Subsystem;

/**
 *
 * @author ramazanov_im
 */
public class ThreePhMeasure extends SchemeElement {
    public ThreePhMeasure(Subsystem sys){
        super(sys);
        addElemCont(new ElectricPin(this, 4, 4));   //A
        addElemCont(new ElectricPin(this, 20, 4));  //B
        addElemCont(new ElectricPin(this, 40, 4));  //C
        addElemCont(new ElectricPin(this, 4, 60));  //a
        addElemCont(new ElectricPin(this, 20, 60)); //b
        addElemCont(new ElectricPin(this, 40, 60)); //c
        addMathContact('o');
        addMathContact('o');
    }
    public ThreePhMeasure(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String[] str={
                "p.1=p.4","p.2=p.5","p.3=p.6",
                "i.1+i.4=0","i.2+i.5=0","i.3+i.6=0",
                "O.1={i.1,i.2,i.3}",
                "O.1={p.1-p.2,p.1-p.3,p.2-p.3}"
        };
        return str;
    }

    @Override
    protected void setParams(){
        setName("Three-phase measurements");
    }

    @Override
    protected String getDescription(){
        return "This block measures three-phase currents and line-line voltages.";
    }
}

