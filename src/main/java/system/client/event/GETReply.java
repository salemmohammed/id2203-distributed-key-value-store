package system.client.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.THeader;
import system.network.TMessage;

/**
 * Created by Robin on 2016-02-13.
 */
public class GETReply extends TMessage {

    public GETReply(TAddress src, TAddress dst) {
        super(src, dst, Transport.TCP);
    }

    public GETReply(THeader header) {
        super(header);
    }
}
