package system.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import system.client.event.GETReply;
import system.client.event.GETRequest;
import system.KVEntry;
import system.client.event.PUTReply;
import system.network.TAddress;
import system.network.TMessage;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Robin on 2016-02-13.
 */
public class Client extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);
    Positive<Network> net = requires(Network.class);
    private final ArrayList<TAddress> nodes;
    private final TAddress self;
    private TMessage command;

    public Client(Init init) {
        self = init.self;
        nodes = init.nodes;
        command = init.command;

        subscribe(startHandler, control);
        subscribe(getReplyHandler, net);
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
                trigger((command), net);
        }
    };

    Handler<GETReply> getReplyHandler = new Handler<GETReply>() {
        @Override
        public void handle(GETReply getReply) {
            System.out.println("received GETREPLY: key-" + getReply.getKey() + " value-" +getReply.getKeyValue());
         //   System.out.println(self+": Received GETReply KEY: " + getReply.getKeyValue().getKey() + " VALUE: " + getReply.getKeyValue().getValue());
        }
    };

    Handler<PUTReply> putReplyHandler = new Handler<PUTReply>() {
        @Override
        public void handle(PUTReply getReply) {
            System.out.println("received PUTREPLY: key-" + getReply.getKey() + " value-" +getReply.getKeyValue());
            //   System.out.println(self+": Received GETReply KEY: " + getReply.getKeyValue().getKey() + " VALUE: " + getReply.getKeyValue().getValue());
        }
    };

    public static class Init extends se.sics.kompics.Init<Client> {

        public final ArrayList<TAddress> nodes;
        public TAddress self;
        private TMessage command;

        public Init(TAddress self, ArrayList<TAddress> nodes, TMessage command) {
            this.self = self;
            this.nodes = nodes;
            this.command = command;
        }
    }
}
