package Elements.Math.Sources.SqeezeWave;

import ElementBase.MathElement;
import Elements.Environment.Subsystem.Subsystem;
import MathPackODE.Solver;

import java.util.ArrayList;
import java.util.List;

public class SqeezeWave extends MathElement{
    private ScalarParameter fStart,fEnd,slopeTime,A,phaseShift;
    private final double pi=Math.PI;

    public SqeezeWave(Subsystem sys){
        super(sys);

        addMathContact('o');
    }

    public SqeezeWave(boolean val){
        super(val);
    }

    @Override
    protected List<Double> getValue(int outIndex) {
        List<Double> out=new ArrayList<>();
        double fs=fStart.getValue(),
            fe=fEnd.getValue(),
            phi=phaseShift.getValue(),
            a=A.getValue(),
            tSlp=slopeTime.getValue(),
            y,t=Solver.time;

        double smoothA=(a-15)/tSlp*t+15;
        if(t<tSlp)
            y=smoothA*Math.sin(2*pi*((fe-fs)/tSlp*t+fs)/2*t+phi/180*pi);
        else
            y=a*Math.sin(2*pi*fe*t+phi*pi/180);
        out.add(y);
        return out;
    }

    @Override
    protected String getDescription() {
        return "This block represents sine wave with linearly increased frequency.";
    }

    @Override
    protected void setParams() {
        fStart=new ScalarParameter("Starting frequency, Hz",0.0);
        fEnd=new ScalarParameter("Final frequency, Hz",50.0);
        slopeTime= new ScalarParameter("Raise time, sec",1.0);
        A=new ScalarParameter("Amplitude",1.0);
        phaseShift=new ScalarParameter("Phase shift, deg",0.0);
        getParameters().addAll(List.of(A,fStart,fEnd,slopeTime,phaseShift));

        setName("Squeeze sine wave");
    }
}
