package system.beb;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Start;
import system.beb.event.BebBroadcast;
import system.beb.event.BebDataMessage;
import system.beb.event.BebDeliver;
import system.network.TAddress;

import java.util.ArrayList;

/**
 * Created by Robin on 2016-02-14.
 */
public class BestEffortBroadcast extends ComponentDefinition {

    private Positive<PerfectPointToPointLink> pp2p = requires(PerfectPointToPointLink.class);
    private Negative<BestEffortBroadcastPort> beb = provides(BestEffortBroadcastPort.class);

    public BestEffortBroadcast() {
        subscribe(startHandler, control);
        subscribe(broadcastHandler, beb);
        subscribe(deliverHandler, pp2p);
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {

        }
    };

    //Perform broadcast using Perfect Links
    private Handler<BebBroadcast> broadcastHandler = new Handler<BebBroadcast>() {
        @Override
        public void handle(BebBroadcast event) {
            ArrayList <TAddress> nodes = event.getBroadcastNodes();
            for (TAddress node : nodes) {
                BebDataMessage msg = new BebDataMessage(node, event.getDeliverEvent());
                //logger.info("bcast to {}!", node.getId());
                trigger(new Pp2pSend(node, msg), pp2p);
            }
        }
    };

    //Deliver to application
    private Handler<BebDeliver> deliverHandler = new Handler<BebDeliver>() {
        @Override
        public void handle(BebDeliver event) {
            //logger.info("Node {} received delivery event", self);
            trigger(event.getMessage(), beb);
        }
    };

}
