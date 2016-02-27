package system.coordination.paxos.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

public class Decide extends TMessage {

        private int pts;
        private int pl;
        private int t;

    public Decide(TAddress source, TAddress destination, int pts, int pl, int t) {
            super(source, destination, Transport.TCP);
            this.setPts(pts);
            this.setPl(pl);
            this.setT(t);
        }

    public int getPts() {
        return pts;
    }

    public void setPts(int pts) {
        this.pts = pts;
    }

    public int getPl() {
        return pl;
    }

    public void setPl(int pl) {
        this.pl = pl;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }
}

