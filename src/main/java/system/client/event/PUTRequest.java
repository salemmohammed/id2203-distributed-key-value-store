package system.client.event;

import system.data.KVEntry;
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


    public String toString() {
        return "{PUTRequest: key= " + kv.getKey() + ", value= " + kv.getValue() + "}";

    }

}
