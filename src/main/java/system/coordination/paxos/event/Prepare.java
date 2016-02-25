package system.coordination.paxos.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

/**
 * Created by marcus on 23/02/16.
 */
public class Prepare extends TMessage {

    private int pts;
    private int al;
    private int t;

    public Prepare(TAddress source, TAddress destination, int pts, int al, int t) {
        super(source, destination, Transport.TCP);
        this.setPts(pts);
        this.setAl(al);
        this.setT(t);
    }

    public int getPts() {
        return pts;
    }

    public void setPts(int pts) {
        this.pts = pts;
    }

    public int getAl() {
        return al;
    }

    public void setAl(int al) {
        this.al = al;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }
}
