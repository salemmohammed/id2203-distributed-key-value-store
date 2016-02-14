package system.port.pp2p;

import se.sics.kompics.PortType;

/**
 * Created by marcus on 14/02/16.
 */
public class PerfectPointToPointLink extends PortType {
    {
        request(Pp2pSend.class);
        indication(Pp2pDeliver.class);

    }
}
