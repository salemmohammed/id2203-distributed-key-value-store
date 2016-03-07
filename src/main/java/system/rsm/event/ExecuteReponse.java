package system.rsm.event;

import se.sics.kompics.KompicsEvent;
import client.event.CommandMessage;

public class ExecuteReponse implements KompicsEvent {

    CommandMessage commandMessage;

    public ExecuteReponse(CommandMessage commandMessage) {
        this.commandMessage = commandMessage;
    }

    public CommandMessage getCommandMessage() {
        return commandMessage;
    }
}
