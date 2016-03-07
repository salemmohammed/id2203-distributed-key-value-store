package system.riwm.port;

import se.sics.kompics.PortType;
import system.riwm.event.*;

public class RIWMPort extends PortType{
    {
        request(InitReadRequest.class);
        request(InitWriteRequest.class);

        indication(ReadReturn.class);
        indication(WriteReturn.class);
    }
}
