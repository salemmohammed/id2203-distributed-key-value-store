package system.coordination.paxos.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

import java.util.ArrayList;

/**
 * Created by marcus on 23/02/16.
 */
public class Accept extends TMessage {
    private int nc;
    private ArrayList<TMessage> vc;

    public Accept(TAddress source, TAddress destination, int nc, ArrayList<TMessage> vc) {

        super(source, destination, Transport.TCP);
        this.nc = nc;
        this.vc = vc;
    }

    public int getNc() {
        return nc;
    }

    public void setNc(int nc) {
        this.nc = nc;
    }

    public ArrayList<TMessage> getVc() {
        return vc;
    }

    public void setVc(ArrayList<TMessage> vc) {
        this.vc = vc;
    }
}
