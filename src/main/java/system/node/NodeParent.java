package system.node;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import system.epfd.EventuallyPerfectFailureDetector;
import system.epfd.port.FDPort;
import system.network.TAddress;

import java.util.ArrayList;
import java.util.HashMap;

public class NodeParent extends ComponentDefinition {

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);

    public NodeParent(Init init) {
        Component node = create(Node.class, new Node.Init(init.self, init.neighbours, init.otherGroupLeader, init.store, init.replicationGroup, init.isLeader));
        connect(node.getNegative(Network.class), network, Channel.TWO_WAY);

        Component epfd = create(EventuallyPerfectFailureDetector.class, new EventuallyPerfectFailureDetector.Init(init.self, init.neighbours));
        connect(epfd.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(node.getNegative(FDPort.class), epfd.getPositive(FDPort.class), Channel.TWO_WAY);
        connect(epfd.getNegative(Timer.class), timer, Channel.TWO_WAY);
    }

    public static class Init extends se.sics.kompics.Init<NodeParent> {

        public final TAddress self;
        public ArrayList<TAddress> neighbours;
        public final TAddress otherGroupLeader;
        public HashMap <Integer, Integer> store;
        ArrayList<TAddress> replicationGroup;
        public boolean isLeader;

        public Init(TAddress self, ArrayList<TAddress> neighbours, TAddress otherGroupLeader, HashMap<Integer, Integer> store, ArrayList<TAddress> replicationGroup, boolean isLeader) {
            this.self = self;
            this.neighbours = neighbours;
            this.otherGroupLeader = otherGroupLeader;
            this.store = store;
            this.replicationGroup = replicationGroup;
            this.isLeader = isLeader;
        }
    }
}
