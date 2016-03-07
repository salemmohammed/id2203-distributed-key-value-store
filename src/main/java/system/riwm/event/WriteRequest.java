package system.riwm.event;

import se.sics.kompics.KompicsEvent;
import system.data.KVEntry;

public class WriteRequest implements KompicsEvent{

    private KVEntry kv;

    public int getRid() {
        return rid;
    }

    public void setRid(int wts) {
        this.rid = wts;
    }

    private int rid;

    public WriteRequest(KVEntry kv, int rid) {
        this.kv = kv;
        this.rid = rid;
    }

    public KVEntry getKVEntry() {
        return this.kv;
    }
}
