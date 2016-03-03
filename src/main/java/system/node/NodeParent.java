package system.node;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import system.beb.BestEffortBroadcast;
import system.data.KVEntry;
import system.beb.BestEffortBroadcastPort;
import system.coordination.meld.MELDPort;
import system.coordination.meld.MonarchicalEventualLeaderDetector;
import system.coordination.paxos.AbortableSequenceConsensus;
import system.coordination.paxos.port.ASCPort;
import system.coordination.rsm.ReplicatedStateMachine;
import system.coordination.rsm.port.RSMPort;
import system.data.ReplicationGroup;
import system.epfd.EventuallyPerfectFailureDetector;
import system.port.epfd.FDPort;
import system.network.TAddress;

import java.util.ArrayList;
import java.util.HashMap;

public class NodeParent extends ComponentDefinition {

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);

    public NodeParent(Init init) {
        Component node = create(Node.class, new Node.Init(init.self, init.replicationGroups, init.store, init.replicationGroup, init.leader));
        connect(node.getNegative(Network.class), network, Channel.TWO_WAY);

        Component epfd = create(EventuallyPerfectFailureDetector.class, new EventuallyPerfectFailureDetector.Init(init.self, init.replicationGroup.getNodes()));
        connect(epfd.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(epfd.getNegative(Timer.class), timer, Channel.TWO_WAY);

        Component beb = create(BestEffortBroadcast.class, new BestEffortBroadcast.Init(init.self));
        connect(node.getNegative(BestEffortBroadcastPort.class), beb.getPositive(BestEffortBroadcastPort.class), Channel.TWO_WAY);
        connect(beb.getNegative(Network.class),network, Channel.TWO_WAY);

        Component asc = create(AbortableSequenceConsensus.class, new AbortableSequenceConsensus.Init(init.self, init.replicationGroup.getNodes()));
        connect(node.getNegative(ASCPort.class), asc.getPositive(ASCPort.class), Channel.TWO_WAY);
        connect(asc.getNegative(Network.class), network, Channel.TWO_WAY);

        Component rsm = create(ReplicatedStateMachine.class, new ReplicatedStateMachine.Init(init.self, init.replicationGroup.getBound(), init.store));
        connect(node.getNegative(RSMPort.class), rsm.getPositive(RSMPort.class), Channel.TWO_WAY);

        Component meld = create(MonarchicalEventualLeaderDetector.class, new MonarchicalEventualLeaderDetector.Init(init.replicationGroup.getNodes()));
        connect(node.getNegative(MELDPort.class), meld.getPositive(MELDPort.class), Channel.TWO_WAY);
        connect(meld.getNegative(FDPort.class), epfd.getPositive(FDPort.class), Channel.TWO_WAY);
    }

    public static class Init extends se.sics.kompics.Init<NodeParent> {

        public final TAddress self;
        public ArrayList<ReplicationGroup> replicationGroups;
        public HashMap <Integer, KVEntry> store;
        ReplicationGroup replicationGroup;
        public TAddress leader;

        public Init(TAddress self, ArrayList<ReplicationGroup> replicationGroups, HashMap<Integer, KVEntry> store, ReplicationGroup replicationGroup, TAddress leader) {
            this.self = self;
            this.replicationGroups = replicationGroups;
            this.store = store;
            this.replicationGroup = replicationGroup;
            this.leader = leader;
        }
    }
}
