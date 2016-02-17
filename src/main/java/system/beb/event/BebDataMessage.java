package system.beb.event;

import se.sics.kompics.KompicsEvent;
import system.network.TAddress;

/**
 * Created by Robin on 2016-02-14.
 */
public class BebDataMessage implements KompicsEvent {
    private static final long serialVersionUID = 9183185042302932366L;
    private BebDeliver data;

    public BebDataMessage(TAddress source, BebDeliver data) {


        this.data = data;
    }

    public BebDeliver getData() {
        return data;
    }
}
