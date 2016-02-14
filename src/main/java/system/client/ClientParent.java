package system.client;

import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import system.network.TAddress;

import java.util.ArrayList;

/**
 * Created by Robin on 2016-02-13.
 */
public class ClientParent extends ComponentDefinition {

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);

    public ClientParent(Init init) {
        Component client = create(Client.class, new Client.Init(init.self, init.nodes));
        connect(client.getNegative(Network.class), network, Channel.TWO_WAY);
    }

    public static class Init extends se.sics.kompics.Init<ClientParent> {

        public final TAddress self;
        ArrayList<TAddress> nodes;

        public Init(TAddress self, ArrayList<TAddress> nodes) {
            this.self = self;
            this.nodes = nodes;
        }
    }
}
