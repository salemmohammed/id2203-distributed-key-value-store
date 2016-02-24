package system.node;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import system.beb.BestEffortBroadcast;
import system.beb.BestEffortBroadcastPort;
import system.KVEntry;
import system.coordination.riwm.ReadImposeWriteMajority;
import system.coordination.riwm.port.RIWMPort;
import system.data.Bound;
import system.epfd.EventuallyPerfectFailureDetector;
import system.port.epfd.FDPort;
import system.network.TAddress;

import java.util.ArrayList;
import java.util.HashMap;

public class NodeParent extends ComponentDefinition {

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);

    public NodeParent(Init init) {
        Component node = create(Node.class, new Node.Init(init.self, init.neighbours, init.store, init.replicationGroup, init.isLeader, init.bounds));
        connect(node.getNegative(Network.class), network, Channel.TWO_WAY);

        Component epfd = create(EventuallyPerfectFailureDetector.class, new EventuallyPerfectFailureDetector.Init(init.self, init.neighbours));
        connect(epfd.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(node.getNegative(FDPort.class), epfd.getPositive(FDPort.class), Channel.TWO_WAY);
        connect(epfd.getNegative(Timer.class), timer, Channel.TWO_WAY);

        Component riwm = create(ReadImposeWriteMajority.class, new ReadImposeWriteMajority.Init(init.self ,init.store, init.neighbours, init.bounds));
        connect(node.getNegative(RIWMPort.class), riwm.getPositive(RIWMPort.class), Channel.TWO_WAY);
        connect(riwm.getNegative(Network.class),network, Channel.TWO_WAY);

        Component beb = create(BestEffortBroadcast.class, new BestEffortBroadcast.Init(init.self));
        connect(beb.getNegative(Network.class),network, Channel.TWO_WAY);
        connect(riwm.getNegative(BestEffortBroadcastPort.class), beb.getPositive(BestEffortBroadcastPort.class), Channel.TWO_WAY);
    }

    public static class Init extends se.sics.kompics.Init<NodeParent> {

        public final TAddress self;
        public ArrayList<TAddress> neighbours;
        public HashMap <Integer, KVEntry> store;
        ArrayList<TAddress> replicationGroup;
        public boolean isLeader;
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
