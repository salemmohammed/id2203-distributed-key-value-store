import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Node extends ComponentDefinition {

    private final TAddress self;

    private final HashMap<String, TAddress> neighbours;
    Positive<Network> net = requires(Network.class);


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
            while(it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println("OKEY let's send us a message! Consider this other node!");
                trigger(new NodeMessage(self,(TAddress) pair.getValue()), net);
            }


        }
    };

    Handler<NodeMessage> nodeMessageHandler = new Handler<NodeMessage>() {
        @Override
        public void handle(NodeMessage event) {
            System.out.println("Node Message - Source: " + event.getHeader().getSource().getPort() + "Destination: " + self.getPort());

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
