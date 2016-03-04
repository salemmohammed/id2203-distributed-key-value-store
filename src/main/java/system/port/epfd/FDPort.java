package system.port.epfd;

import se.sics.kompics.PortType;
import system.epfd.event.Restore;
import system.epfd.event.Suspect;

public class FDPort extends PortType {
    {
        indication(Suspect.class);
        indication(Restore.class);
    }
}
