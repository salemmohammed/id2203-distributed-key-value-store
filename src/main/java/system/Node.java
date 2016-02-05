package system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Node extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final TAddress self;

    private final HashMap<String, TAddress> neighbours;
    Positive<Network> net = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);


    public Node(Init init) {
        this.self = init.self;
        this.neighbours = init.neighbours;
        subscribe(startHandler, control);
        subscribe(nodeMessageHandler, net);
    }

    Handler<Start> startHandler = new Handler<Start>() {

        @Override
        public void handle(Start event) {

            Iterator it = neighbours.entrySet().iterator();
            LOG.info( self.toString() + ": ( Start Event Triggered )") ;
            while(it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                trigger(new NodeMessage(self,(TAddress) pair.getValue()), net);
                LOG.info( self.toString() + ": ( Node message sent To: " + pair.getValue() + " )");
            }


        }
    };

    Handler<NodeMessage> nodeMessageHandler = new Handler<NodeMessage>() {

        @Override
        public void handle(NodeMessage event) {
            LOG.info( self.toString() + ": ( NodeMessage Received From: " + event.getSource() + " )");

        }
    };




    public static class Init extends se.sics.kompics.Init<Node> {

        public final TAddress self;
        public final HashMap<String, TAddress> neighbours;

        public Init(TAddress self, HashMap<String, TAddress> neighbours) {
            this.self = self;
            this.neighbours = neighbours;
        }
    }
}
