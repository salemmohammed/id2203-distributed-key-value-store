package system.coordination.paxos.event;

import java.util.List;

/**
 * Created by Robin on 2016-02-25.
 */
public class ReadItem {
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
