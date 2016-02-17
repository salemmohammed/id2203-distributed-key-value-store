package system.coordination.event;

import se.sics.kompics.Event;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.config.ValueMerger;
import se.sics.kompics.network.Transport;
import system.client.event.ValueTimestampPair;
import system.network.TAddress;
import system.network.TMessage;

/**
 * Created by marcus on 17/02/16.
 */
public class ReadResponseMessage extends TMessage{

    private ValueTimestampPair pair;

    public ReadResponseMessage(TAddress source, TAddress destination, ValueTimestampPair pair) {
        super(source, destination, Transport.TCP);
        this.pair = pair;
    }

    public ValueTimestampPair getValueTimestampPair() {
        return this.pair;
    }
}
