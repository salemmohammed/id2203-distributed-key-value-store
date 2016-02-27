package system.coordination.paxos.event;

import se.sics.kompics.KompicsEvent;
import system.client.event.Command;

public class AscAbort implements KompicsEvent {
    private Command command;

    public AscAbort(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return this.command;
    }
}
