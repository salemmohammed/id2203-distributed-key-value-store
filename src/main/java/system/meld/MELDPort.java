package system.meld;

import se.sics.kompics.PortType;
import system.meld.event.CheckLeader;
import system.meld.event.Trust;

public class MELDPort extends PortType {
    {
        indication(Trust.class);
        request(CheckLeader.class);
    }
}
