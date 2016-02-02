import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.netty.NettyInit;
import se.sics.kompics.network.netty.NettyNetwork;
import sun.nio.ch.Net;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Parent extends ComponentDefinition {

    HashMap<String, TAddress> nodes = new HashMap<>();

    public Parent() {

        try {
            TAddress node1 = new TAddress(Inet4Address.getLocalHost(), 6666);
            TAddress node2 = new TAddress(Inet4Address.getLocalHost(), 6667);
            nodes.put("node1", node1);
            nodes.put("node2", node2);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        Component node1 = create(Node.class, new Node.Init(nodes.get("node1"), getNeighbours("node1", nodes)));
        Component node2 = create(Node.class, new Node.Init(nodes.get("node2"), getNeighbours("node2", nodes)));

        System.out.println(nodes.size());
        Component networkNode1 = create(NettyNetwork.class, new NettyInit(nodes.get("node1")));
        Component networkNode2 = create(NettyNetwork.class, new NettyInit(nodes.get("node2")));
        connect(node1.getNegative(Network.class), networkNode1.getPositive(Network.class), Channel.TWO_WAY);
        connect(node2.getNegative(Network.class), networkNode2.getPositive(Network.class), Channel.TWO_WAY);
    }

    {

    }


    private HashMap<String, TAddress> getNeighbours(String self, HashMap<String, TAddress> nodes) {

        HashMap<String, TAddress> neighbours = new HashMap<>();

        Iterator it = nodes.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if(!pair.getKey().equals(self)) {
                neighbours.put((String) pair.getKey(), (TAddress) pair.getValue());
            }
        }

        return neighbours;
    }

}
