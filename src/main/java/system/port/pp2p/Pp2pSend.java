package system.port.pp2p;

import se.sics.kompics.KompicsEvent;
import system.network.TAddress;

/**
 * Created by marcus on 14/02/16.
 */
public final class Pp2pSend implements KompicsEvent {

    private final Pp2pDeliver deliverEvent;
    private final TAddress destination;

    public Pp2pSend(TAddress destination, Pp2pDeliver pp2pDeliver) {
        this.destination = destination;
        this.deliverEvent = pp2pDeliver;
    }

    public final Pp2pDeliver getDeliverEvent() {
        return deliverEvent;
    }

    public final TAddress getDestination() {
        return destination;
    }

}
