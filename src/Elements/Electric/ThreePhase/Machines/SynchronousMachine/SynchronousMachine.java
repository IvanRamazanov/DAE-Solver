package Elements.Electric.ThreePhase.Machines.SynchronousMachine;

import ElementBase.ElectricPin;
import ElementBase.MechPin;
import ElementBase.SchemeElement;
import ElementBase.ThreePhasePin;
import Elements.Environment.Subsystem.Subsystem;

public class SynchronousMachine extends SchemeElement{
    ScalarParameter Lls,Lmd,Lmq,Pp,Rf,Rs,Llf,J,F;
    
    public SynchronousMachine(Subsystem sys){
        super(sys);

        addElectricCont(new ElectricPin(this, 9, 63));  // Field
        addElectricCont(new ElectricPin(this, 40, 63));
        addThreePhaseCont(new ThreePhasePin(this,25,4)); // ABC

        addMechCont(new MechPin(this,45,35));   // positive
        addMechCont(new MechPin(this,4,35));    // ref

//        addMathContact('o');
    }
    
    public SynchronousMachine(boolean val){
        super(val);
    }

    @Override
    public String[] getStringFunction() {
        String M="(3*Pp*(((Lls+Lmd)*((2*X.1*cos(Pp*X.5))/3+(2*X.2*cos((2*pi)/3-Pp*X.5))/3-(2*cos((2*pi)/3+Pp*X.5)*(X.1+X.2))/3)+X.3*Lmd)*((2*X.2*sin((2*pi)/3-Pp*X.5))/3-(2*X.1*sin(Pp*X.5))/3+(2*sin((2*pi)/3+Pp*X.5)*(X.1+X.2))/3)-(Lls+Lmq)*((2*X.1*cos(Pp*X.5))/3+(2*X.2*cos((2*pi)/3-Pp*X.5))/3-(2*cos((2*pi)/3+Pp*X.5)*(X.1+X.2))/3)*((2*X.2*sin((2*pi)/3-Pp*X.5))/3-(2*X.1*sin(Pp*X.5))/3+(2*sin((2*pi)/3+Pp*X.5)*(X.1+X.2))/3)))/2";
        String[] out={
//                "=Rs*((2*X.1*cos(Pp*X.5))/3+(2*X.2*cos((2*pi)/3-Pp*X.5))/3-(2*cos((2*pi)/3+Pp*X.5)*(X.1+X.2))/3)+Lmd*d.X.3-(2*p.3*cos(Pp*X.5))/3-(2*p.4*cos((2*pi)/3-Pp*X.5))/3-(2*p.5*cos((2*pi)/3+Pp*X.5))/3+X.4*(Lls+Lmd)*((2*Pp*sin((2*pi)/3+Pp*X.5)*(X.1+X.2))/3-(2*X.1*Pp*sin(Pp*X.5))/3+(2*X.2*Pp*sin((2*pi)/3-Pp*X.5))/3)-X.4*(Lls+Lmq)*((2*X.2*sin((2*pi)/3-Pp*X.5))/3-(2*X.1*sin(Pp*X.5))/3+(2*sin((2*pi)/3+Pp*X.5)*(X.1+X.2))/3)+d.X.1*(Lls+Lmd)*((2*cos(Pp*X.5))/3-(2*cos((2*pi)/3+Pp*X.5))/3)-d.X.2*((2*cos((2*pi)/3+Pp*X.5))/3-(2*cos((2*pi)/3-Pp*X.5))/3)*(Lls+Lmd)",
//                "=(2*p.3*sin(Pp*X.5))/3+Rs*((2*X.2*sin((2*pi)/3-Pp*X.5))/3-(2*X.1*sin(Pp*X.5))/3+(2*sin((2*pi)/3+Pp*X.5)*(X.1+X.2))/3)-(2*p.4*sin((2*pi)/3-Pp*X.5))/3+(2*p.5*sin((2*pi)/3+Pp*X.5))/3+X.4*((Lls+Lmd)*((2*X.1*cos(Pp*X.5))/3+(2*X.2*cos((2*pi)/3-Pp*X.5))/3-(2*cos((2*pi)/3+Pp*X.5)*(X.1+X.2))/3)+X.3*Lmd)-X.4*(Lls+Lmq)*((2*X.1*Pp*cos(Pp*X.5))/3-(2*Pp*cos((2*pi)/3+Pp*X.5)*(X.1+X.2))/3+(2*X.2*Pp*cos((2*pi)/3-Pp*X.5))/3)-d.X.1*((2*sin(Pp*X.5))/3-(2*sin((2*pi)/3+Pp*X.5))/3)*(Lls+Lmq)+d.X.2*((2*sin((2*pi)/3+Pp*X.5))/3+(2*sin((2*pi)/3-Pp*X.5))/3)*(Lls+Lmq)",
//                "p.1-p.2=d.X.3*(Llf+Lmd)+X.3*Rf+Lmd*d.X.1*((2*cos(Pp*X.5))/3-(2*cos((2*pi)/3+Pp*X.5))/3)-Lmd*d.X.2*((2*cos((2*pi)/3+Pp*X.5))/3-(2*cos((2*pi)/3-Pp*X.5))/3)+Lmd*X.4*((2*Pp*sin((2*pi)/3+Pp*X.5)*(X.1+X.2))/3-(2*X.1*Pp*sin(Pp*X.5))/3+(2*X.2*Pp*sin((2*pi)/3-Pp*X.5))/3)",
//                "d.X.4=("+M+"-T.1-"+F.toString()+"*X.4)/"+J.toString(),
//                "d.X.5=X.4",
//                "i.3+i.4+i.5=0",
//                "i.1+i.2=0",
//                "T.1+T.2=0",
//                "X.1=i.3",
//                "X.2=i.4",
//                "X.3=i.1",
//                "X.4=w.1-w.2"




                "d.X.1=-(2*Llf*Lls*p.4-2*Llf*Lmd*p.3-2*Llf*Lmq*p.3-4*Lls*Lmd*p.3-4*Llf*Lls*p.3+Llf*Lmd*p.4-2*Lmd*Lmq*p.3+Llf*Lmq*p.4+2*Lls*Lmd*p.4+2*Llf*Lls*p.5+Llf*Lmd*p.5+Lmd*Lmq*p.4+Llf*Lmq*p.5+2*Lls*Lmd*p.5+Lmd*Lmq*p.5+6*X.1*Llf*Lls*Rs+3*X.1*Llf*Lmd*Rs+3*X.1*Llf*Lmq*Rs+6*X.1*Lls*Lmd*Rs+3*X.1*Lmd*Lmq*Rs+2*Llf*Lmd*p.3*cos(2*Pp*X.5)-2*Llf*Lmq*p.3*cos(2*Pp*X.5)-Llf*Lmd*p.4*cos(2*Pp*X.5)-2*Lmd*Lmq*p.3*cos(2*Pp*X.5)+Llf*Lmq*p.4*cos(2*Pp*X.5)-Llf*Lmd*p.5*cos(2*Pp*X.5)+Lmd*Lmq*p.4*cos(2*Pp*X.5)+Llf*Lmq*p.5*cos(2*Pp*X.5)+Lmd*Lmq*p.5*cos(2*Pp*X.5)+6*Lls*Lmd*p.1*cos(Pp*X.5)-6*Lls*Lmd*p.2*cos(Pp*X.5)+6*Lmd*Lmq*p.1*cos(Pp*X.5)-6*Lmd*Lmq*p.2*cos(Pp*X.5)+3^(1/2)*Llf*Lmd*p.4*sin(2*Pp*X.5)-3^(1/2)*Llf*Lmq*p.4*sin(2*Pp*X.5)-3^(1/2)*Llf*Lmd*p.5*sin(2*Pp*X.5)-3^(1/2)*Lmd*Lmq*p.4*sin(2*Pp*X.5)+3^(1/2)*Llf*Lmq*p.5*sin(2*Pp*X.5)+3^(1/2)*Lmd*Lmq*p.5*sin(2*Pp*X.5)-2*3^(1/2)*X.1*Llf*Lls^2*X.4-3^(1/2)*X.1*Llf*Lmd^2*X.4-3^(1/2)*X.1*Llf*Lmq^2*X.4-3^(1/2)*X.1*Lls*Lmd^2*X.4-2*3^(1/2)*X.1*Lls^2*Lmd*X.4-4*3^(1/2)*X.2*Llf*Lls^2*X.4-2*3^(1/2)*X.2*Llf*Lmd^2*X.4-3^(1/2)*X.1*Lmd*Lmq^2*X.4-2*3^(1/2)*X.2*Llf*Lmq^2*X.4-2*3^(1/2)*X.2*Lls*Lmd^2*X.4-4*3^(1/2)*X.2*Lls^2*Lmd*X.4-2*3^(1/2)*X.2*Lmd*Lmq^2*X.4-3*X.1*Llf*Lmd^2*X.4*sin(2*Pp*X.5)+3*X.1*Llf*Lmq^2*X.4*sin(2*Pp*X.5)-3*X.1*Lls*Lmd^2*X.4*sin(2*Pp*X.5)+3*X.1*Lmd*Lmq^2*X.4*sin(2*Pp*X.5)-6*X.3*Llf*Lmd^2*X.4*sin(Pp*X.5)-6*X.3*Lls*Lmd^2*X.4*sin(Pp*X.5)-6*X.3*Lls*Lmd*Rf*cos(Pp*X.5)-6*X.3*Lmd*Lmq*Rf*cos(Pp*X.5)-3*X.1*Llf*Lmd*Rs*cos(2*Pp*X.5)+3*X.1*Llf*Lmq*Rs*cos(2*Pp*X.5)+3*X.1*Lmd*Lmq*Rs*cos(2*Pp*X.5)-2*3^(1/2)*X.1*Llf*Lls*Lmd*X.4-2*3^(1/2)*X.1*Llf*Lls*Lmq*X.4-4*3^(1/2)*X.2*Llf*Lls*Lmd*X.4-2*3^(1/2)*X.1*Lls*Lmd*Lmq*X.4-4*3^(1/2)*X.2*Llf*Lls*Lmq*X.4-4*3^(1/2)*X.2*Lls*Lmd*Lmq*X.4-3^(1/2)*X.1*Llf*Lmd*Rs*sin(2*Pp*X.5)+3^(1/2)*X.1*Llf*Lmq*Rs*sin(2*Pp*X.5)-2*3^(1/2)*X.2*Llf*Lmd*Rs*sin(2*Pp*X.5)+3^(1/2)*X.1*Lmd*Lmq*Rs*sin(2*Pp*X.5)+2*3^(1/2)*X.2*Llf*Lmq*Rs*sin(2*Pp*X.5)+2*3^(1/2)*X.2*Lmd*Lmq*Rs*sin(2*Pp*X.5)-6*X.1*Llf*Lls*Lmd*X.4*sin(2*Pp*X.5)+6*X.1*Llf*Lls*Lmq*X.4*sin(2*Pp*X.5)+6*X.1*Lls*Lmd*Lmq*X.4*sin(2*Pp*X.5)-6*X.3*Llf*Lls*Lmd*X.4*sin(Pp*X.5)+2*3^(1/2)*X.1*Llf*Lls^2*Pp*X.4+2*3^(1/2)*X.1*Lls^2*Lmd*Pp*X.4+4*3^(1/2)*X.2*Llf*Lls^2*Pp*X.4+4*3^(1/2)*X.2*Lls^2*Lmd*Pp*X.4+3^(1/2)*X.1*Llf*Lmd^2*X.4*cos(2*Pp*X.5)-3^(1/2)*X.1*Llf*Lmq^2*X.4*cos(2*Pp*X.5)+3^(1/2)*X.1*Lls*Lmd^2*X.4*cos(2*Pp*X.5)+2*3^(1/2)*X.2*Llf*Lmd^2*X.4*cos(2*Pp*X.5)-3^(1/2)*X.1*Lmd*Lmq^2*X.4*cos(2*Pp*X.5)-2*3^(1/2)*X.2*Llf*Lmq^2*X.4*cos(2*Pp*X.5)+2*3^(1/2)*X.2*Lls*Lmd^2*X.4*cos(2*Pp*X.5)-2*3^(1/2)*X.2*Lmd*Lmq^2*X.4*cos(2*Pp*X.5)+2*3^(1/2)*X.1*Llf*Lls*Lmd*Pp*X.4+2*3^(1/2)*X.1*Llf*Lls*Lmq*Pp*X.4+2*3^(1/2)*X.1*Llf*Lmd*Lmq*Pp*X.4+4*3^(1/2)*X.2*Llf*Lls*Lmd*Pp*X.4+2*3^(1/2)*X.1*Lls*Lmd*Lmq*Pp*X.4+4*3^(1/2)*X.2*Llf*Lls*Lmq*Pp*X.4+4*3^(1/2)*X.2*Llf*Lmd*Lmq*Pp*X.4+4*3^(1/2)*X.2*Lls*Lmd*Lmq*Pp*X.4+2*3^(1/2)*X.1*Llf*Lls*Lmd*X.4*cos(2*Pp*X.5)-2*3^(1/2)*X.1*Llf*Lls*Lmq*X.4*cos(2*Pp*X.5)+4*3^(1/2)*X.2*Llf*Lls*Lmd*X.4*cos(2*Pp*X.5)-2*3^(1/2)*X.1*Lls*Lmd*Lmq*X.4*cos(2*Pp*X.5)-4*3^(1/2)*X.2*Llf*Lls*Lmq*X.4*cos(2*Pp*X.5)-4*3^(1/2)*X.2*Lls*Lmd*Lmq*X.4*cos(2*Pp*X.5))/(6*(Lls+Lmq)*(Llf*Lls+Llf*Lmd+Lls*Lmd))",
                "d.X.2=-(2*Llf*Lls*p.3+Llf*Lmd*p.3+Llf*Lmq*p.3+2*Lls*Lmd*p.3-4*Llf*Lls*p.4-2*Llf*Lmd*p.4+Lmd*Lmq*p.3-2*Llf*Lmq*p.4-4*Lls*Lmd*p.4+2*Llf*Lls*p.5+Llf*Lmd*p.5-2*Lmd*Lmq*p.4+Llf*Lmq*p.5+2*Lls*Lmd*p.5+Lmd*Lmq*p.5+6*X.2*Llf*Lls*Rs+3*X.2*Llf*Lmd*Rs+3*X.2*Llf*Lmq*Rs+6*X.2*Lls*Lmd*Rs+3*X.2*Lmd*Lmq*Rs-Llf*Lmd*p.3*cos(2*Pp*X.5)+Llf*Lmq*p.3*cos(2*Pp*X.5)-Llf*Lmd*p.4*cos(2*Pp*X.5)+Lmd*Lmq*p.3*cos(2*Pp*X.5)+Llf*Lmq*p.4*cos(2*Pp*X.5)+2*Llf*Lmd*p.5*cos(2*Pp*X.5)+Lmd*Lmq*p.4*cos(2*Pp*X.5)-2*Llf*Lmq*p.5*cos(2*Pp*X.5)-2*Lmd*Lmq*p.5*cos(2*Pp*X.5)-3*Lls*Lmd*p.1*cos(Pp*X.5)+3*Lls*Lmd*p.2*cos(Pp*X.5)-3*Lmd*Lmq*p.1*cos(Pp*X.5)+3*Lmd*Lmq*p.2*cos(Pp*X.5)+3^(1/2)*Llf*Lmd*p.3*sin(2*Pp*X.5)-3^(1/2)*Llf*Lmq*p.3*sin(2*Pp*X.5)-3^(1/2)*Llf*Lmd*p.4*sin(2*Pp*X.5)-3^(1/2)*Lmd*Lmq*p.3*sin(2*Pp*X.5)+3^(1/2)*Llf*Lmq*p.4*sin(2*Pp*X.5)+3^(1/2)*Lmd*Lmq*p.4*sin(2*Pp*X.5)+3*3^(1/2)*Lls*Lmd*p.1*sin(Pp*X.5)-3*3^(1/2)*Lls*Lmd*p.2*sin(Pp*X.5)+3*3^(1/2)*Lmd*Lmq*p.1*sin(Pp*X.5)-3*3^(1/2)*Lmd*Lmq*p.2*sin(Pp*X.5)+4*3^(1/2)*X.1*Llf*Lls^2*X.4+2*3^(1/2)*X.1*Llf*Lmd^2*X.4+2*3^(1/2)*X.1*Llf*Lmq^2*X.4+2*3^(1/2)*X.1*Lls*Lmd^2*X.4+4*3^(1/2)*X.1*Lls^2*Lmd*X.4+2*3^(1/2)*X.2*Llf*Lls^2*X.4+3^(1/2)*X.2*Llf*Lmd^2*X.4+2*3^(1/2)*X.1*Lmd*Lmq^2*X.4+3^(1/2)*X.2*Llf*Lmq^2*X.4+3^(1/2)*X.2*Lls*Lmd^2*X.4+2*3^(1/2)*X.2*Lls^2*Lmd*X.4+3^(1/2)*X.2*Lmd*Lmq^2*X.4+3*X.1*Llf*Lmd^2*X.4*sin(2*Pp*X.5)-3*X.1*Llf*Lmq^2*X.4*sin(2*Pp*X.5)+3*X.1*Lls*Lmd^2*X.4*sin(2*Pp*X.5)+3*X.2*Llf*Lmd^2*X.4*sin(2*Pp*X.5)-3*X.1*Lmd*Lmq^2*X.4*sin(2*Pp*X.5)-3*X.2*Llf*Lmq^2*X.4*sin(2*Pp*X.5)+3*X.2*Lls*Lmd^2*X.4*sin(2*Pp*X.5)-3*X.2*Lmd*Lmq^2*X.4*sin(2*Pp*X.5)+3*X.3*Llf*Lmd^2*X.4*sin(Pp*X.5)+3*X.3*Lls*Lmd^2*X.4*sin(Pp*X.5)+3*X.3*Lls*Lmd*Rf*cos(Pp*X.5)+3*X.3*Lmd*Lmq*Rf*cos(Pp*X.5)+3*X.1*Llf*Lmd*Rs*cos(2*Pp*X.5)-3*X.1*Llf*Lmq*Rs*cos(2*Pp*X.5)+3*X.2*Llf*Lmd*Rs*cos(2*Pp*X.5)-3*X.1*Lmd*Lmq*Rs*cos(2*Pp*X.5)-3*X.2*Llf*Lmq*Rs*cos(2*Pp*X.5)-3*X.2*Lmd*Lmq*Rs*cos(2*Pp*X.5)+4*3^(1/2)*X.1*Llf*Lls*Lmd*X.4+4*3^(1/2)*X.1*Llf*Lls*Lmq*X.4+2*3^(1/2)*X.2*Llf*Lls*Lmd*X.4+4*3^(1/2)*X.1*Lls*Lmd*Lmq*X.4+2*3^(1/2)*X.2*Llf*Lls*Lmq*X.4+2*3^(1/2)*X.2*Lls*Lmd*Lmq*X.4-3*3^(1/2)*X.3*Lls*Lmd*Rf*sin(Pp*X.5)-3*3^(1/2)*X.3*Lmd*Lmq*Rf*sin(Pp*X.5)-3^(1/2)*X.1*Llf*Lmd*Rs*sin(2*Pp*X.5)+3^(1/2)*X.1*Llf*Lmq*Rs*sin(2*Pp*X.5)+3^(1/2)*X.2*Llf*Lmd*Rs*sin(2*Pp*X.5)+3^(1/2)*X.1*Lmd*Lmq*Rs*sin(2*Pp*X.5)-3^(1/2)*X.2*Llf*Lmq*Rs*sin(2*Pp*X.5)-3^(1/2)*X.2*Lmd*Lmq*Rs*sin(2*Pp*X.5)+6*X.1*Llf*Lls*Lmd*X.4*sin(2*Pp*X.5)-6*X.1*Llf*Lls*Lmq*X.4*sin(2*Pp*X.5)+6*X.2*Llf*Lls*Lmd*X.4*sin(2*Pp*X.5)-6*X.1*Lls*Lmd*Lmq*X.4*sin(2*Pp*X.5)-6*X.2*Llf*Lls*Lmq*X.4*sin(2*Pp*X.5)-6*X.2*Lls*Lmd*Lmq*X.4*sin(2*Pp*X.5)+3*X.3*Llf*Lls*Lmd*X.4*sin(Pp*X.5)-4*3^(1/2)*X.1*Llf*Lls^2*Pp*X.4-4*3^(1/2)*X.1*Lls^2*Lmd*Pp*X.4-2*3^(1/2)*X.2*Llf*Lls^2*Pp*X.4-2*3^(1/2)*X.2*Lls^2*Lmd*Pp*X.4+3^(1/2)*X.1*Llf*Lmd^2*X.4*cos(2*Pp*X.5)-3^(1/2)*X.1*Llf*Lmq^2*X.4*cos(2*Pp*X.5)+3^(1/2)*X.1*Lls*Lmd^2*X.4*cos(2*Pp*X.5)-3^(1/2)*X.2*Llf*Lmd^2*X.4*cos(2*Pp*X.5)-3^(1/2)*X.1*Lmd*Lmq^2*X.4*cos(2*Pp*X.5)+3^(1/2)*X.2*Llf*Lmq^2*X.4*cos(2*Pp*X.5)-3^(1/2)*X.2*Lls*Lmd^2*X.4*cos(2*Pp*X.5)+3^(1/2)*X.2*Lmd*Lmq^2*X.4*cos(2*Pp*X.5)+3*3^(1/2)*X.3*Llf*Lmd^2*X.4*cos(Pp*X.5)+3*3^(1/2)*X.3*Lls*Lmd^2*X.4*cos(Pp*X.5)-4*3^(1/2)*X.1*Llf*Lls*Lmd*Pp*X.4-4*3^(1/2)*X.1*Llf*Lls*Lmq*Pp*X.4-4*3^(1/2)*X.1*Llf*Lmd*Lmq*Pp*X.4-2*3^(1/2)*X.2*Llf*Lls*Lmd*Pp*X.4-4*3^(1/2)*X.1*Lls*Lmd*Lmq*Pp*X.4-2*3^(1/2)*X.2*Llf*Lls*Lmq*Pp*X.4-2*3^(1/2)*X.2*Llf*Lmd*Lmq*Pp*X.4-2*3^(1/2)*X.2*Lls*Lmd*Lmq*Pp*X.4+2*3^(1/2)*X.1*Llf*Lls*Lmd*X.4*cos(2*Pp*X.5)-2*3^(1/2)*X.1*Llf*Lls*Lmq*X.4*cos(2*Pp*X.5)-2*3^(1/2)*X.2*Llf*Lls*Lmd*X.4*cos(2*Pp*X.5)-2*3^(1/2)*X.1*Lls*Lmd*Lmq*X.4*cos(2*Pp*X.5)+2*3^(1/2)*X.2*Llf*Lls*Lmq*X.4*cos(2*Pp*X.5)+2*3^(1/2)*X.2*Lls*Lmd*Lmq*X.4*cos(2*Pp*X.5)+3*3^(1/2)*X.3*Llf*Lls*Lmd*X.4*cos(Pp*X.5))/(6*(Lls+Lmq)*(Llf*Lls+Llf*Lmd+Lls*Lmd))",
                "d.X.3=(3*Lls*p.1-3*Lls*p.2+3*Lmd*p.1-3*Lmd*p.2-3*X.3*Lls*Rf-3*X.3*Lmd*Rf-2*Lmd*p.3*cos(Pp*X.5)+Lmd*p.4*cos(Pp*X.5)+Lmd*p.5*cos(Pp*X.5)+3*X.1*Lmd*Rs*cos(Pp*X.5)-3^(1/2)*Lmd*p.4*sin(Pp*X.5)+3^(1/2)*Lmd*p.5*sin(Pp*X.5)+3^(1/2)*X.1*Lmd*Rs*sin(Pp*X.5)+2*3^(1/2)*X.2*Lmd*Rs*sin(Pp*X.5)+3*X.1*Lls*Lmd*X.4*sin(Pp*X.5)+3*X.1*Lmd*Lmq*X.4*sin(Pp*X.5)-3^(1/2)*X.1*Lls*Lmd*X.4*cos(Pp*X.5)-3^(1/2)*X.1*Lmd*Lmq*X.4*cos(Pp*X.5)-2*3^(1/2)*X.2*Lls*Lmd*X.4*cos(Pp*X.5)-2*3^(1/2)*X.2*Lmd*Lmq*X.4*cos(Pp*X.5))/(3*(Llf*Lls+Llf*Lmd+Lls*Lmd))",
                "d.X.4=("+M+"-T.1-"+F.toString()+"*X.4)/"+J.toString(),
                "d.X.5=X.4",
                "i.3+i.4+i.5=0",
                "i.1+i.2=0",
                "T.1+T.2=0",
                "X.1=i.3",
                "X.2=i.4",
                "X.3=i.1",
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
        getParameters().add(Rs=new ScalarParameter("Stator resistance",0.00076));
        getParameters().add(Lmd=new ScalarParameter("d-axis mutual inductance",0.0005246));
        getParameters().add(Lmq=new ScalarParameter("q-axis mutual inductance",0.0003845));
        getParameters().add(Lls=new ScalarParameter("Stator leakage inductanse",1.273e-5));
        getParameters().add(Rf=new ScalarParameter("excitation winding resistance",0.0001576));
        getParameters().add(Llf=new ScalarParameter("rotor leakage inductance",8.703e-5));
        getParameters().add(Pp=new ScalarParameter("pole pairs",2));
        getParameters().add(J=new ScalarParameter("Inertia",49.81));
        getParameters().add(F=new ScalarParameter("Friction",0.8));

        getInitials().add(new InitParam("Stator phase A current",0));
        getInitials().add(new InitParam("Stator phase B current",0));
        getInitials().add(new InitParam("Rotor current",0));
        getInitials().add(new InitParam("Rotor speed",0));
        getInitials().add(new InitParam("Rotor angle",0));

        setName("Synchronous machine");
    }
}
