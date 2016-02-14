package system.beb.event;

import system.network.TAddress;
import system.port.pp2p.Pp2pDeliver;

/**
 * Created by Robin on 2016-02-14.
 */
public class BebDataMessage extends Pp2pDeliver {
    private static final long serialVersionUID = 9183185042302932366L;
    private BebDeliver data;

    public BebDataMessage(TAddress source, BebDeliver data) {
        super(source);
        this.data = data;
    }

    public BebDeliver getData() {
        return data;
    }
}
