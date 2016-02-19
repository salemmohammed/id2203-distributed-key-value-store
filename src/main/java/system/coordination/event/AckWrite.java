package system.coordination.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

/**
 * Created by marcus on 18/02/16.
 */


public class AckWrite extends TMessage{
    public AckWrite(TAddress src, TAddress dst,) {
        super(src, dst, Transport.TCP);
    }
}
