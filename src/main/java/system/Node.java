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
import system.event.NodeMessage;

import java.util.*;

public class Node extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final TAddress self;
    private final TAddress otherGroupLeader;
    private boolean isLeader;


    private final HashMap<String, TAddress> neighbours;
    Positive<Network> net = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    private UUID timerId;

    private LinkedList<TAddress> startupAcks;


    public Node(Init init) {
        this.self = init.self;
        this.neighbours = init.neighbours;
        this.otherGroupLeader = init.otherGroupLeader;
        this.isLeader = init.isLeader;
        startupAcks = new LinkedList<TAddress>();
        subscribe(startHandler, control);
        subscribe(nodeMessageHandler, net);
    }

    Handler<Start> startHandler = new Handler<Start>() {

                @Override
                public void handle(Start event) {

                    SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(0, 1000);
                    HeartbeatTimeout timeout = new HeartbeatTimeout(spt);
                    spt.setTimeoutEvent(timeout);
                    trigger(spt, timer);
                    timerId = timeout.getTimeoutId();

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
