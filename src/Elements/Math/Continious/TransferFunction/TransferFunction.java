package Elements.Math.Continious.TransferFunction;

import ElementBase.DynamMathElem;
import Elements.Environment.Subsystem.Subsystem;

import java.util.ArrayList;
import java.util.List;

public class TransferFunction extends DynamMathElem{
    VectorParameter Arow,Brow;
    double[] A,B;
    int lenA,lenB;

    public TransferFunction(Subsystem sys){
        super(sys);
        addMathContact('i');
        addMathContact('o');
    }

    public TransferFunction(boolean val){
        super(val);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        List<Double> res=getInputs().get(0).getValue();
        double out=0;
        for(int i=0;i<lenB-1;i++){
            out+=wsX.get(i).getValue()*(B[lenB-i-1]-A[lenB-i-1]*B[0]);
        }
        out+=B[0]*res.get(0);
        List<Double> o=new ArrayList<>();
        o.add(out);
        return o;
    }

    @Override
    protected String getDescription() {
        return "Transfer function";
    }

    @Override
    protected void setParams() {
        Arow=new VectorParameter("Denominator","[1 1]");
        Brow=new VectorParameter("Nominator","[1]");
        getParameters().addAll(List.of(Brow,Arow));

        setName("Transfer function");
    }

    @Override
    public void evalDerivatives(){
        List<Double> res=getInputs().get(0).getValue();
        double sum=0;

        for(int i=1;i<wsX.size();i++){
            wsDX.get(i-1).set(wsX.get(i).getValue());
            sum-=A[lenA-i-1]*wsX.get(i).getValue();
        }
        sum-=A[lenA-1]*wsX.get(0).getValue();
        sum+=res.get(0);

        wsDX.get(wsDX.size()-1).set(sum);
    }

    @Override
    public void init(){
        double[] a=Arow.getValue();
        lenA=a.length;
        A=new double[lenA];
        for(int i=0;i<lenA;i++)
            A[i]=a[i];
        double[] b=Brow.getValue();
        lenB=b.length;
        B=new double[lenB];
        for(int i=0;i<lenB;i++)
            B[i]=b[i];

        if(lenB>lenA)
            throw new Error("Nominator bigger, than denominator in "+getName());
        if(lenB<lenA){
            double[] tmp=new double[lenA];
            for(int i=0;i<lenB;i++){
                tmp[lenA-1-i]=B[lenB-1-i];
            }
            B=tmp;
            lenB=B.length;
        }

        // normalization
        double n=A[0];
        for(int i=0;i<lenA;i++)
            A[i]/=n;
        for(int i=0;i<lenB;i++)
            B[i]/=n;


        double[] den=Arow.getValue();
        double[] row=x0.getValue();
//        if((row.length+1)<den.length){
//            double[][] inits=new double[1][den.length];
//            for(int i=0;i<row.length;i++){
//                inits[0][i]=row[i];
//            }
//            x0.setValue(inits);
//        }else
 if((row.length+1)!=den.length)
            throw new Error("Initial X vector bigger than TF order!");
        super.init();
    }
}
