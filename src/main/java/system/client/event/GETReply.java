package system.client.event;

import system.data.KVEntry;
import system.network.TAddress;

public class GETReply extends CommandMessage {

    private KVEntry kv;
    public boolean successful;


    public GETReply(TAddress src, TAddress dst, KVEntry kv) {
        super(src, dst);
        this.kv = kv;
    }

    public KVEntry getKVEntry() {

        return kv;
    }

    public String toString() {
        return "{GETReply: key= " + kv.getKey() + ", value= " + kv.getValue() + ", successful= " + successful + "}";

    }

}
