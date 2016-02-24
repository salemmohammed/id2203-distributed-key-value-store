package system.coordination.paxos.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

import java.util.ArrayList;

/**
 * Created by marcus on 23/02/16.
 */
public class Propose extends TMessage {


    private TMessage c;

    public Propose(TAddress source, TAddress destination,TMessage c) {
        super(source, destination, Transport.TCP);
        this.c = c;
    }

    public TMessage getC() {
        return c;
    }

    public void setC(TMessage c) {
        this.c = c;
    }




}
