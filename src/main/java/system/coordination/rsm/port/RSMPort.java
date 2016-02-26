package system.coordination.rsm.port;

import se.sics.kompics.PortType;
import system.coordination.rsm.event.ExecuteCommand;
import system.coordination.rsm.event.ExecuteReponse;

public class RSMPort extends PortType {
    {
        request(ExecuteCommand.class);
        indication(ExecuteReponse.class);
    }
}
