package sim.preload;

import system.KVEntry;
import system.network.TAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Robin on 2016-02-13.
 */
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

    {
        int split = Integer.MAX_VALUE/3;
        store1.put(split - 10000,new KVEntry(0,3532));
        store2.put(2*split - 14234,new KVEntry(0,4224));
        store3.put(3*split - 13224,new KVEntry(0,234234));
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
        return replicationGroups[arrayId(suffix)];

    }

    public static HashMap<Integer, KVEntry> getHashMapByIpSuffix(int suffix) {
        return stores[arrayId(suffix)];
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


}
