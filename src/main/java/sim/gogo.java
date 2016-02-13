package sim;

import java.util.HashMap;

/**
 * Created by Robin on 2016-02-12.
 */
public class gogo {

    public static void main (String [] args) {
        HashMap<String, String> map = new HashMap<>();
        map.put("hello","hello");
        map.put("dwad","hello");
        map.put("fwafwa","hello");
        map.put("fawfawf","hello");
        map.entrySet().forEach(node -> {

            System.out.println(map.toString());
        });
    }

    }

