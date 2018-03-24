package ElementBase;

import Connections.LineMarker;
import Elements.Environment.Subsystem.Subsystem;

public interface Pass{
    void setPass(Subsystem oldSys, LineMarker lm);

    Pin getOutside();
}
