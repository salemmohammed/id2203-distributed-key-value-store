package system;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.timer.java.JavaTimer;

import java.util.ArrayList;
import java.util.HashMap;

public class Parent extends ComponentDefinition {

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);


   //Component timer = create(JavaTimer.class, Init.NONE); //NEW

    public Parent(Init init) {
        Component node = create(Node.class, new Node.Init(init.self, init.neighbours, init.otherGroupLeader, init.isLeader));
        connect(node.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(node.getNegative(Timer.class), timer, Channel.TWO_WAY);


        Component epfd = create(EPFD.class, new EPFD.Init(init.self, init.neighbours));
        connect(epfd.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(node.getNegative(FDPort.class), epfd.getPositive(FDPort.class), Channel.TWO_WAY);

    }

    public static class Init extends se.sics.kompics.Init<Parent> {

        public final TAddress self;
        ArrayList<TAddress> neighbours;
        public final TAddress otherGroupLeader;
        boolean isLeader;


        public Init(TAddress self, ArrayList<TAddress> neighbours, TAddress otherGroupLeader, boolean isLeader) {
            this.self = self;
            this.neighbours = neighbours;
            this.otherGroupLeader = otherGroupLeader;
            this.isLeader = isLeader;
        }
    }

}
