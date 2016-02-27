package system.coordination.paxos.port;

import se.sics.kompics.PortType;
import system.coordination.paxos.event.AscAbort;
import system.coordination.paxos.event.AscDecide;
import system.coordination.paxos.event.AscPropose;

public class ASCPort extends PortType {
    {
        request(AscPropose.class);
        indication(AscDecide.class);
        indication(AscAbort.class);
    }
}
