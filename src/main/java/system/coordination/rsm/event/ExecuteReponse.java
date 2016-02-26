package system.coordination.rsm.event;

import se.sics.kompics.KompicsEvent;
import system.client.event.Command;

public class ExecuteReponse implements KompicsEvent {

    Command command;

    public ExecuteReponse(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }
}
