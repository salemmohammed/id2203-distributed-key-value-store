package system.client.event;

import system.KVEntry;
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
}
