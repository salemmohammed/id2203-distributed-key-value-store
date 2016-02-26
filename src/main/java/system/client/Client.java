package system.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import system.client.event.CASReply;
import system.client.event.GETReply;
import system.client.event.GETRequest;
import system.KVEntry;
import system.client.event.PUTReply;
import system.network.TAddress;
import system.network.TMessage;

import java.util.ArrayList;
import java.util.Iterator;

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
        subscribe(putReplyHandler, net);
        subscribe(casReplyHandler, net);
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            System.out.println("Sending command " + command);
                trigger((command), net);
        }
    };

    Handler<GETReply> getReplyHandler = new Handler<GETReply>() {
        @Override
        public void handle(GETReply getReply) {
            System.out.println(self + ": Received GETREPLY key-" + getReply.getKVEntry().getKey() + " value-" +getReply.getKVEntry().getValue());
         //   System.out.println(self+": Received GETReply KEY: " + getReply.getKeyValue().getKey() + " VALUE: " + getReply.getKeyValue().getValue());
        }
    };

    Handler<PUTReply> putReplyHandler = new Handler<PUTReply>() {
        @Override
        public void handle(PUTReply putReply) {
            System.out.println(self +  ": Received PUTREPLY: key-" + putReply.getKv().getKey() + " value-"+putReply.getKv().getValue() + " success-" + putReply.successful);
            //   System.out.println(self+": Received GETReply KEY: " + getReply.getKeyValue().getKey() + " VALUE: " + getReply.getKeyValue().getValue());
        }
    };

    Handler<CASReply> casReplyHandler = new Handler<CASReply>() {
        @Override
        public void handle(CASReply casReply) {
            if(casReply.successful) {
                System.out.println(self + ": Received CASREPLY: key-" + casReply.getKVEntry().getKey() + " referenceValue-" + casReply.getOldValue() + " newValue-"+casReply.getKVEntry().getValue());
            }
            else {
                System.out.println(self + ": Received CASREPLY: key-" + casReply.getKVEntry().getKey() + " referenceValue-" + casReply.getKVEntry().getValue() + " newValue-" + casReply.getOldValue());
            }
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
