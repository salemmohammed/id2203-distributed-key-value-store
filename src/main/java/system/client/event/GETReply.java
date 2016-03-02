package system.client.event;

import system.KVEntry;
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

}
