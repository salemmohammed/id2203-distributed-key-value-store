package system.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import system.beb.BestEffortBroadcastPort;
import system.beb.event.BebBroadcastRequest;
import system.beb.event.BebDeliver;
import system.client.event.CASRequest;
import system.client.event.CommandMessage;
import system.client.event.GETRequest;
import system.data.KVEntry;
import system.client.event.PUTRequest;
import system.coordination.meld.MELDPort;
import system.coordination.meld.event.CheckLeader;
import system.coordination.meld.event.Trust;
import system.coordination.paxos.event.AscAbort;
import system.coordination.paxos.event.AscDecide;
import system.coordination.paxos.event.AscPropose;
import system.coordination.paxos.port.ASCPort;
import system.coordination.rsm.event.ExecuteCommand;
import system.coordination.rsm.event.ExecuteReponse;
import system.coordination.rsm.port.RSMPort;
import system.data.ReplicationGroup;
import system.network.TAddress;
import java.util.*;

public class Node extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Node.class);
    private final TAddress self;
    private ReplicationGroup replicationGroup;
    private TAddress leader;
    private ArrayList<CommandMessage> commandMessageHoldbackQueue = new ArrayList<>();
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
            LOG.info("NODE:" + self + ": Start Event Triggered (Replication= " + replicationGroup+")");
        }
    };

    Handler<GETRequest> getRequestHandler = new Handler<GETRequest>() {
        @Override
        public void handle(GETRequest getRequest) {
            Integer key = getRequest.getKv().getKey();
            //  System.out.println("proposing get");
            if(replicationGroup.withinPartitionSpace(key)) {
            if(self.equals(leader)) {
                trigger(new AscPropose(getRequest), asc);
            }
            else {
                GETRequest getRequestToLeader = new GETRequest(getRequest.getSource(), leader, getRequest.getKv(), getRequest.getPid(), getRequest.getSeqNum());
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
        System.out.println("forwarding to correct group, group size " + replicationGroups.size());
        commandMessage.setPid(self.getId());
        commandMessage.setSeqNum(seqNum);
        seqNum++;
        for(ReplicationGroup replicationGroup : replicationGroups) {
            if(replicationGroup.withinPartitionSpace(key)) {
                BebDeliver bebDeliver = new BebDeliver(commandMessage.getSource(), commandMessage);
                trigger(new BebBroadcastRequest(bebDeliver, replicationGroup.getNodes()), beb);
            }
        }
    }

    Handler<PUTRequest> putRequestHandler = new Handler<PUTRequest>() {
        @Override
        public void handle(PUTRequest putRequest) {
            Integer key = putRequest.getKv().getKey();
            //  System.out.println("proposing get");
            if (replicationGroup.withinPartitionSpace(key)) {
                if (self.equals(leader)) {
                    trigger(new AscPropose(putRequest), asc);
                } else {
                    PUTRequest putRequestToLeader = new PUTRequest(putRequest.getSource(), leader, putRequest.getKv(), putRequest.getPid(), putRequest.getSeqNum());
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
            //  System.out.println("proposing get");
            if (replicationGroup.withinPartitionSpace(key)) {
                if (self.equals(leader)) {
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
            System.out.println("NODE:" + self + " Received AscAbort, checking for new trust..." +  abortEvent);
            trigger(new CheckLeader(), meld);
        }
    };


    Handler<AscDecide> ascDecideHandler = new Handler<AscDecide>() {
        @Override
        public void handle(AscDecide ascDecide) {
            ExecuteCommand executeCommand = new ExecuteCommand((CommandMessage) ascDecide.getValue());
            System.out.println("decided " + executeCommand.getCommandMessage().getDestination());
            trigger(executeCommand, rsm);
        }
    };

    Handler<ExecuteReponse> executeReponseHandler = new Handler<ExecuteReponse>() {
        @Override
        public void handle(ExecuteReponse executeReponse) {
            if(self.equals(leader)) {
                System.out.println(self + " sending response to " + executeReponse.getCommandMessage().getDestination());
                trigger(executeReponse.getCommandMessage(), net);
            }
        }
    };

    Handler<Trust> trustHandler = new Handler<Trust>() {
        @Override
        public void handle(Trust trust) {
            System.out.println("NODE:" + self + " Received trust, new leader is " + trust.getLeader());
            leader = trust.getLeader();
            for(int i = 0; i < commandMessageHoldbackQueue.size(); i++) {
                CommandMessage commandMessage = commandMessageHoldbackQueue.remove(i);
                commandMessage.setDestination(leader);
                trigger(commandMessage, net);
                System.out.println("NODE:" + self + " Forwarding HBQ-MSG " + commandMessage + " to " + commandMessage.getDestination());
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
