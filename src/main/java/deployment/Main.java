package deployment;

import java.net.InetAddress;

/**
 * Created by Robin on 2016-03-02.
 */
public class Main {
    public static void main(String [] args) {
        InetAddress ip = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);
    }
}
