package client.event;

import system.data.KVEntry;
import system.network.TAddress;

public class PUTReply extends CommandMessage {

    private KVEntry kv;
    public boolean successful;

    public PUTReply(TAddress src, TAddress dst, KVEntry kv, int pid, int seqNum) {
        super(src, dst, pid, seqNum);
        this.kv = kv;
    }

    public KVEntry getKVEntry() {
        return kv;
    }


    public String toString() {
        String commandString = "pid= " + this.getPid() + ", seqNum= " + this.getSeqNum();
        return "{PUTReply: " + commandString + ", key= " + kv.getKey() + ", value= " + kv.getValue() + ", successful= " + successful + "}";

    }

}
