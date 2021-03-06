package system.multipaxos.event;

import java.io.Serializable;
import java.util.List;

public class ReadItem implements Serializable{
    private int ts;
    private List<Object> vsuf;

    public ReadItem(int ts, List<Object> vsuf)
    {
        this.setTs(ts);
        this.setVsuf(vsuf);
    }

    public int getTs() {
        return ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public List<Object> getVsuf() {
        return vsuf;
    }

    public void setVsuf(List<Object> vsuf) {
        this.vsuf = vsuf;
    }
}
