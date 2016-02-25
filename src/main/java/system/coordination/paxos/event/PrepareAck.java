package system.coordination.paxos.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

import java.util.List;

/**
 * Created by marcus on 23/02/16.
 */
public class PrepareAck extends TMessage {

    private int ts;
    private int ats;
    private List<Object> vsuf;
    private int al;
    private int t;

    public PrepareAck(TAddress source, TAddress destination, int ts, int ats, List<Object> vsuf, int al, int t) {
        super(source, destination, Transport.TCP);
        this.setTs(ts);
        this.setAts(ats);
        this.setVsuf(vsuf);
        this.setAl(al);
        this.setT(t);
    }

    public int getTs() {
        return ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public int getAts() {
        return ats;
    }

    public void setAts(int ats) {
        this.ats = ats;
    }

    public List<Object> getVsuf() {
        return vsuf;
    }

    public void setVsuf(List<Object> vsuf) {
        this.vsuf = vsuf;
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
