package system.coordination.paxos.event;

import se.sics.kompics.KompicsEvent;

/**
 * Created by Robin on 2016-02-24.
 */
public class AscPropose implements KompicsEvent {
    private Object proposal;

    public AscPropose(Object proposal) {
        this.proposal = proposal;
    }

    public Object getProposal() {
        return proposal;
    }

    public void setProposal(Object proposal) {
        this.proposal = proposal;
    }
}
