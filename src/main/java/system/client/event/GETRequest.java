package system.client.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.THeader;
import system.network.TMessage;

/**
 * Created by Robin on 2016-02-13.
 */
public class GETRequest extends TMessage {

    private int key;

    public GETRequest(TAddress src, TAddress dst, int key) {
        super(src, dst, Transport.TCP);
        this.key = key;
    }

    public GETRequest(THeader header) {
        super(header);
    }

    public int getKey() {
        return key;
    }
}
