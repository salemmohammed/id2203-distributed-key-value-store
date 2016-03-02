package system.client.event;

import system.KVEntry;
import system.network.TAddress;

public class PUTRequest extends CommandMessage {

    private KVEntry kv;

    public PUTRequest(TAddress src, TAddress dst, KVEntry kv, int pid, int seqNum) {
        super(src, dst, pid, seqNum);
        this.kv = kv;
    }

    public KVEntry getKv() {
        return kv;
    }

    public boolean equals(Object obj) {
        PUTRequest getRequest = (PUTRequest) obj;
        KVEntry kvEntry = getRequest.getKv();
        if(kvEntry.getKey() == kv.getKey() && kvEntry.getValue() == kv.getValue()) {
            return true;
        }
        return false;
    }
}
