package sim.preload;

import system.KVEntry;
import system.data.Bound;
import system.data.ReplicationGroup;
import system.network.TAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class DatastoreFactory {

    static HashMap<Integer, KVEntry> [] stores = new HashMap[6];
    //Even number maps
    static HashMap<Integer, KVEntry> store1 = new HashMap<>();
    static HashMap<Integer, KVEntry> store2 = new HashMap<>();
    static HashMap<Integer, KVEntry> store3 = new HashMap<>();

    static ReplicationGroup [] replicationGroups = new ReplicationGroup[3];

    static Bound[] bounds = new Bound[3];

    {
        Bound bound1 = new Bound(0,9);
        Bound bound2 = new Bound(10,19);
        Bound bound3 = new Bound(20,29);

        bounds[0] = bound1;
        bounds[1] = bound2;
        bounds[2] = bound3;

        store1.put(5,new KVEntry(5,3532, 0));
        store2.put(15,new KVEntry(15, 323, 0));
        store3.put(25,new KVEntry(25, 5644, 0));
        stores[0] = store1;
        stores[1] = store2;
        stores[2] = store3;

        try {

            ArrayList<TAddress> replicationGroup1Nodes = new ArrayList<>();
            replicationGroup1Nodes.add(new TAddress(InetAddress.getByName("192.193.0.1"), 10000));
            replicationGroup1Nodes.add(new TAddress(InetAddress.getByName("192.193.0.2"), 10000));
            replicationGroup1Nodes.add(new TAddress(InetAddress.getByName("192.193.0.3"), 10000));
            ReplicationGroup replicationGroup1 = new ReplicationGroup(bounds[0], replicationGroup1Nodes);
            replicationGroups[0] = replicationGroup1;

            ArrayList<TAddress> replicationGroup2Nodes = new ArrayList<>();
            replicationGroup2Nodes.add(new TAddress(InetAddress.getByName("192.193.0.4"), 10000));
            replicationGroup2Nodes.add(new TAddress(InetAddress.getByName("192.193.0.5"), 10000));
            replicationGroup2Nodes.add(new TAddress(InetAddress.getByName("192.193.0.6"), 10000));
            ReplicationGroup replicationGroup2 = new ReplicationGroup(bounds[1], replicationGroup2Nodes);
            replicationGroups[1] = replicationGroup2;

            ArrayList<TAddress> replicationGroup3Nodes = new ArrayList<>();
            replicationGroup3Nodes.add(new TAddress(InetAddress.getByName("192.193.0.7"), 10000));
            replicationGroup3Nodes.add(new TAddress(InetAddress.getByName("192.193.0.8"), 10000));
            replicationGroup3Nodes.add(new TAddress(InetAddress.getByName("192.193.0.9"), 10000));
            ReplicationGroup replicationGroup3 = new ReplicationGroup(bounds[2], replicationGroup3Nodes);
            replicationGroups[2] = replicationGroup3;
        }
        catch(UnknownHostException uhe) {
            uhe.printStackTrace();
        }
    }

    public static ReplicationGroup getReplicationGroupByIpSuffix(int suffix) {
        System.out.println("suffix " + suffix);
        System.out.println("arrayid " + arrayId(suffix));
        return replicationGroups[arrayId(suffix)];
    }



    public static HashMap<Integer, KVEntry> getHashMapByIpSuffix(int suffix) {
        return (HashMap<Integer, KVEntry>)stores[arrayId(suffix)].clone();
    }

    public static TAddress getReplicationGroupLeader(int suffix) {
        return getReplicationGroupByIpSuffix(suffix).getNodes().get(0);
    }

    public static ArrayList<ReplicationGroup> getReplicationGroups() {
        ArrayList<ReplicationGroup> replicationGroupList = new ArrayList<>();
        for(int i = 0; i < replicationGroups.length; i++) {
            replicationGroupList.add(replicationGroups[i]);
        }
        return replicationGroupList;
    }


    private static int arrayId(int suffix) {
        suffix = suffix-1;
        if(suffix < 3)
            return 0;

        if(suffix < 6)
            return 1;

        if(suffix < 9)
            return 2;
        return 500;
    }

    public static Bound getBoundsByIpSuffix(int suffix) {

        if(suffix <= 3) {
            return bounds[0];
        }
        if(suffix > 3 && suffix <= 6) {
            return bounds[1];
        }
        if(suffix > 6 && suffix <= 9) {
            return bounds[2];
        }
        return null;
    }
}
