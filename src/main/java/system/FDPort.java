package system;

import se.sics.kompics.PortType;
import system.event.Restore;
import system.event.Suspect;

/**
 * Created by Robin on 2016-02-13.
 */
public class FDPort extends PortType {
    {
        indication(Suspect.class);
        indication(Restore.class);
    }
}
