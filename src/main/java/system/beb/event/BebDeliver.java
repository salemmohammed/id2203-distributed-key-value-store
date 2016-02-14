package system.beb.event;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.test.Message;
import system.network.TAddress;

import java.io.Serializable;

/**
 * Created by Robin on 2016-02-14.
 */
public class BebDeliver implements KompicsEvent {

    private static final long serialVersionUID = 4088333329204792579L;

    private TAddress source;
    private Message message;


    public BebDeliver(TAddress source) {
        this.source = source;
    }

    public TAddress getSource() {
        return source;
    }

    public Message getMessage() {
        return message;
    }
}