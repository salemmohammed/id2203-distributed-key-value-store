package system.beb.event;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.test.Message;
import se.sics.kompics.simulator.events.TakeSnapshot;
import system.network.TAddress;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Robin on 2016-02-14.
 */
public class BebDeliver implements KompicsEvent {

    private static final long serialVersionUID = 4088333329204792579L;

    private TAddress source;
    private Object data;

    public BebDeliver(TAddress source, Object data) {
        this.source = source;
        this.data = data;
    }

    public TAddress getSource() {
        return source;
    }

    public Object getData() {
        return data;
    }
}