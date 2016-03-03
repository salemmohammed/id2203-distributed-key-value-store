package deployment;

import preload.DatastoreFactory;
import se.sics.kompics.Kompics;
import system.KVEntry;
import system.data.Bound;
import system.data.ReplicationGroup;
import system.network.TAddress;
import system.node.NodeParent;

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

        DatastoreFactory datastoreFactory = new DatastoreFactory();


        TAddress self;
        ArrayList<ReplicationGroup> replicationGroups = new ArrayList<>();
        HashMap<Integer, KVEntry> store = new HashMap<>();
        ReplicationGroup replicationGroup;
        TAddress leader;

        int port = 10000 + id;



            try {
                self = new TAddress(InetAddress.getByName("127.0.0." + id), port);
                store = datastoreFactory.getHashMapByIpSuffix(id);
                replicationGroup = datastoreFactory.getReplicationGroupByIpSuffix(id);
                replicationGroups = datastoreFactory.getReplicationGroups();
                System.out.println(self + ": groupsize " + replicationGroups.size());
                leader = datastoreFactory.getReplicationGroupLeader(id);
                System.out.println(id + " my leader is " + leader);

            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }


        Kompics.createAndStart(NodeParent.class, new NodeParent.Init(self, replicationGroups, store, replicationGroup, leader));


    }




}
