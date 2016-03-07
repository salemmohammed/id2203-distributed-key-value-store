package system.rsm.port;

import se.sics.kompics.PortType;
import system.rsm.event.ExecuteCommand;
import system.rsm.event.ExecuteReponse;

public class RSMPort extends PortType {
    {
        request(ExecuteCommand.class);
        indication(ExecuteReponse.class);
    }
}
