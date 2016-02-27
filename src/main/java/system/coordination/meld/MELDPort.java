package system.coordination.meld;

import se.sics.kompics.PortType;
import system.coordination.meld.event.Trust;

/**
 * Created by Robin on 2016-02-27.
 */
public class MELDPort extends PortType {
    {
        indication(Trust.class);
    }
}
