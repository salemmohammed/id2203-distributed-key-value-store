package system.epfd.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.THeader;
import system.network.TMessage;

import java.io.Serializable;

public class HeartbeatReply extends TMessage implements Serializable {
    public HeartbeatReply(TAddress src, TAddress dst) {
        super(src, dst, Transport.TCP);
    }

    public HeartbeatReply(THeader header) {
        super(header);
    }
}
