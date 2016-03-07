package system.beb.event;

import se.sics.kompics.KompicsEvent;
import system.network.TAddress;

import java.util.ArrayList;

public class BebBroadcastRequest implements KompicsEvent {

    private final BebDeliver deliverEvent;
    private final ArrayList <TAddress> nodes;

    public BebBroadcastRequest(BebDeliver deliverEvent, ArrayList <TAddress> nodes) {
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