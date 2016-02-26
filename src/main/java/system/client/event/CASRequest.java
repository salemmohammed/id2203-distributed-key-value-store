package system.client.event;

import system.KVEntry;
import system.network.TAddress;

public class CASRequest extends Command{

    private KVEntry kv;


    private int newValue;


    public CASRequest(TAddress src, TAddress dst, KVEntry kv, int newValue) {
        super(src, dst);
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
