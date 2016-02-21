package system.client.event;

import se.sics.kompics.network.Transport;
import system.KVEntry;
import system.network.TAddress;
import system.network.THeader;
import system.network.TMessage;

/**
 * Created by Robin on 2016-02-13.
 */
public class PUTRequest extends TMessage {

    private KVEntry kv;

    public PUTRequest(TAddress src, TAddress dst, KVEntry kv) {
        super(src, dst, Transport.TCP);
        this.kv = kv;
    }

    public PUTRequest(THeader header) {
        super(header);
    }

    public KVEntry getKv() {
        return kv;
    }
}
