package system.coordination.event;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Transport;
import system.KVEntry;
import system.network.TAddress;
import system.network.TMessage;

/**
 * Created by marcus on 17/02/16.
 */

public class WriteRequest implements KompicsEvent{

    private KVEntry kv;

    public int getWts() {
        return wts;
    }

    public void setWts(int wts) {
        this.wts = wts;
    }

    private int wts;

    public WriteRequest(KVEntry kv, int wts) {
        this.kv = kv;
        this.wts = wts;
    }

    public KVEntry getKVEntry() {
        return this.kv;
    }
}
