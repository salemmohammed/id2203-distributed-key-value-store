package system.coordination.paxos.port;

import se.sics.kompics.PortType;
import system.coordination.paxos.event.Abort;
import system.coordination.paxos.event.AscDecide;
import system.coordination.paxos.event.AscPropose;

/**
 * Created by Robin on 2016-02-24.
 */
public class AbortableSequenceConsensusPort extends PortType {
    {
        request(AscPropose.class);
        indication(AscDecide.class);
        indication(Abort.class);
    }
}
