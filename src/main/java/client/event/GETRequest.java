package client.event;

import system.data.KVEntry;
import system.network.TAddress;

public class GETRequest extends CommandMessage {

    private KVEntry kv;

    public GETRequest(TAddress src, TAddress dst, KVEntry kv, int pid, int seqNum) {
        super(src, dst, pid, seqNum);
        this.kv = kv;
    }

    public KVEntry getKVEntry() {
        return kv;
    }


    public String toString() {
        String commandString = "pid= " + this.getPid() + ", seqNum= " + this.getSeqNum();
        return "{GETRequest: " + commandString + ", key= " + kv.getKey() + "}";

    }

}
