package system.coordination.meld;

import se.sics.kompics.PortType;
import system.coordination.meld.event.CheckLeader;
import system.coordination.meld.event.Trust;

public class MELDPort extends PortType {
    {
        indication(Trust.class);
        request(CheckLeader.class);
    }
}
