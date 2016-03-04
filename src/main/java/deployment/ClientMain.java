package deployment;

import preload.DatastoreFactory;
import preload.DatastoreFactoryDeployment;
import se.sics.kompics.Kompics;
import system.client.ClientParentDeployment;
import system.data.KVEntry;
import system.client.ClientParent;
import system.client.event.CommandMessage;
import system.client.event.GETRequest;
import system.network.TAddress;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by marcus on 03/03/16.
 */
public class ClientMain {

    public static void main(String[] args) {

        int id = Integer.parseInt(args[0]);
        String commandType = args[1];
        int key = Integer.parseInt(args[2]);

        TAddress ip = null;

        int port = 20000;
        try {
            ip = new TAddress(InetAddress.getByName("127.0.0.1"), port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        DatastoreFactoryDeployment datastoreFactory = new DatastoreFactoryDeployment();

        CommandMessage command;
        TAddress dst = datastoreFactory.getReplicationGroupByIpSuffix(2).getNodes().get(1);
        KVEntry kv = new KVEntry(key);

        switch (commandType) {
            case "GET":
                command = new GETRequest(ip, dst, kv, id, 1);
                Kompics.createAndStart(ClientParentDeployment.class, new ClientParentDeployment.Init(ip, command));

        }
    }
}
