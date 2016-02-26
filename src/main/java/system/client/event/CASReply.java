package system.client.event;

import system.KVEntry;
import system.network.TAddress;

public class CASReply extends Command{

    private KVEntry kv;
    private int oldValue;
    public boolean successful;


    public CASReply(TAddress src, TAddress dst, KVEntry kv, int oldValue) {
        super(src, dst);
        this.kv = kv;
        this.oldValue = oldValue;
    }

    public KVEntry getKVEntry() {

        return kv;
    }

    public int getOldValue() {
        return oldValue;
    }
}
