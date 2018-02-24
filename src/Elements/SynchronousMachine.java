package Elements;

import ElementBase.ElemMechPin;
import ElementBase.ElemPin;
import ElementBase.SchemeElement;

import java.util.List;

public class SynchronousMachine extends SchemeElement{
    /*
        X.1 = Ia
        X.2 = Ib
        X.3 = If - rotor current
        X.4 = w - rotor speed in electric domain

        also assume that zero point potential = 0
     */

    Parameter Lls,Lmd,Lmq,Pp,Rf,Rs,Llf,J,F;

    public SynchronousMachine(){
        super();

        addElemCont(new ElemPin(this, 9, 4));  //A
        addElemCont(new ElemPin(this, 25, 4));  // B
        addElemCont(new ElemPin(this, 40, 4));  // C
        addElemCont(new ElemPin(this, 9, 63));  // Field
        addElemCont(new ElemPin(this, 40, 63));

        addMechCont(new ElemMechPin(this,45,35));   // positive
        addMechCont(new ElemMechPin(this,4,35));    // ref
    }

    public SynchronousMachine(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
//        String M="(3*Pp*((2*1.7320508075688772935274463415059*(X.2*cos(time*X.4)+X.1*cos(pi/3+time*X.4))*(X.3*Lmd+(2*1.7320508075688772935274463415059*(X.2*sin(time*X.4)+X.1*sin(pi/3+time*X.4))*(Lls+Lmd))/3))/3-(4*(X.2*cos(time*X.4)+X.1*cos(pi/3+time*X.4))*(X.2*sin(time*X.4)+X.1*sin(pi/3+time*X.4))*(Lls+Lmq))/3))/2";
        String M="(3*Pp*((2*1.732050807568877*(X.2*cos(time*X.4)+X.1*cos(pi/3+time*X.4))*(X.3*Lmd+(2*1.732050807568877*(X.2*sin(time*X.4)+X.1*sin(pi/3+time*X.4))*(Lls+Lmd))/3))/3-(4*(X.2*cos(time*X.4)+X.1*cos(pi/3+time*X.4))*(X.2*sin(time*X.4)+X.1*sin(pi/3+time*X.4))*(Lls+Lmq))/3))/2";
                String [] out={
//                "=Lmd*d.X.3-(2*1.7320508075688772935274463415059*(p.2*sin(time*X.4*Pp)+p.1*sin(pi/3+time*X.4*Pp)))/3+(2*1.7320508075688772935274463415059*Rs*(X.2*sin(time*X.4*Pp)+X.1*sin(pi/3+time*X.4*Pp)))/3+(2*1.7320508075688772935274463415059*(X.2*X.4*Pp*cos(time*X.4*Pp)+X.1*X.4*Pp*cos(pi/3+time*X.4*Pp))*(Lls+Lmd))/3+(2*1.7320508075688772935274463415059*d.X.4*(X.2*time*cos(time*X.4*Pp)+X.1*time*cos(pi/3+time*X.4*Pp))*(Lls+Lmd))/3-(2*1.7320508075688772935274463415059*X.4*Pp*(X.2*cos(time*X.4*Pp)+X.1*cos(pi/3+time*X.4*Pp))*(Lls+Lmq))/3+(2*1.7320508075688772935274463415059*d.X.2*sin(time*X.4*Pp)*(Lls+Lmd))/3+(2*1.7320508075688772935274463415059*d.X.1*sin(pi/3+time*X.4*Pp)*(Lls+Lmd))/3",
//                "=X.4*Pp*(X.3*Lmd+(2*1.7320508075688772935274463415059*(X.2*sin(time*X.4*Pp)+X.1*sin(pi/3+time*X.4*Pp))*(Lls+Lmd))/3)-(2*1.7320508075688772935274463415059*(p.2*cos(time*X.4*Pp)+p.1*cos(pi/3+time*X.4*Pp)))/3+(2*1.7320508075688772935274463415059*Rs*(X.2*cos(time*X.4*Pp)+X.1*cos(pi/3+time*X.4*Pp)))/3-(2*1.7320508075688772935274463415059*(X.2*X.4*Pp*sin(time*X.4*Pp)+X.1*X.4*Pp*sin(pi/3+time*X.4*Pp))*(Lls+Lmq))/3-(2*1.7320508075688772935274463415059*d.X.4*(X.2*time*sin(time*X.4*Pp)+X.1*time*sin(pi/3+time*X.4*Pp))*(Lls+Lmq))/3+(2*1.7320508075688772935274463415059*d.X.2*cos(time*X.4*Pp)*(Lls+Lmq))/3+(2*1.7320508075688772935274463415059*d.X.1*cos(pi/3+time*X.4*Pp)*(Lls+Lmq))/3",
//                "p.4-p.5=d.X.3*(Llf+Lmd)+X.3*Rf+(2*1.7320508075688772935274463415059*Lmd*(X.2*X.4*Pp*cos(time*X.4*Pp)+X.1*X.4*Pp*cos(pi/3+time*X.4*Pp)))/3+(2*1.7320508075688772935274463415059*Lmd*d.X.2*sin(time*X.4*Pp))/3+(2*1.7320508075688772935274463415059*Lmd*d.X.1*sin(pi/3+time*X.4*Pp))/3+(2*1.7320508075688772935274463415059*Lmd*d.X.4*(X.2*time*cos(time*X.4*Pp)+X.1*time*cos(pi/3+time*X.4*Pp)))/3",
                "d.X.4=("+M+"-T.1-"+F.toString()+"*X.4)/"+J.toString(),
                "=Lmd*d.X.3-(2*p.1*cos(time*X.4))/3-(2*p.2*cos((2*pi)/3-time*X.4))/3-(2*p.3*cos((2*pi)/3+time*X.4))/3+(2*1.732050807568877*Rs*(X.2*sin(time*X.4)+X.1*sin(pi/3+time*X.4)))/3+(2*1.732050807568877*(X.2*X.4*cos(time*X.4)+X.1*X.4*cos(pi/3+time*X.4))*(Lls+Lmd))/3+(2*1.732050807568877*d.X.4*(X.2*time*cos(time*X.4)+X.1*time*cos(pi/3+time*X.4))*(Lls+Lmd))/3-(2*1.732050807568877*X.4*(X.2*cos(time*X.4)+X.1*cos(pi/3+time*X.4))*(Lls+Lmq))/3+(2*1.732050807568877*d.X.2*sin(time*X.4)*(Lls+Lmd))/3+(2*1.732050807568877*d.X.1*sin(pi/3+time*X.4)*(Lls+Lmd))/3",
                "=X.4*(X.3*Lmd+(2*1.732050807568877*(X.2*sin(time*X.4)+X.1*sin(pi/3+time*X.4))*(Lls+Lmd))/3)+(2*p.1*sin(time*X.4))/3-(2*p.2*sin((2*pi)/3-time*X.4))/3+(2*p.3*sin((2*pi)/3+time*X.4))/3+(2*1.732050807568877*Rs*(X.2*cos(time*X.4)+X.1*cos(pi/3+time*X.4)))/3-(2*1.732050807568877*(X.2*X.4*sin(time*X.4)+X.1*X.4*sin(pi/3+time*X.4))*(Lls+Lmq))/3-(2*1.732050807568877*d.X.4*(X.2*time*sin(time*X.4)+X.1*time*sin(pi/3+time*X.4))*(Lls+Lmq))/3+(2*1.732050807568877*d.X.2*cos(time*X.4)*(Lls+Lmq))/3+(2*1.732050807568877*d.X.1*cos(pi/3+time*X.4)*(Lls+Lmq))/3",
                "p.4-p.5=d.X.3*(Llf+Lmd)+X.3*Rf+(2*1.732050807568877*Lmd*(X.2*X.4*cos(time*X.4)+X.1*X.4*cos(pi/3+time*X.4)))/3+(2*1.732050807568877*Lmd*d.X.2*sin(time*X.4))/3+(2*1.732050807568877*Lmd*d.X.1*sin(pi/3+time*X.4))/3+(2*1.732050807568877*Lmd*d.X.4*(X.2*time*cos(time*X.4)+X.1*time*cos(pi/3+time*X.4)))/3",
                "i.1+i.2+i.3=0",
                "i.4+i.5=0",
                "-T.1=T.2+"+M,
                "X.1=i.1",
                "X.2=i.2",
                "X.3=i.4",
                "X.4=w.1-w.2"
        };
        for(int i=0;i<out.length;i++){
            out[i]=out[i].replaceAll("Pp",Pp.toString());
            out[i]=out[i].replaceAll("Lmd",Lmd.toString());
            out[i]=out[i].replaceAll("Lmq",Lmq.toString());
            out[i]=out[i].replaceAll("Lls",Lls.toString());
            out[i]=out[i].replaceAll("Rf",Rf.toString());
            out[i]=out[i].replaceAll("Rs",Rs.toString());
            out[i]=out[i].replaceAll("Llf",Llf.toString());
            out[i]=out[i].replaceAll("pi",Double.toString(Math.PI));
        }
        return out;
    }

    @Override
    protected String getDescription() {
        return "Synchronous drive without damper winding";
    }

    @Override
    protected void setParams() {
        Rs=new Parameter("Stator resistance",0.00076);
        Lmd=new Parameter("d-axis mutual inductance",0.0005246);
        Lmq=new Parameter("q-axis mutual inductance",0.0003845);
        Lls=new Parameter("Stator leakage inductanse",1.273e-5);
        Rf=new Parameter("excitation winding resistance",0.0001576);
        Llf=new Parameter("rotor leakage inductance",8.703e-5);
        Pp=new Parameter("pole pairs",2);
        J=new Parameter("Inertia",49.81);
        F=new Parameter("Friction",0.8);

        getParameters().addAll(List.of(Rs,Lmd,Lmq,Lls,Rf,Llf,Pp,J,F));

        getInitials().add(new InitParam("Stator phase A current",0));
        getInitials().add(new InitParam("Stator phase B current",0));
        getInitials().add(new InitParam("Rotor current",0));
        getInitials().add(new InitParam("Rotor speed",0));

        setName("Synchronous machine");
    }
}
