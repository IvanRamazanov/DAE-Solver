/*
 * The MIT License
 *
 * Copyright 2018 Ivan.
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
package ElementBase;

import ElementBase.Element.Parameter;
import ElementBase.Element.InitParam;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ivan
 */
public class ElemSerialization implements Serializable{
        private final String elemName;
        private final double[] initValue,paramValue;
        private final boolean[] priorities;
        
        ElemSerialization(Element she){
            this.elemName=she.getClass().getName();
            initValue=new double[she.getInitials().size()];
            paramValue=new double[she.getParameters().size()];
            priorities=new boolean[paramValue.length];
            
            int i=0;
            for(Parameter p:she.getParameters()){
                paramValue[i++]=p.getDoubleValue();
            }
            i=0;
            for(InitParam p:she.getInitials()){
                initValue[i]=p.getDoubleValue();
                priorities[i++]=p.getPriority();
            }
            
        }
        
        public Element deserialize(){
            Element out=null;
            try {
                Class<?> clas=Class.forName(elemName);
                Constructor<?> ctor=clas.getConstructor();
                out=(Element)ctor.newInstance(new Object[] {});
                
                int i=0;
                for(Parameter p:out.getParameters()){
                    p.setValue(paramValue[i++]);
                }
                i=0;
                for(InitParam p:out.getInitials()){
                    p.setValue(initValue[i]);
                    p.setPriority(priorities[i++]);
                }
            } catch (Exception ex) {
                Logger.getLogger(ElemSerialization.class.getName()).log(Level.SEVERE, null, ex);
            }
            return out;
        }
    }
