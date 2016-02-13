package sim.preload;

import java.util.HashMap;

/**
 * Created by Robin on 2016-02-13.
 */
public class DatastoreFactory {

    static HashMap<Integer, Integer> [] evenMaps = new HashMap[3];
    //Even number maps
    static HashMap<Integer, Integer> hm1 = new HashMap<>();
    static HashMap<Integer, Integer> hm2 = new HashMap<>();
    static HashMap<Integer, Integer> hm3 = new HashMap<>();

    static HashMap<Integer, Integer> [] unevenMaps = new HashMap[3];
    //Uneven number maps
    static HashMap<Integer, Integer> hm4 = new HashMap<>();
    static HashMap<Integer, Integer> hm5 = new HashMap<>();
    static HashMap<Integer, Integer> hm6 = new HashMap<>();

    {
        hm1.put(2,51);
        hm2.put(4,324);
        hm3.put(6,33);
        evenMaps[0] = hm1;
        evenMaps[1] = hm2;
        evenMaps[2] = hm3;

        hm4.put(3,435);
        hm5.put(6,48);
        hm6.put(9,15);
        unevenMaps[0] = hm4;
        unevenMaps[1] = hm5;
        unevenMaps[2] = hm6;
    }

    public static HashMap<Integer, Integer> getEvenHashMapByIpSuffix(int suffix) {
        return evenMaps[suffix-1];
    }

    public static HashMap<Integer, Integer> getUnevenHashMapByIpSuffix(int suffix) {
        return unevenMaps[suffix-4];
    }
}
