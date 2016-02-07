package system.event;

import se.sics.kompics.network.Transport;
import system.TAddress;
import system.THeader;
import system.TMessage;

public class NodeMessage extends TMessage {

    public NodeMessage(TAddress src, TAddress dst) {
        super(src, dst, Transport.TCP);
    }

    public NodeMessage(THeader header) {
        super(header);
    }
}
