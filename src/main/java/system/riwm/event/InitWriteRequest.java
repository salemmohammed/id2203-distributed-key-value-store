package system.riwm.event;

import se.sics.kompics.KompicsEvent;
import system.data.KVEntry;

public class InitWriteRequest implements KompicsEvent{

    private KVEntry kv;

    public InitWriteRequest(KVEntry kv) {
        this.kv = kv;
    }

    public KVEntry getKVEntry() {
        return this.kv;
    }
}
