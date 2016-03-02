package system.coordination.paxos.event;

import se.sics.kompics.KompicsEvent;
import system.client.event.CommandMessage;

public class AscAbort implements KompicsEvent {
    private CommandMessage commandMessage;

    public AscAbort(CommandMessage commandMessage) {
        this.commandMessage = commandMessage;
    }

    public CommandMessage getCommandMessage() {
        return this.commandMessage;
    }
}
