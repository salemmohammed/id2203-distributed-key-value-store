package system.coordination.rsm.event;

import se.sics.kompics.KompicsEvent;
import system.client.event.Command;

public class ExecuteCommand implements KompicsEvent{


    Command command;

    public ExecuteCommand(Command command) {
        this.command = command;

    }

    public Command getCommand() {
        return command;
    }

}
