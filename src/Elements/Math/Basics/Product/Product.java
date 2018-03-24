package Elements.Math.Basics.Product;

import ElementBase.MathElement;
import Elements.Environment.Subsystem.Subsystem;

import java.util.ArrayList;
import java.util.List;

public class Product extends MathElement{
    public Product(Subsystem sys){
        super(sys);

        addMathContact('i');
        addMathContact('i');
        addMathContact('o');
    }

    public Product(boolean val){
        super(val);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        List<Double> in1=getInputs().get(0).getValue(),
                in2=getInputs().get(1).getValue();
        final List<Double> out=new ArrayList<>();
        for(int i=0;i<in1.size();i++){
            out.add(in1.get(i)*in2.get(i));
        }
        return out;
    }

    @Override
    protected String getDescription() {
        return "out=in1*in2 element wise";
    }

    @Override
    protected void setParams() {
        setName("Product");
    }
}
