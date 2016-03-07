package system.multipaxos.event;

import se.sics.kompics.KompicsEvent;
import client.event.CommandMessage;

public class AscAbort implements KompicsEvent {
    private CommandMessage commandMessage;

    public AscAbort(CommandMessage commandMessage) {
        this.commandMessage = commandMessage;
    }

    public CommandMessage getCommandMessage() {
        return this.commandMessage;
    }
}
