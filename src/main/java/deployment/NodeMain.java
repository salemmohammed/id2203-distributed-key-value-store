package deployment;

import preload.DatastoreFactory;
import preload.DatastoreFactoryDeployment;
import se.sics.kompics.Kompics;
import system.data.KVEntry;
import system.data.ReplicationGroup;
import system.network.TAddress;
import system.node.NodeParent;
import system.node.NodeParentDeployment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Robin on 2016-03-02.
 */
public class NodeMain {
    public static void main(String [] args) {
        int id = Integer.parseInt(args[0]);

        DatastoreFactoryDeployment datastoreFactory = new DatastoreFactoryDeployment();


        TAddress self;
        ArrayList<ReplicationGroup> replicationGroups = new ArrayList<>();
        HashMap<Integer, KVEntry> store = new HashMap<>();
        ReplicationGroup replicationGroup;
        TAddress leader;

        int port = 10000 + id;

            try {
                self = new TAddress(InetAddress.getByName("127.0.0.1"), port);
                store = datastoreFactory.getHashMapByIpSuffix(id);
                replicationGroup = datastoreFactory.getReplicationGroupByIpSuffix(id);
                replicationGroups = datastoreFactory.getReplicationGroups();
                System.out.println(self + ": groupsize " + replicationGroups.size());
                leader = datastoreFactory.getReplicationGroupLeader(id);
                System.out.println(id + " my leader is " + leader);

            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }

        Kompics.createAndStart(NodeParentDeployment.class, new NodeParentDeployment.Init(self, replicationGroups, store, replicationGroup, leader));
    }
}
