package system.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import system.client.event.GETReply;
import system.client.event.GETRequest;
import system.client.event.ValueTimestampPair;
import system.coordination.port.RIWMPort;
import system.port.epfd.FDPort;
import system.epfd.event.Restore;
import system.epfd.event.Suspect;
import system.network.TAddress;
import java.util.*;

public class Node extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final TAddress self;
    private boolean isLeader;
    private ArrayList<TAddress> replicationGroup;

    private HashMap<Integer, ValueTimestampPair> store;
    private final ArrayList<TAddress> neighbours;
    Positive<Network> net = requires(Network.class);
    Positive<FDPort> epfd = requires(FDPort.class);
    Positive<RIWMPort> riwm = requires(RIWMPort.class);
    private LinkedList<TAddress> startupAcks;


    public Node(Init init) {
        this.self = init.self;
        this.neighbours = init.neighbours;
        this.store = init.store;
        this.replicationGroup = init.replicationGroup;
        this.isLeader = init.isLeader;
        startupAcks = new LinkedList<>();

        subscribe(startHandler, control);

        subscribe(suspectHandler, epfd);
        subscribe(restoreHandler, epfd);

        subscribe(getRequestHandler, net);
    }

    Handler<Start> startHandler = new Handler<Start>() {
                @Override
                public void handle(Start event) {
                    //Send initial message to verify connectivity
                    Iterator it = neighbours.iterator();
                    LOG.info(self.toString() + ": Start Event Triggered (Store= " + store+")");
                    LOG.info(self.toString() + ": Start Event Triggered (Replication= " + replicationGroup+")");
        }
    };

    Handler<GETRequest> getRequestHandler = new Handler<GETRequest>() {
        @Override
        public void handle(GETRequest getRequest) {
            int key = getRequest.getKey();
            System.out.println("Received GETRequest");
            trigger(new GETReply(self, getRequest.getSource(), keyValue), net);
        }
    };

    Handler<Suspect> suspectHandler = new Handler<Suspect>() {
        @Override
        public void handle(Suspect suspect) {
            System.out.println("Received suspect");
        }
    };

    Handler<Restore> restoreHandler = new Handler<Restore>() {
        @Override
        public void handle(Restore suspect) {
            System.out.println("Received restore");
        }
    };

    public static class Init extends se.sics.kompics.Init<Node> {

        public final TAddress self;
        public final ArrayList<TAddress> neighbours;
        public boolean isLeader;
        public HashMap<Integer, ValueTimestampPair> store;
        public ArrayList<TAddress> replicationGroup;

        public Init(TAddress self, ArrayList<TAddress> neighbours, HashMap<Integer, ValueTimestampPair> store, ArrayList<TAddress> replicationGroup, boolean isLeader) {
            this.self = self;
            this.neighbours = neighbours;
            this.store = store;
            this.replicationGroup = replicationGroup;
            this.isLeader = isLeader;
        }
    }
}
