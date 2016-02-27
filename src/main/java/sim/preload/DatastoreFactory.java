package sim.preload;

import system.KVEntry;
import system.data.Bound;
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

    static ArrayList<TAddress> replicationGroup1 = new ArrayList<>();
    static ArrayList<TAddress> replicationGroup2 = new ArrayList<>();
    static ArrayList<TAddress> replicationGroup3 = new ArrayList<>();

    static ArrayList<TAddress> [] replicationGroups = new ArrayList[6];

    static ArrayList<TAddress> neighbours = new ArrayList<>();

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
            replicationGroup1.add(new TAddress(InetAddress.getByName("192.193.0.1"), 10000));
            replicationGroup1.add(new TAddress(InetAddress.getByName("192.193.0.2"), 10000));
            replicationGroup1.add(new TAddress(InetAddress.getByName("192.193.0.3"), 10000));

            replicationGroup2.add(new TAddress(InetAddress.getByName("192.193.0.4"), 10000));
            replicationGroup2.add(new TAddress(InetAddress.getByName("192.193.0.5"), 10000));
            replicationGroup2.add(new TAddress(InetAddress.getByName("192.193.0.6"), 10000));

            replicationGroup3.add(new TAddress(InetAddress.getByName("192.193.0.7"), 10000));
            replicationGroup3.add(new TAddress(InetAddress.getByName("192.193.0.8"), 10000));
            replicationGroup3.add(new TAddress(InetAddress.getByName("192.193.0.9"), 10000));


            replicationGroups[0] = replicationGroup1;
            replicationGroups[1] = replicationGroup2;
            replicationGroups[2] = replicationGroup3;

            neighbours.add(new TAddress(InetAddress.getByName("192.193.0.1"), 10000));
            neighbours.add(new TAddress(InetAddress.getByName("192.193.0.2"), 10000));
            neighbours.add(new TAddress(InetAddress.getByName("192.193.0.3"), 10000));
            neighbours.add(new TAddress(InetAddress.getByName("192.193.0.4"), 10000));
            neighbours.add(new TAddress(InetAddress.getByName("192.193.0.5"), 10000));
            neighbours.add(new TAddress(InetAddress.getByName("192.193.0.6"), 10000));
            neighbours.add(new TAddress(InetAddress.getByName("192.193.0.7"), 10000));
            neighbours.add(new TAddress(InetAddress.getByName("192.193.0.8"), 10000));
            neighbours.add(new TAddress(InetAddress.getByName("192.193.0.9"), 10000));

        }
        catch(UnknownHostException uhe) {
            uhe.printStackTrace();
        }



    }

    public static ArrayList<TAddress> getNeighbours() {
        return neighbours;
    }

    public static ArrayList<TAddress> getReplicationGroupByIpSuffix(int suffix) {
        return (ArrayList<TAddress>) replicationGroups[arrayId(suffix)].clone();

    }

    public static HashMap<Integer, KVEntry> getHashMapByIpSuffix(int suffix) {
        return (HashMap<Integer, KVEntry>)stores[arrayId(suffix)].clone();
    }

    public static TAddress getReplicationGroupLeader(int addressSufix) {
        return getReplicationGroupByIpSuffix(addressSufix).get(0);
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
