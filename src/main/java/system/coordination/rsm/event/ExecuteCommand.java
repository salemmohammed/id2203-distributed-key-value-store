package system.coordination.rsm.event;

import se.sics.kompics.KompicsEvent;
import system.client.event.CommandMessage;

public class ExecuteCommand implements KompicsEvent{


    CommandMessage commandMessage;

    public ExecuteCommand(CommandMessage commandMessage) {
        this.commandMessage = commandMessage;

    }

    public CommandMessage getCommandMessage() {
        return commandMessage;
    }

}
