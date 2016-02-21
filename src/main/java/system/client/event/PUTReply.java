package system.client.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.THeader;
import system.network.TMessage;

/**
 * Created by Robin on 2016-02-13.
 */
public class PUTReply extends TMessage {

    private Integer key;
    private Integer value;


    public PUTReply(TAddress src, TAddress dst, Integer key, Integer value) {
        super(src, dst, Transport.TCP);
        this.key = key;
        this.value = value;
    }

    public PUTReply(THeader header) {
        super(header);
    }
    public Integer getKeyValue() {
        return value;
    }
    public Integer getKey() {
        return key;
    }
}
