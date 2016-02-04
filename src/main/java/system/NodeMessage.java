package system;

import se.sics.kompics.network.Transport;

public class NodeMessage extends TMessage {

    public NodeMessage(TAddress src, TAddress dst) {
        super(src, dst, Transport.TCP);
    }

    NodeMessage(THeader header) {
        super(header);
    }
}
