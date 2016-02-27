package system.client.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

public class Command extends TMessage{

    public Command(TAddress source, TAddress destination) {
        super(source, destination, Transport.TCP);
    }

    public void setDestination(TAddress destination) {
        super.getHeader().dst = destination;
    }
}
