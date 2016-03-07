package system.multipaxos.event;

import se.sics.kompics.KompicsEvent;
import client.event.CommandMessage;

public class AscPropose implements KompicsEvent {
    private CommandMessage proposal;

    public AscPropose(CommandMessage proposal) {
        this.proposal = proposal;
    }

    public CommandMessage getProposal() {
        return proposal;
    }

    public void setProposal(CommandMessage proposal) {
        this.proposal = proposal;
    }
}
