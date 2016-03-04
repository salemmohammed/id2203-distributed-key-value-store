package system.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import system.client.event.*;
import system.network.TAddress;

public class Client extends ComponentDefinition {

    private static final Logger LOG = LoggerFactory.getLogger(Client.class);
    Positive<Network> net = requires(Network.class);
    private final TAddress self;
    private int seqNum;
    private CommandMessage command;

    public Client(Init init) {
        self = init.self;
        command = init.command;
        seqNum = 0;

        subscribe(startHandler, control);
        subscribe(getReplyHandler, net);
        subscribe(putReplyHandler, net);
        subscribe(casReplyHandler, net);
    }

    Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start event) {
            command.setSeqNum(seqNum);
            seqNum++;
            trigger(command, net);
            System.out.println("\nCLIENT" + self + " Sent " + command + " to " + command.getDestination());
        }
    };

    Handler<GETReply> getReplyHandler = new Handler<GETReply>() {
        @Override
        public void handle(GETReply getReply) {
            System.out.println("\nCLIENT" + self + ": Received GETREPLY key-" + getReply.getKVEntry().getKey() + " value-" +getReply.getKVEntry().getValue()  + " FROM " + getReply.getSource());
        }
    };

    Handler<PUTReply> putReplyHandler = new Handler<PUTReply>() {
        @Override
        public void handle(PUTReply putReply) {
            System.out.println("\nCLIENT" + self + ": Received PUTREPLY: key-" + putReply.getKv().getKey() + " value-"+putReply.getKv().getValue() + " success-" + putReply.successful  + " FROM " + putReply.getSource());
        }
    };

    Handler<CASReply> casReplyHandler = new Handler<CASReply>() {
        @Override
        public void handle(CASReply casReply) {
            if(casReply.successful) {
                System.out.println("\nCLIENT" + self + ": Received CASREPLY: key-" + casReply.getKVEntry().getKey() + " referenceValue-" + casReply.getOldValue() + " newValue-"+casReply.getKVEntry().getValue() + " success-" + casReply.successful + " FROM " + casReply.getSource());
            }
            else {
                System.out.println("\nCLIENT" + self + ": Received CASREPLY: key-" + casReply.getKVEntry().getKey() + " referenceValue-" + casReply.getKVEntry().getValue() + " newValue-" + casReply.getOldValue() + " success-" + casReply.successful  + " FROM " + casReply.getSource());
            }
        }
    };

    public static class Init extends se.sics.kompics.Init<Client> {

        public TAddress self;
        private CommandMessage command;

        public Init(TAddress self, CommandMessage command) {
            this.self = self;
            this.command = command;
        }
    }
}
