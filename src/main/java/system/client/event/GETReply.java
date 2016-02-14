package system.client.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.THeader;
import system.network.TMessage;

/**
 * Created by Robin on 2016-02-13.
 */
public class GETReply extends TMessage {

    private KeyValuePair keyValue;

    public GETReply(TAddress src, TAddress dst, KeyValuePair keyValue) {
        super(src, dst, Transport.TCP);
        this.keyValue = keyValue;
    }

    public GETReply(THeader header) {
        super(header);
    }

    public KeyValuePair getKeyValue() {
        return keyValue;
    }
}
