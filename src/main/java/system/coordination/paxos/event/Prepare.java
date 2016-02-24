package system.coordination.paxos.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

/**
 * Created by marcus on 23/02/16.
 */
public class Prepare extends TMessage {


    private int n;


    public Prepare(TAddress source, TAddress destination, int n) {
        super(source, destination, Transport.TCP);
        this.n = n;
    }

    public int getN() { return n;
    }

    public void setN(int n) {
        this.n = n;
    }
}
