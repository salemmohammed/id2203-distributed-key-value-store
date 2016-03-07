package system.epfd.event;

import se.sics.kompics.KompicsEvent;
import system.network.TAddress;

public class Suspect implements KompicsEvent {

    private TAddress node;

    public Suspect(TAddress node) {
        this.node = node;
    }

    public TAddress getNode() {
        return node;
    }
}
