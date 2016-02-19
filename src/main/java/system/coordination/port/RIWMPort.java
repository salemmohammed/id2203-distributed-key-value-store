package system.coordination.port;

import se.sics.kompics.PortType;
import system.coordination.event.*;

/**
 * Created by marcus on 17/02/16.
 */
public class RIWMPort extends PortType{
    {
        request(InitReadRequest.class);
        request(InitWriteRequest.class);

        indication(ReadReturn.class);
        indication(WriteReturn.class);
    }
}
