package system.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import system.client.event.CASRequest;
import system.client.event.Command;
import system.client.event.GETRequest;
import system.KVEntry;
import system.client.event.PUTRequest;
import system.coordination.paxos.event.AscDecide;
import system.coordination.paxos.event.AscPropose;
import system.coordination.paxos.port.AbortableSequenceConsensusPort;
import system.coordination.rsm.ReplicatedStateMachine;
import system.coordination.rsm.event.ExecuteCommand;
import system.coordination.rsm.event.ExecuteReponse;
import system.coordination.rsm.port.RSMPort;
import system.data.Bound;
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
    private Bound bounds;

    private final ArrayList<TAddress> neighbours;
    Positive<Network> net = requires(Network.class);
    Positive<FDPort> epfd = requires(FDPort.class);
    Positive<AbortableSequenceConsensusPort> asc = requires(AbortableSequenceConsensusPort.class);
    Positive<RSMPort> rsm = requires(RSMPort.class);

    private int seqNum = 0;




    public Node(Init init) {
        this.self = init.self;
        this.neighbours = init.neighbours;
        this.replicationGroup = init.replicationGroup;
        this.isLeader = init.isLeader;
        this.bounds = init.bounds;

        subscribe(startHandler, control);

        subscribe(suspectHandler, epfd);
        subscribe(restoreHandler, epfd);

        subscribe(getRequestHandler, net);
        subscribe(putRequestHandler, net);
        subscribe(casRequestHandler, net);

        subscribe(ascDecideHandler, asc);

        subscribe(executeReponseHandler, rsm);
    }

    Handler<Start> startHandler = new Handler<Start>() {
                @Override
                public void handle(Start event) {
                    //Send initial message to verify connectivity
                    Iterator it = neighbours.iterator();
                    LOG.info(self.toString() + ": Start Event Triggered (Replication= " + replicationGroup+")");
        }
    };

    Handler<GETRequest> getRequestHandler = new Handler<GETRequest>() {
        @Override
        public void handle(GETRequest getRequest) {
            trigger(new AscPropose(getRequest), asc);
        }
    };

    Handler<PUTRequest> putRequestHandler = new Handler<PUTRequest>() {
        @Override
        public void handle(PUTRequest putRequest) {
            trigger(new AscPropose(putRequest), asc);
        }
    };

    Handler<CASRequest> casRequestHandler = new Handler<CASRequest>() {
        @Override
        public void handle(CASRequest casRequest) {
            trigger(new AscPropose(casRequest), asc);
        }
    };


    Handler<AscDecide> ascDecideHandler = new Handler<AscDecide>() {
        @Override
        public void handle(AscDecide ascDecide) {
            ExecuteCommand executeCommand = new ExecuteCommand((Command) ascDecide.getValue());
            trigger(executeCommand, rsm);
        }
    };

    Handler<ExecuteReponse> executeReponseHandler = new Handler<ExecuteReponse>() {
        @Override
        public void handle(ExecuteReponse executeReponse) {
            trigger(executeReponse.getCommand(), net);
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
        public HashMap<Integer, KVEntry> store;
        public ArrayList<TAddress> replicationGroup;
        public Bound bounds;

        public Init(TAddress self, ArrayList<TAddress> neighbours, HashMap<Integer, KVEntry> store, ArrayList<TAddress> replicationGroup, boolean isLeader, Bound bounds) {
            this.self = self;
            this.neighbours = neighbours;
            this.store = store;
            this.replicationGroup = replicationGroup;
            this.isLeader = isLeader;
            this.bounds = bounds;
        }
    }
}
