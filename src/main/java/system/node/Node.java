package system.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import system.client.event.GETRequest;
import system.KVEntry;
import system.client.event.PUTRequest;
import system.coordination.paxos.event.AscDecide;
import system.coordination.paxos.event.AscPropose;
import system.coordination.paxos.port.AbortableSequenceConsensusPort;
import system.coordination.riwm.event.InitReadRequest;
import system.coordination.riwm.event.InitWriteRequest;
import system.coordination.riwm.event.ReadReturn;
import system.coordination.riwm.event.WriteReturn;
import system.coordination.riwm.port.RIWMPort;
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

    private HashMap<Integer, KVEntry> store;
    private final ArrayList<TAddress> neighbours;
    Positive<Network> net = requires(Network.class);
    Positive<FDPort> epfd = requires(FDPort.class);
    Positive<RIWMPort> riwm = requires(RIWMPort.class);
    Positive<AbortableSequenceConsensusPort> asc = requires(AbortableSequenceConsensusPort.class);



    public Node(Init init) {
        this.self = init.self;
        this.neighbours = init.neighbours;
        this.store = init.store;
        System.out.println(store.toString());
        this.replicationGroup = init.replicationGroup;
        this.isLeader = init.isLeader;
        this.bounds = init.bounds;

        subscribe(startHandler, control);

        subscribe(suspectHandler, epfd);
        subscribe(restoreHandler, epfd);

        subscribe(readReturnHandler, riwm);
        subscribe(writeReturnHandler, riwm);

        subscribe(getRequestHandler, net);
        subscribe(putRequestHandler, net);

        subscribe(ascProposeHandler, net);
        subscribe(ascDecideHandler, net);
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
            int key = getRequest.getKv().getKey();
            InitReadRequest initReadRequest = new InitReadRequest(key, neighbours);
            trigger(initReadRequest, riwm);
        }
    };

    Handler<ReadReturn> readReturnHandler = new Handler<ReadReturn>() {
        @Override
        public void handle(ReadReturn readReturn) {
        }
    };

    Handler<PUTRequest> putRequestHandler = new Handler<PUTRequest>() {
        @Override
        public void handle(PUTRequest putRequest) {
            int key = putRequest.getKv().getKey();
            InitWriteRequest initWriteRequest = new InitWriteRequest(putRequest.getKv());
            trigger(initWriteRequest, riwm);
        }
    };

    Handler<AscPropose> ascProposeHandler = new Handler<AscPropose>() {
        @Override
        public void handle(AscPropose ascPropose) {
        }
    };

    Handler<AscDecide> ascDecideHandler = new Handler<AscDecide>() {
        @Override
        public void handle(AscDecide writeReturn) {
        }
    };

    Handler<WriteReturn> writeReturnHandler = new Handler<WriteReturn>() {
        @Override
        public void handle(WriteReturn writeReturn) {
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
