package system.epfd.event;

import se.sics.kompics.KompicsEvent;
import system.network.TAddress;

/**
 * Created by Robin on 2016-02-13.
 */
public class Restore implements KompicsEvent {

    private TAddress node;

    public Restore(TAddress node) {
        this.node = node;
    }

    public TAddress getNode() {
        return this.node;
    }
}
