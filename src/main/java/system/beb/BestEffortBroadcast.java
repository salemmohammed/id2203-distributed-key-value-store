package system.beb;

import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import system.beb.event.BebBroadcast;
import system.beb.event.BebDataMessage;
import system.beb.event.BebDeliver;
import system.network.TAddress;
import system.port.pp2p.PerfectPointToPointLinkPort;
import system.port.pp2p.Pp2pSend;

import java.util.ArrayList;

/**
 * Created by Robin on 2016-02-14.
 */
public class BestEffortBroadcast extends ComponentDefinition {

    private Positive<Network> net = requires(Network.class);
    private Negative<BestEffortBroadcastPort> beb = provides(BestEffortBroadcastPort.class);

    private TAddress self;

    public BestEffortBroadcast(Init init) {
        this.self = init.self;
        subscribe(startHandler, control);
        subscribe(broadcastHandler, beb);
        subscribe(deliverHandler, net);
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
                trigger(new Pp2pSend(node, msg), net);
            }
            BebDataMessage msg = new BebDataMessage(self, event.getDeliverEvent());
            trigger(new Pp2pSend(self, msg).getDeliverEvent(), net);
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

    public static class Init extends se.sics.kompics.Init<BestEffortBroadcast> {
        public TAddress self;

        public Init(TAddress self) {
            this.self = self;
        }
    }
}
