package system.epfd.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.THeader;
import system.network.TMessage;

import java.io.Serializable;

/**
 * Created by Robin on 2016-02-07.
 */
public class HeartbeatRequest extends TMessage implements Serializable {
    public HeartbeatRequest(TAddress src, TAddress dst) {
        super(src, dst, Transport.TCP);
    }

    public HeartbeatRequest(THeader header) {
        super(header);
    }
}
