package system.coordination.paxos;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

/**
 * Created by marcus on 23/02/16.
 */
public class Promise extends TMessage {

    private int n;
    private int na;
    private TMessage va;

    public Promise(TAddress source, TAddress destination, int n, int na, TMessage va) {
        super(source, destination, Transport.TCP);
        this.n = n;
        this.na = na;
        this.va = va;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getNa() {
        return na;
    }

    public void setNa(int na) {
        this.na = na;
    }

    public TMessage getVa() {
        return va;
    }

    public void setVa(TMessage va) {
        this.va = va;
    }
}
