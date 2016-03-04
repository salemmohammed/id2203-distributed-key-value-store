package system.client.event;

import system.data.KVEntry;
import system.network.TAddress;

public class PUTReply extends CommandMessage {

    private KVEntry kv;
    public boolean successful;

    public PUTReply(TAddress src, TAddress dst, KVEntry kv) {
        super(src, dst);
        this.kv = kv;
    }

    public KVEntry getKv() {
        return kv;
    }


    public String toString() {
        return "{PUTReply: key= " + kv.getKey() + ", value= " + kv.getValue() + ", successful= " + successful + "}";

    }

}
