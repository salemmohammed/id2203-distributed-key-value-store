package system.coordination.port;

import se.sics.kompics.PortType;
import system.coordination.event.ReadResponseMessage;

/**
 * Created by marcus on 17/02/16.
 */
public class RIWMPort extends PortType{
    {
        indication(ReadResponseMessage.class);
    }
}
