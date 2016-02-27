package system.coordination.paxos.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

public class AcceptAck extends TMessage {
    private int pts;
    private int l;
    private int t;

    public AcceptAck(TAddress source, TAddress destination, int pts, int l, int t) {
        super(source, destination, Transport.TCP);
        this.setPts(pts);
        this.setL(l);
        this.setT(t);
    }

    public int getPts() {
        return pts;
    }

    public void setPts(int pts) {
        this.pts = pts;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }
}
