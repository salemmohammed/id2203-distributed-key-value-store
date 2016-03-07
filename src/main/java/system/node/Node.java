package system.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import system.beb.BestEffortBroadcastPort;
import system.beb.event.BebBroadcastRequest;
import system.beb.event.BebDeliver;
import client.event.CASRequest;
import client.event.CommandMessage;
import client.event.GETRequest;
import system.data.KVEntry;
import client.event.PUTRequest;
import system.meld.MELDPort;
import system.meld.event.CheckLeader;
import system.meld.event.Trust;
import system.multipaxos.event.AscAbort;
import system.multipaxos.event.AscDecide;
import system.multipaxos.event.AscPropose;
import system.multipaxos.port.ASCPort;
import system.rsm.event.ExecuteCommand;
import system.rsm.event.ExecuteReponse;
import system.rsm.port.RSMPort;
import system.data.ReplicationGroup;
import system.network.TAddress;
import java.util.*;

public class Node extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final TAddress self;
    private ReplicationGroup replicationGroup;
    private TAddress leader;
    private ArrayList<CommandMessage> commandMessageHoldbackQueue = new ArrayList<>();
    private ArrayList<CommandMessage> decidedSequence = new ArrayList<>();
    private int seqNum;

    private ArrayList<ReplicationGroup> replicationGroups;
    Positive<Network> net = requires(Network.class);
    Positive<ASCPort> asc = requires(ASCPort.class);
    Positive<MELDPort> meld = requires(MELDPort.class);
    Positive<RSMPort> rsm = requires(RSMPort.class);
    Positive<BestEffortBroadcastPort> beb = requires(BestEffortBroadcastPort.class);

    public Node(Init init) {
        this.self = init.self;
        this.replicationGroups = init.replicationGroups;
        this.replicationGroup = init.replicationGroup;
        this.leader = init.leader;
        seqNum = 0;

        subscribe(startHandler, control);
        subscribe(getRequestHandler, net);
        subscribe(putRequestHandler, net);
        subscribe(casRequestHandler, net);
        subscribe(bebDeliverHandler, beb);
        subscribe(ascDecideHandler, asc);
        subscribe(ascAbortHandler, asc);
        subscribe(executeReponseHandler, rsm);
        subscribe(trustHandler, meld);
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            System.out.println(self + ": Start Event Triggered (Replication= " + replicationGroup+")");
            System.out.println(self + ": My leader is " + leader);
        }
    };

    Handler<GETRequest> getRequestHandler = new Handler<GETRequest>() {
        @Override
        public void handle(GETRequest getRequest) {
            Integer key = getRequest.getKVEntry().getKey();
            if(replicationGroup.withinPartitionSpace(key)) {
            if(self.equals(leader)) {
                System.out.println(self + " Proposing: [" + getRequest.toString() + "]");
                trigger(new AscPropose(getRequest), asc);
            }
            else {
                GETRequest getRequestToLeader = new GETRequest(getRequest.getSource(), leader, getRequest.getKVEntry(), getRequest.getPid(), getRequest.getSeqNum());
                trigger(getRequestToLeader, net);
                seqNum++;
            }
        }
            else {
                forwardToCorrectReplicationGroup(key, getRequest);
            }
            }
    };

    private void forwardToCorrectReplicationGroup(Integer key, CommandMessage commandMessage) {
        commandMessage.setPid(self.getId());
        commandMessage.setSeqNum(seqNum);
        seqNum++;
        int i = 0;
        for(ReplicationGroup replicationGroup : replicationGroups) {
            if(replicationGroup.withinPartitionSpace(key)) {
                System.out.println(commandMessage.getDestination() + " Not my key space! Forwarding to correct group: group " + (i+1));
                BebDeliver bebDeliver = new BebDeliver(commandMessage.getSource(), commandMessage);
                trigger(new BebBroadcastRequest(bebDeliver, replicationGroup.getNodes()), beb);
            }
            i++;
        }
    }

    Handler<PUTRequest> putRequestHandler = new Handler<PUTRequest>() {
        @Override
        public void handle(PUTRequest putRequest) {
            Integer key = putRequest.getKVEntry().getKey();
            if (replicationGroup.withinPartitionSpace(key)) {
                if (self.equals(leader)) {
                    System.out.println(self + " Proposing: [" + putRequest.toString() + "]");
                    trigger(new AscPropose(putRequest), asc);
                } else {
                    PUTRequest putRequestToLeader = new PUTRequest(putRequest.getSource(), leader, putRequest.getKVEntry(), putRequest.getPid(), putRequest.getSeqNum());
                    trigger(putRequestToLeader, net);
                }
            } else {
                forwardToCorrectReplicationGroup(key, putRequest);
            }
        }
    };

    Handler<CASRequest> casRequestHandler = new Handler<CASRequest>() {
        @Override
        public void handle(CASRequest casRequest) {
            Integer key = casRequest.getKVEntry().getKey();
            if (replicationGroup.withinPartitionSpace(key)) {
                if (self.equals(leader)) {
                    System.out.println(self + " Proposing: [" + casRequest.toString() + "]");
                    trigger(new AscPropose(casRequest), asc);
                } else {
                    CASRequest casRequestToLeader = new CASRequest(casRequest.getSource(), leader, casRequest.getKVEntry(), casRequest.getNewValue(), casRequest.getPid(), casRequest.getSeqNum());
                    trigger(casRequestToLeader, net);
                    seqNum++;
                }
            }
            else {
                forwardToCorrectReplicationGroup(key, casRequest);
            }
        }
    };

    Handler<BebDeliver> bebDeliverHandler = new Handler<BebDeliver>() {
        @Override
        public void handle(BebDeliver bebDeliver) {
            CommandMessage command = (CommandMessage)bebDeliver.getData();
            command.setDestination(self);
            if(command instanceof GETRequest) {
                trigger((GETRequest) command, net);
            }
            else if(command instanceof PUTRequest) {
                trigger((PUTRequest) command, net);
            }
            else if(command instanceof CASRequest) {
                trigger((CASRequest) command, net);
            }
        }
    };


    Handler<AscAbort> ascAbortHandler = new Handler<AscAbort>() {
        @Override
        public void handle(AscAbort abortEvent) {
            commandMessageHoldbackQueue.add(abortEvent.getCommandMessage());
            System.out.println(self + " Received AscAbort, checking for new trust...");
            trigger(new CheckLeader(), meld);
        }
    };


    Handler<AscDecide> ascDecideHandler = new Handler<AscDecide>() {
        @Override
        public void handle(AscDecide ascDecide) {
            CommandMessage commandMessage = (CommandMessage) ascDecide.getValue();
            ExecuteCommand executeCommand = new ExecuteCommand(commandMessage);
            ArrayList<Object> av = ascDecide.getAv();

            decidedSequence.add(commandMessage);
            printDecidedSequence();


            trigger(executeCommand, rsm);
        }
    };

    private void printDecidedSequence() {
        System.out.print(self + " Decided Sequence so far: [");
        int i = 0;
        for(Object accepted : decidedSequence) {
            CommandMessage command = (CommandMessage) accepted;
            if(command instanceof GETRequest) {
                System.out.print(((GETRequest) command).toString());
            }
            else if(command instanceof PUTRequest) {
                System.out.print(((PUTRequest) command).toString());
            }
            else if(command instanceof CASRequest) {
                System.out.print(((CASRequest) command).toString());
            }

            i++;
            if(i != decidedSequence.size()) {
                System.out.print(", ");
            }


        }
        System.out.print("]");
        System.out.println("");
    }


    Handler<ExecuteReponse> executeReponseHandler = new Handler<ExecuteReponse>() {
        @Override
        public void handle(ExecuteReponse executeReponse) {
            if(self.equals(leader)) {
                System.out.println(self + " Leader Node Sends Response: " + executeReponse.getCommandMessage().toString() + " to " + executeReponse.getCommandMessage().getDestination());
                trigger(executeReponse.getCommandMessage(), net);
            }
        }
    };

    Handler<Trust> trustHandler = new Handler<Trust>() {
        @Override
        public void handle(Trust trust) {
            System.out.println(self + " Received Trust, new leader is " + trust.getLeader());
            leader = trust.getLeader();
            for(int i = 0; i < commandMessageHoldbackQueue.size(); i++) {
                CommandMessage commandMessage = commandMessageHoldbackQueue.remove(i);
                commandMessage.setDestination(leader);
                trigger(commandMessage, net);
                System.out.println(self + " Forwarding HBQ-MSG " + commandMessage + " to " + commandMessage.getDestination());
            }
        }
    };

    public static class Init extends se.sics.kompics.Init<Node> {

        public final TAddress self;
        public final ArrayList<ReplicationGroup> replicationGroups;
        public TAddress leader;
        public HashMap<Integer, KVEntry> store;
        public ReplicationGroup replicationGroup;

        public Init(TAddress self, ArrayList<ReplicationGroup> replicationGroups, HashMap<Integer, KVEntry> store, ReplicationGroup replicationGroup, TAddress leader) {
            this.self = self;
            this.replicationGroups = replicationGroups;
            this.store = store;
            this.replicationGroup = replicationGroup;
            this.leader = leader;
        }
    }
}
