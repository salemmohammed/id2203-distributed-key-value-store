package system.beb;

import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import system.beb.event.BebBroadcastRequest;
import system.beb.event.BebDataMessage;
import system.beb.event.BebDeliver;
import system.network.TAddress;

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
    private Handler<BebBroadcastRequest> broadcastHandler = new Handler<BebBroadcastRequest>() {
        @Override
        public void handle(BebBroadcastRequest event) {
            ArrayList <TAddress> nodes = event.getBroadcastNodes();
            for (TAddress node : nodes) {
                BebDataMessage msg = new BebDataMessage(self,node, event.getDeliverEvent());
                trigger(msg, net);
            }
            BebDataMessage msg = new BebDataMessage(self,self, event.getDeliverEvent());
            trigger(msg, net);
        }
    };

    //Deliver to application
    private Handler<BebDataMessage> deliverHandler = new Handler<BebDataMessage>() {
        @Override
        public void handle(BebDataMessage event) {
            //logger.info("Node {} received delivery event", self);
            trigger(event.getData(), beb);
        }
    };

    public static class Init extends se.sics.kompics.Init<BestEffortBroadcast> {
        public TAddress self;

        public Init(TAddress self) {
            this.self = self;
        }
    }
}
