package system.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import system.client.event.GETReply;
import system.client.event.GETRequest;
import system.event.NodeMessage;
import system.network.TAddress;
import system.node.Node;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Robin on 2016-02-13.
 */
public class Client extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);
    Positive<Network> net = requires(Network.class);
    private final ArrayList<TAddress> nodes;
    private final TAddress self;

    public Client(Init init) {
        self = init.self;
        nodes = init.nodes;

        subscribe(startHandler, control);
        subscribe(getReplyHandler, net);
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            Iterator it = nodes.iterator();
            while(it.hasNext()) {
                TAddress node = (TAddress) it.next();
                System.out.println("Sending GETRequest to " + node);
                trigger(new GETRequest(self, node), net);
            }
        }
    };

    Handler<GETReply> getReplyHandler = new Handler<GETReply>() {
        @Override
        public void handle(GETReply getReply) {
            System.out.println(self+": Received GETReply from " + getReply.getSource());
        }
    };

    public static class Init extends se.sics.kompics.Init<Client> {

        public final ArrayList<TAddress> nodes;
        public TAddress self;

        public Init(TAddress self, ArrayList<TAddress> nodes) {
            this.self = self;
            this.nodes = nodes;
        }
    }
}
