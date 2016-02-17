package system.client.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.THeader;
import system.network.TMessage;

/**
 * Created by Robin on 2016-02-13.
 */
public class GETReply extends TMessage {

    private Integer value;

    public GETReply(TAddress src, TAddress dst, Integer value) {
        super(src, dst, Transport.TCP);
        this.value = value;
    }

    public GETReply(THeader header) {
        super(header);
    }
public Integer getKeyValue() {
        return value;
    }
}
