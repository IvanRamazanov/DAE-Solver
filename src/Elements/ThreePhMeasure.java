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

import ElementBase.ElemPin;
import ElementBase.SchemeElement;

/**
 *
 * @author ramazanov_im
 */
public class ThreePhMeasure extends SchemeElement {  // TODO what is this?
    public ThreePhMeasure(){
        super();
        addElemCont(new ElemPin(this, 4, 4));
        addElemCont(new ElemPin(this, 12, 4));
        addElemCont(new ElemPin(this, 20, 4));
        addElemCont(new ElemPin(this, 4, 60));
        addElemCont(new ElemPin(this, 12, 60));
        addElemCont(new ElemPin(this, 20, 60));
        addMathContact('o');
        addMathContact('o');
    }
    public ThreePhMeasure(boolean Catalog){
        super(Catalog);
    }

    @Override
    public String[] getStringFunction() {
        String[] str={"p.1=p.4","p.2=p.5","O.1=i.1","i.1+i.2=0"};
        return str;
    }

    @Override
    protected void setParams(){
        setName("Трехфазный измеритель");
    }

    @Override
    protected String getDescription(){
        return "This block represents something.";
    }
}

