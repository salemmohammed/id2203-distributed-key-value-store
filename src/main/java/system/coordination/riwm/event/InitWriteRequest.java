package system.coordination.riwm.event;

import se.sics.kompics.KompicsEvent;
import system.KVEntry;

/**
 * Created by marcus on 17/02/16.
 */

public class InitWriteRequest implements KompicsEvent{

    private KVEntry kv;

    public InitWriteRequest(KVEntry kv) {
        this.kv = kv;
    }

    public KVEntry getKVEntry() {
        return this.kv;
    }
}
