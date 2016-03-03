package system.beb.event;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

import java.io.Serializable;

/**
 * Created by Robin on 2016-02-14.
 */
public class BebDataMessage extends TMessage implements Serializable {

    private TAddress source;
    private BebDeliver data;

    public BebDataMessage(TAddress source, TAddress destination, BebDeliver data) {
        super(source, destination, Transport.TCP);
        this.source = source;
        this.data = data;
    }

    public BebDeliver getData() {
        return data;
    }
}
