package system.client;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import system.network.TAddress;
import system.network.TMessage;

import java.util.ArrayList;

public class ClientParent extends ComponentDefinition {

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);

    public ClientParent(Init init) {
        Component client = create(Client.class, new Client.Init(init.self, init.nodes, init.command));
        connect(client.getNegative(Network.class), network, Channel.TWO_WAY);
    }

    public static class Init extends se.sics.kompics.Init<ClientParent> {

        private final TAddress self;
        private ArrayList<TAddress> nodes;
        private TMessage command;

        public Init(TAddress self, ArrayList<TAddress> nodes, TMessage command) {
            this.self = self;
            this.nodes = nodes;
            this.command = command;
        }
    }
}
