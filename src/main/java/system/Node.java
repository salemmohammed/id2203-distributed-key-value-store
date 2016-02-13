package system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.*;
import se.sics.kompics.timer.Timer;
import system.event.HeartbeatReply;
import system.event.HeartbeatRequest;
import system.event.NodeMessage;


import java.util.*;

public class Node extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final TAddress self;
    private final TAddress otherGroupLeader;
    private boolean isLeader;


    private final ArrayList<TAddress> neighbours;
    private final ArrayList<TAddress> suspectedNodes;
    private final ArrayList<TAddress> aliveNodes;
    Positive<Network> net = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    private UUID timerId;
    private int heartbeatDelay = 1;
    private int heartbeatDelayIncrease = 0;

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

        subscribe(heartbeatRequestHandler, net);
        subscribe(heartbeatReplyHandler, net);
        subscribe(heartbeatTimeoutHandler, timer);
    }

    Handler<Start> startHandler = new Handler<Start>() {

                @Override
                public void handle(Start event) {

                    //Add all neighbours as alive nodes
                    aliveNodes.addAll(neighbours);

                    //Send initial message to verify connectivity
                    Iterator it = neighbours.iterator();
                    LOG.info( self.toString() + ": ( Start Event Triggered )") ;
                    while(it.hasNext()) {
                        TAddress neighbour = (TAddress)it.next();
                        trigger(new NodeMessage(self,neighbour), net);
                        LOG.info( self.toString() + ": ( Node message sent To: " + neighbour + " )");
                    }
                    startTimer(heartbeatDelay);
        }
    };


    Handler<HeartbeatTimeout> heartbeatTimeoutHandler = new Handler<HeartbeatTimeout>() {

        @Override
        public void handle(HeartbeatTimeout heartbeatTimeout) {
            ArrayList<TAddress> intersection = new ArrayList<>(aliveNodes);
            intersection.retainAll(suspectedNodes);
            //If we found an alive node that is also suspected, we know need to wait longer for heartbeat reply
            //This will prevent us from suspecting nodes that are alive simply because they did not reply to heartbeat
            if (!intersection.isEmpty()) {
                heartbeatDelay += heartbeatDelayIncrease;
            }

            Iterator it = neighbours.iterator();
            while(it.hasNext()) {
                TAddress neighbour = (TAddress) it.next();
                //If we found a node that has not replied to last heartbeat and is not suspected
                //We suspect it may have crashed and add it to the suspected list
                if (!aliveNodes.contains(neighbour) && !suspectedNodes.contains(neighbour)) {
                    suspectedNodes.add(neighbour);
                    //Here we trigger program logic for detection of suspected node
                    System.out.println(self.toString() + ": SUSPECT: " + neighbour.toString());
                }
                //If we found a node that replied to heartbeat and is also suspected we consider it alive
                else if (aliveNodes.contains(neighbour) && suspectedNodes.contains(neighbour)) {
                    suspectedNodes.remove(neighbour);
                    //Here we trigger program logic for restore of suspected node
                    System.out.println(self.toString() + ": RESTORE: " + neighbour.toString());
                }
                //Send a new heartbeat
                trigger(new HeartbeatRequest(self, neighbour), net);
            }
                aliveNodes.clear();
                //Subscribe to new timeout
                startTimer(heartbeatDelay);
            }
    };

    private void startTimer(int delay) {
        ScheduleTimeout st = new ScheduleTimeout(delay);
        HeartbeatTimeout timeout = new HeartbeatTimeout(st);
        st.setTimeoutEvent(timeout);
        trigger(st, timer);
        timerId = timeout.getTimeoutId();
    }


    //TODO add heartbeat request handler and reply
    Handler<HeartbeatReply> heartbeatReplyHandler = new Handler<HeartbeatReply>() {
        @Override
        public void handle(HeartbeatReply heartbeatTimeout) {
            aliveNodes.add(heartbeatTimeout.getSource());
        }
    };

    //TODO add heartbeat request handler and reply
    Handler<HeartbeatRequest> heartbeatRequestHandler = new Handler<HeartbeatRequest>() {
        @Override
        public void handle(HeartbeatRequest heartbeatTimeout) {
            trigger(new HeartbeatReply(self, heartbeatTimeout.getSource()), net);
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
        public HeartbeatTimeout(ScheduleTimeout spt) {
            super(spt);
        }
    }

    @Override
    public void tearDown() {
        trigger(new CancelPeriodicTimeout(timerId), timer);
    }

    public static class Init extends se.sics.kompics.Init<Node> {

        public final TAddress self;
        public final ArrayList<TAddress> neighbours;
        public final TAddress otherGroupLeader;
        boolean isLeader;

        public Init(TAddress self, ArrayList<TAddress> neighbours, TAddress otherGroupLeader, boolean isLeader) {
            this.self = self;
            this.neighbours = neighbours;
            this.otherGroupLeader = otherGroupLeader;
            this.isLeader = isLeader;
        }
    }
}
