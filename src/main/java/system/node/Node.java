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
import system.coordination.meld.MELDPort;
import system.coordination.meld.event.CheckLeader;
import system.coordination.meld.event.Trust;
import system.coordination.paxos.event.AscAbort;
import system.coordination.paxos.event.AscDecide;
import system.coordination.paxos.event.AscPropose;
import system.coordination.paxos.port.ASCPort;
import system.coordination.rsm.ReplicatedStateMachine;
import system.coordination.rsm.event.ExecuteCommand;
import system.coordination.rsm.event.ExecuteReponse;
import system.coordination.rsm.port.RSMPort;
import system.data.Bound;
import system.network.TAddress;
import java.util.*;

public class Node extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final TAddress self;
    private ArrayList<TAddress> replicationGroup;
    private Bound bounds;
    private TAddress leader;
    private ArrayList<Command> commandHoldbackQueue = new ArrayList<>();

    private final ArrayList<TAddress> neighbours;
    Positive<Network> net = requires(Network.class);
    Positive<ASCPort> asc = requires(ASCPort.class);
    Positive<MELDPort> meld = requires(MELDPort.class);
    Positive<RSMPort> rsm = requires(RSMPort.class);

    public Node(Init init) {
        this.self = init.self;
        this.neighbours = init.neighbours;
        this.replicationGroup = init.replicationGroup;
        this.leader = init.leader;
        this.bounds = init.bounds;

        subscribe(startHandler, control);

        subscribe(getRequestHandler, net);
        subscribe(putRequestHandler, net);
        subscribe(casRequestHandler, net);

        subscribe(ascDecideHandler, asc);
        subscribe(ascAbortHandler, asc);

        subscribe(executeReponseHandler, rsm);

        subscribe(trustHandler, meld);
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            LOG.info(self.toString() + ": Start Event Triggered (Replication= " + replicationGroup+")");
        }
    };

    Handler<GETRequest> getRequestHandler = new Handler<GETRequest>() {
        @Override
        public void handle(GETRequest getRequest) {
            //  System.out.println("proposing get");
            if(self.equals(leader)) {
                trigger(new AscPropose(getRequest), asc);
            }
            else {
                GETRequest getRequestToLeader = new GETRequest(getRequest.getSource(), leader, getRequest.getKv());
                trigger(getRequestToLeader, net);
            }

        }
    };

    Handler<PUTRequest> putRequestHandler = new Handler<PUTRequest>() {
        @Override
        public void handle(PUTRequest putRequest) {
            if(self.equals(leader)) {
                trigger(new AscPropose(putRequest), asc);
            }
            else {
                PUTRequest putRequestToLeader = new PUTRequest(putRequest.getSource(), leader, putRequest.getKv());
                trigger(putRequestToLeader, net);
            }
        }
    };

    Handler<CASRequest> casRequestHandler = new Handler<CASRequest>() {
        @Override
        public void handle(CASRequest casRequest) {
            // System.out.println("proposing cas");
            if(self.equals(leader)) {
                trigger(new AscPropose(casRequest), asc);
            }
            else {
                CASRequest casRequestToLeader = new CASRequest(casRequest.getSource(), leader, casRequest.getKVEntry(), casRequest.getNewValue());
                trigger(casRequestToLeader, net);
            }
        }
    };


    Handler<AscAbort> ascAbortHandler = new Handler<AscAbort>() {
        @Override
        public void handle(AscAbort event) {
            commandHoldbackQueue.add(event.getCommand());
            System.out.println(self + " " + event);
            trigger(new CheckLeader(), meld);
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
            if(self.equals(leader)) {
                trigger(executeReponse.getCommand(), net);
            }
        }
    };

    Handler<Trust> trustHandler = new Handler<Trust>() {
        @Override
        public void handle(Trust trust) {
            System.out.println(self + " Received trust, new leader is " + trust.getLeader());
            leader = trust.getLeader();
            for(int i = 0; i < commandHoldbackQueue.size(); i++) {
                Command command = commandHoldbackQueue.remove(i);
                command.setDestination(leader);
                trigger(command, net);
                System.out.println(self + " Forwarding HBQ-MSG " + command + " to " + command.getDestination());
            }
        }
    };

    public static class Init extends se.sics.kompics.Init<Node> {

        public final TAddress self;
        public final ArrayList<TAddress> neighbours;
        public TAddress leader;
        public HashMap<Integer, KVEntry> store;
        public ArrayList<TAddress> replicationGroup;
        public Bound bounds;

        public Init(TAddress self, ArrayList<TAddress> neighbours, HashMap<Integer, KVEntry> store, ArrayList<TAddress> replicationGroup, TAddress leader, Bound bounds) {
            this.self = self;
            this.neighbours = neighbours;
            this.store = store;
            this.replicationGroup = replicationGroup;
            this.leader = leader;
            this.bounds = bounds;
        }
    }
}
