package system.epfd.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.THeader;
import system.network.TMessage;

/**
 * Created by Robin on 2016-02-07.
 */
public class HeartbeatReply extends TMessage {
    public HeartbeatReply(TAddress src, TAddress dst) {
        super(src, dst, Transport.TCP);
    }

    public HeartbeatReply(THeader header) {
        super(header);
    }
}
