package system.pp2p;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import system.network.TAddress;
import system.port.pp2p.PerfectPointToPointLink;
import system.port.pp2p.Pp2pDeliver;
import system.port.pp2p.Pp2pSend;

import java.sql.Time;

/**
 * Created by marcus on 14/02/16.
 */
public class StubbornLink extends ComponentDefinition{

    private Negative<PerfectPointToPointLink> pp2p = provides(PerfectPointToPointLink.class);
    private Positive<Network> network = requires(Network.class);
    private Positive<Timer> timer = requires(Timer.class);

    private TAddress self;


    private Handler<Pp2pSend> handlePp2pSend = new Handler<Pp2pSend>() {
        @Override
        public void handle(Pp2pSend event) {
            TAddress dest = event.getDestination();

            if(dest.equals(self)) {
                // Deliver
                Pp2pDeliver dEvent = event.getDeliverEvent();
                trigger(dEvent, pp2p);
            }






        }
    }

}
