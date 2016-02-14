package system.beb.event;

import se.sics.kompics.KompicsEvent;
import system.network.TAddress;

import java.util.ArrayList;

/**
 * Created by Robin on 2016-02-14.
 */
public class BebBroadcast implements KompicsEvent {

    private final BebDeliver deliverEvent;
    private final ArrayList <TAddress> nodes;

    public BebBroadcast(BebDeliver deliverEvent, ArrayList <TAddress> nodes) {
        this.deliverEvent = deliverEvent;
        this.nodes = nodes;
    }

    public BebDeliver getDeliverEvent() {
        return deliverEvent;
    }

    public ArrayList<TAddress> getBroadcastNodes() {
        return nodes;
    }
}