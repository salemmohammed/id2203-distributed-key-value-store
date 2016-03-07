package system.riwm.event;

import se.sics.kompics.network.Transport;
import system.data.KVEntry;
import system.network.TAddress;
import system.network.TMessage;

import java.io.Serializable;

public class ReadResponseMessage extends TMessage implements Serializable{

    private KVEntry kv;
    private int rId;

    public ReadResponseMessage(TAddress source, TAddress destination, KVEntry kv, int rId) {
        super(source, destination, Transport.TCP);
        this.kv = kv;
        this.rId = rId;
    }

    public KVEntry getKv() {
        return this.kv;
    }

    public int getrId() {
        return rId;
    }
}
