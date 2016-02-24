package system.coordination.paxos.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

/**
 * Created by Robin on 2016-02-24.
 */
public class Nack extends TMessage {

    public Nack(TAddress source, TAddress destination) {
        super(source, destination, Transport.TCP);
    }
}
