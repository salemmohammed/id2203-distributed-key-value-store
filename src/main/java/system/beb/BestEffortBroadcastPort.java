package system.beb;

import se.sics.kompics.PortType;
import system.beb.event.BebBroadcastRequest;
import system.beb.event.BebDeliver;

/**
 * Created by Robin on 2016-02-14.
 */
public class BestEffortBroadcastPort extends PortType {
    {
        indication(BebDeliver.class);
        request(BebBroadcastRequest.class);
    }
}