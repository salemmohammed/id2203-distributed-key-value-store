package sim.preload;

import system.network.TAddress;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Robin on 2016-02-13.
 */
public class DatastoreFactory {

    static HashMap<Integer, Integer> [] stores = new HashMap[6];
    //Even number maps
    static HashMap<Integer, Integer> store1 = new HashMap<>();
    static HashMap<Integer, Integer> store2 = new HashMap<>();
    static HashMap<Integer, Integer> store3 = new HashMap<>();
    static HashMap<Integer, Integer> store4 = new HashMap<>();
    static HashMap<Integer, Integer> store5 = new HashMap<>();
    static HashMap<Integer, Integer> store6 = new HashMap<>();

    static ArrayList<TAddress> replicationGroup1 = new ArrayList<>();
    static ArrayList<TAddress> replicationGroup2 = new ArrayList<>();
    static ArrayList<TAddress> replicationGroup3 = new ArrayList<>();
    static ArrayList<TAddress> replicationGroup4 = new ArrayList<>();
    static ArrayList<TAddress> replicationGroup5 = new ArrayList<>();
    static ArrayList<TAddress> replicationGroup6 = new ArrayList<>();

    static ArrayList<TAddress> [] replicationGroups = new ArrayList[6];

    {
        int split = Integer.MAX_VALUE/6;
        store1.put(split - 10000,3532);
        store2.put(2*split - 14234,4224);
        store3.put(3*split - 13224,4224);
        store4.put(4*split - 432,4224);
        store5.put(5*split - 43535,4224);
        store6.put(6*split - 52532,4224);
        stores[0] = store1;
        stores[1] = store2;
        stores[2] = store3;
        stores[3] = store4;
        stores[4] = store5;
        stores[5] = store6;

        try {
            replicationGroup1.add(new TAddress(InetAddress.getByName("192.193.0.1"), 10000));
            replicationGroup1.add(new TAddress(InetAddress.getByName("192.193.0.2"), 10000));

            replicationGroup2.add(new TAddress(InetAddress.getByName("192.193.0.2"), 10000));
            replicationGroup2.add(new TAddress(InetAddress.getByName("192.193.0.3"), 10000));

            replicationGroup3.add(new TAddress(InetAddress.getByName("192.193.0.3"), 10000));
            replicationGroup3.add(new TAddress(InetAddress.getByName("192.193.0.4"), 10000));

            replicationGroup4.add(new TAddress(InetAddress.getByName("192.193.0.4"), 10000));
            replicationGroup4.add(new TAddress(InetAddress.getByName("192.193.0.5"), 10000));

            replicationGroup5.add(new TAddress(InetAddress.getByName("192.193.0.5"), 10000));
            replicationGroup5.add(new TAddress(InetAddress.getByName("192.193.0.6"), 10000));

            replicationGroup6.add(new TAddress(InetAddress.getByName("192.193.0.6"), 10000));
            replicationGroup6.add(new TAddress(InetAddress.getByName("192.193.0.1"), 10000));

            replicationGroups[0] = replicationGroup1;
            replicationGroups[1] = replicationGroup2;
            replicationGroups[2] = replicationGroup3;
            replicationGroups[3] = replicationGroup4;
            replicationGroups[4] = replicationGroup5;
            replicationGroups[5] = replicationGroup6;
        }
        catch(UnknownHostException uhe) {
            uhe.printStackTrace();
        }



    }

    public static ArrayList<TAddress> getReplicationGroupByIpSuffix(int suffix) {
        return replicationGroups[suffix-1];
    }

    public static HashMap<Integer, Integer> getHashMapByIpSuffix(int suffix) {
        return stores[suffix-1];
    }
}
