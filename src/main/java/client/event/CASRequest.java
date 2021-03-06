package client.event;

import system.data.KVEntry;
import system.network.TAddress;

public class CASRequest extends CommandMessage {

    private KVEntry kv;


    private int newValue;


    public CASRequest(TAddress src, TAddress dst, KVEntry kv, int newValue, int pid, int seqNum) {
        super(src, dst, pid, seqNum);
        this.newValue = newValue;
        this.kv = kv;
    }

    public KVEntry getKVEntry() {

        return kv;
    }

    public int getNewValue() {
        return newValue;
    }


    public String toString() {
        String commandString = "pid= " + this.getPid() + ", seqNum= " + this.getSeqNum();
        return "{CASRequest: " + commandString +  ", key= " + kv.getKey() + ", value= " + kv.getValue() + ", newValue= " + newValue + "}";

    }
}
