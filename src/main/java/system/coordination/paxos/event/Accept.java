package system.coordination.paxos.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

import java.io.Serializable;
import java.util.ArrayList;

public class Accept extends TMessage implements Serializable {
    private int ts;
    private ArrayList<Object> vsuf;
    private int offs;
    private int t;

    public Accept(TAddress source, TAddress destination, int ts, ArrayList<Object> vsuf, int offs, int t) {
        super(source, destination, Transport.TCP);
        this.ts = ts;
        this.vsuf = vsuf;
        this.offs = offs;
        this.t = t;

    }

    public int getTs() {
        return ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public ArrayList<Object> getVsuf() {
        return vsuf;
    }

    public void setVsuf(ArrayList<Object> vsuf) {
        this.vsuf = vsuf;
    }

    public int getOffs() {
        return offs;
    }

    public void setOffs(int offs) {
        this.offs = offs;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }
}
