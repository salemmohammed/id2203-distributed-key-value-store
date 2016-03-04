package system.client.event;

import system.data.KVEntry;
import system.network.TAddress;

public class CASReply extends CommandMessage {

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


    public String toString() {
        return "{CASReply: key= " + kv.getKey() + ", value= " + kv.getValue() + ", oldValue= " + oldValue + ", successful= " + successful + "}";

    }
}
