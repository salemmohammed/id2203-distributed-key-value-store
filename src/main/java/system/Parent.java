package system;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

import java.util.HashMap;

public class Parent extends ComponentDefinition {

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);


    public Parent(Init init) {
        Component node = create(Node.class, new Node.Init(init.self, init.neighbours, init.otherGroupLeader, init.isLeader));
        connect(node.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(node.getNegative(Timer.class), timer, Channel.TWO_WAY);
    }



    public static class Init extends se.sics.kompics.Init<Parent> {

        public final TAddress self;
        HashMap<String, TAddress> neighbours;
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
