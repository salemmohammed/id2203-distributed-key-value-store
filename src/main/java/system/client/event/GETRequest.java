package system.client.event;

import system.KVEntry;
import system.network.TAddress;

public class GETRequest extends Command{

    private KVEntry kv;

    public GETRequest(TAddress src, TAddress dst, KVEntry kv) {
        super(src, dst);
        this.kv = kv;
    }

    public KVEntry getKv() {
        return kv;
    }
}
