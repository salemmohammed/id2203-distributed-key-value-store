package system.coordination.paxos.event;

import se.sics.kompics.KompicsEvent;

/**
 * Created by Robin on 2016-02-24.
 */
public class AscDecide implements KompicsEvent {
    private Object value;

    public AscDecide(Object value) {
        this.setValue(value);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
