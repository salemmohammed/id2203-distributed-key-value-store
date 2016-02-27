package system.coordination.paxos.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

import java.util.ArrayList;

public class Propose extends TMessage {

    private TMessage tMessage;

    public Propose(TAddress source, TAddress destination,TMessage tMessage) {
        super(source, destination, Transport.TCP);
        this.tMessage = tMessage;
    }

    public TMessage getC() {
        return tMessage;
    }

    public void setC(TMessage c) {
        this.tMessage = tMessage;
    }




}
