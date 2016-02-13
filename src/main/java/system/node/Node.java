package system.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import system.client.event.GETReply;
import system.client.event.GETRequest;
import system.epfd.port.FDPort;
import system.epfd.event.Restore;
import system.epfd.event.Suspect;
import system.event.*;
import system.network.TAddress;


import java.util.*;

public class Node extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final TAddress self;
    private final TAddress otherGroupLeader;
    private boolean isLeader;

    private HashMap<Integer, Integer> store;
    private final ArrayList<TAddress> neighbours;
    Positive<Network> net = requires(Network.class);
    Positive<FDPort> epfd = requires(FDPort.class);
    private LinkedList<TAddress> startupAcks;


    public Node(Init init) {
        this.self = init.self;
        this.neighbours = init.neighbours;
        this.otherGroupLeader = init.otherGroupLeader;
        this.store = init.store;
        this.isLeader = init.isLeader;
        startupAcks = new LinkedList<>();

        subscribe(startHandler, control);
        subscribe(nodeMessageHandler, net);

        subscribe(suspectHandler, epfd);
        subscribe(restoreHandler, epfd);

        subscribe(getRequestHandler, net);
    }

    Handler<Start> startHandler = new Handler<Start>() {
                @Override
                public void handle(Start event) {
                    //Send initial message to verify connectivity
                    Iterator it = neighbours.iterator();
                    LOG.info(self.toString() + ": Start Event Triggered (Store= " + store+")") ;
                    while(it.hasNext()) {
                        TAddress neighbour = (TAddress)it.next();
                        trigger(new NodeMessage(self,neighbour), net);
                        LOG.info( self.toString() + ": ( Node message sent To: " + neighbour + " )");
                    }
        }
    };

    Handler<GETRequest> getRequestHandler = new Handler<GETRequest>() {
        @Override
        public void handle(GETRequest getRequest) {
            System.out.println("Received GETRequest");
            trigger(new GETReply(self, getRequest.getSource()), net);
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


    Handler<NodeMessage> nodeMessageHandler = new Handler<NodeMessage>() {
        @Override
        public void handle(NodeMessage event) {
            //Leader waits for message from everyone in the group, then sends message to other leader
            if(isLeader) {
                if(event.getSource().equals(otherGroupLeader)) {
                    LOG.info("Leader: " + self.toString() + event.getSource() + " Received From: " + event.getSource() + " )");
                }
                    startupAcks.add(event.getSource());
            }
            if(startupAcks.size() == neighbours.size()) {
                trigger(new NodeMessage(self,otherGroupLeader), net);
            }
            LOG.info( self.toString() + ": ( NodeMessage Received From: " + event.getSource() + " )");
        }
    };

    public static class Init extends se.sics.kompics.Init<Node> {

        public final TAddress self;
        public final ArrayList<TAddress> neighbours;
        public final TAddress otherGroupLeader;
        public boolean isLeader;
        public HashMap<Integer, Integer> store;

        public Init(TAddress self, ArrayList<TAddress> neighbours, TAddress otherGroupLeader, HashMap<Integer, Integer> store, boolean isLeader) {
            this.self = self;
            this.neighbours = neighbours;
            this.otherGroupLeader = otherGroupLeader;
            this.store = store;
            this.isLeader = isLeader;
        }
    }
}
