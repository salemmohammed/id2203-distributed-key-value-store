package system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.CancelPeriodicTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.Timeout;
import system.event.HeartbeatMessage;
import system.event.NodeMessage;

import java.lang.reflect.Array;
import java.util.*;

public class Node extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final TAddress self;
    private final TAddress otherGroupLeader;
    private boolean isLeader;


    private final HashMap<String, TAddress> neighbours;
    private final ArrayList<TAddress> suspectedNodes;
    private final ArrayList<TAddress> aliveNodes;
    Positive<Network> net = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    private UUID timerId;
    private int heartbeatDelay = 500;
    private int heartbeatDelayIncrease = 200;

    private LinkedList<TAddress> startupAcks;


    public Node(Init init) {
        suspectedNodes = new ArrayList<>();
        aliveNodes = new ArrayList<>();
        this.self = init.self;
        this.neighbours = init.neighbours;
        this.otherGroupLeader = init.otherGroupLeader;
        this.isLeader = init.isLeader;
        startupAcks = new LinkedList<>();
        subscribe(startHandler, control);
        subscribe(nodeMessageHandler, net);
        subscribe(heartbeatTimeoutHandler, timer);
    }

    Handler<Start> startHandler = new Handler<Start>() {

                @Override
                public void handle(Start event) {
                    //Init period timeout period
                    int period = 1000;
                    int delay = 500;
                    SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(delay, period);
                    HeartbeatTimeout timeout = new HeartbeatTimeout(spt);
                    spt.setTimeoutEvent(timeout);
                    trigger(spt, timer);
                    timerId = timeout.getTimeoutId();

                    //Add all neighbours as alive nodes
                    aliveNodes.addAll(neighbours.values());

                    //Send initial message to verify connectivity
                    Iterator it = neighbours.entrySet().iterator();
                    LOG.info( self.toString() + ": ( Start Event Triggered )") ;
                    while(it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        trigger(new NodeMessage(self,(TAddress) pair.getValue()), net);
                        LOG.info( self.toString() + ": ( Node message sent To: " + pair.getValue() + " )");
                    }
        }
    };

    Handler<HeartbeatTimeout> heartbeatTimeoutHandler = new Handler<HeartbeatTimeout>() {

        @Override
        public void handle(HeartbeatTimeout heartbeatTimeout) {

            ArrayList<TAddress> intersection = new ArrayList<>(aliveNodes);
            intersection.retainAll(suspectedNodes);
            //If we found an alive node that is also suspected, we know need to wait longer for heartbeat reply
            //This will prevent us from suspecting nodes that are alive simply because they did not reply to heartbeat
            if(!intersection.isEmpty()) {
                heartbeatDelay += heartbeatDelayIncrease;
            }
            //For each known neighbour
            neighbours.entrySet().forEach(node -> {
                TAddress nodeAddress = node.getValue();
                //If we found a node that has not replied to last heartbeat and is not suspected
                //We suspect it may have crashed and send a heartbeat to that node
                if(!aliveNodes.contains(nodeAddress) && !suspectedNodes.contains(nodeAddress)){
                    suspectedNodes.add(nodeAddress);
                    //Here we trigger program logic for detection of suspected node
                    System.out.println(self.toString() + ": SUSPECT: " + nodeAddress.toString());
                }
                //If we found a node that replied to heartbeat and is also suspected we consider it alive
                else if(aliveNodes.contains(nodeAddress) && suspectedNodes.contains(nodeAddress)){
                    suspectedNodes.remove(nodeAddress);
                    //Here we trigger program logic for restore of suspected node
                    System.out.println(self.toString() + ": RESTORE: " + nodeAddress.toString());
                }
                //Send a new heartbeat
                trigger(new HeartbeatMessage(self, nodeAddress), net);
            });
            aliveNodes.clear();
            //Subscribe to new timeout
        }
    };


    //TODO add heartbeat request handler and reply
    Handler<HeartbeatMessage> heartBeatHandler = new Handler<HeartbeatMessage>() {
        @Override
        public void handle(HeartbeatMessage heartbeatTimeout) {


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

    public static class HeartbeatTimeout extends Timeout {
        public HeartbeatTimeout(SchedulePeriodicTimeout spt) {
            super(spt);
        }
    }

    @Override
    public void tearDown() {
        trigger(new CancelPeriodicTimeout(timerId), timer);
    }

    public static class Init extends se.sics.kompics.Init<Node> {

        public final TAddress self;
        public final HashMap<String, TAddress> neighbours;
        public final TAddress otherGroupLeader;
        boolean isLeader;

        public Init(TAddress self, HashMap<String, TAddress> neighbours, TAddress otherGroupLeader, boolean isLeader) {
            this.self = self;
            this.neighbours = neighbours;
            this.otherGroupLeader = otherGroupLeader;
            this.isLeader = isLeader;
        }
    }
}
