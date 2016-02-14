package system.port.pp2p;

import se.sics.kompics.KompicsEvent;
import system.network.TAddress;

import java.io.Serializable;

/**
 * Created by marcus on 14/02/16.
 */
public abstract class Pp2pDeliver implements KompicsEvent, Serializable {

    private TAddress source;

    protected Pp2pDeliver(TAddress source) {
        this.source = source;
    }

    public final TAddress getSource() {
        return source;
    }

}
