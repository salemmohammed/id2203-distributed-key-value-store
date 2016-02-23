package system.coordination.riwm.port;

import se.sics.kompics.PortType;
import system.coordination.riwm.event.*;

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
