package system.coordination.paxos.event;

import se.sics.kompics.KompicsEvent;

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
