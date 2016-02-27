package system.coordination.meld.event;

import se.sics.kompics.KompicsEvent;
import system.network.TAddress;

public class Trust implements KompicsEvent {
    private TAddress leader;

    public Trust(TAddress leader) {
        this.leader = leader;
    }

    public TAddress getLeader() {
        return this.leader;
    }
}
