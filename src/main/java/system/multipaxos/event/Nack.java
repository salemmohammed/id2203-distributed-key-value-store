package system.multipaxos.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

import java.io.Serializable;

public class Nack extends TMessage implements Serializable{

    private int ts;
    private int t;

    public Nack(TAddress source, TAddress destination, int ts, int t) {
        super(source, destination, Transport.TCP);
        this.setTs(ts);
        this.setT(t);
    }

    public int getTs() {
        return ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }
}
