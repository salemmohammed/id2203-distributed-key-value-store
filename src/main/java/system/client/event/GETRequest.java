package system.client.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.THeader;
import system.network.TMessage;

/**
 * Created by Robin on 2016-02-13.
 */
public class GETRequest extends TMessage {

    public GETRequest(TAddress src, TAddress dst) {
        super(src, dst, Transport.TCP);
    }

    public GETRequest(THeader header) {
        super(header);
    }
}
