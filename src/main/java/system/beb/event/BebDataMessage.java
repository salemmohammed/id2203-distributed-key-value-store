package system.beb.event;

import system.network.TAddress;

/**
 * Created by Robin on 2016-02-14.
 */
public class BebDataMessage extends Pp2pDeliver {
    private static final long serialVersionUID = 9183185042302932366L;
    private BebDeliver data;

    protected BebDataMessage(TAddress source, BebDeliver data) {
        super(source);
        this.data = data;
    }

    public BebDeliver getData() {
        return data;
    }
}
