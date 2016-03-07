package system.multipaxos.port;

import se.sics.kompics.PortType;
import system.multipaxos.event.AscAbort;
import system.multipaxos.event.AscDecide;
import system.multipaxos.event.AscPropose;

public class ASCPort extends PortType {
    {
        request(AscPropose.class);
        indication(AscDecide.class);
        indication(AscAbort.class);
    }
}
