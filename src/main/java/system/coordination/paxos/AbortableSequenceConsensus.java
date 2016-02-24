package system.coordination.paxos;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import system.coordination.paxos.event.*;
import system.coordination.paxos.port.AbortableSequenceConsensusPort;
import system.network.TMessage;

import java.util.ArrayList;

/**
 * Created by marcus on 23/02/16.
 */
public class AbortableSequenceConsensus extends ComponentDefinition {

    private int nc;
    private int np;
    private int na;

    private ArrayList<PrepareAck> S = new ArrayList<>();
    private ArrayList<Integer> a = new ArrayList<>();
    private ArrayList<TMessage> vc = new ArrayList<>();
    private ArrayList<TMessage> va = new ArrayList<>();
    private int lambdaC;

    Negative<AbortableSequenceConsensusPort> asc = provides(AbortableSequenceConsensusPort.class);
    Positive<Network> net = requires(Network.class);

    public AbortableSequenceConsensus(Init init) {
        subscribe(proposeHandler, asc);
        subscribe(prepareHandler, net);
        subscribe(nackHandler, asc);
        subscribe(prepareAckHandler, net);
        subscribe(acceptHandler, net);
        subscribe(acceptAckHandler, net);
        subscribe(decideHandler, net);
    }


    Handler<AscPropose> proposeHandler = new Handler<AscPropose>() {
        @Override
        public void handle(AscPropose event) {
            if(nc == 0) {
                // Do prepare phase
                nc = 1; // should be unique
                S = new ArrayList<>();
                a = new ArrayList<>();
                lambdaC = 0;
                //trigger(new Prepare(), tob);
            } else {


                //trigger(new Accept,tob);

            }
        }
    };



    Handler<Prepare> prepareHandler = new Handler<Prepare>() {
        @Override
        public void handle(Prepare event) {
            int n = event.getN();
            if(np < n) {
                np = n;
            }
            //trigger(new Promise);
        }
    };


    Handler<Nack> nackHandler = new Handler<Nack>() {
        @Override
        public void handle(Nack event) {

        }
    };



    Handler<PrepareAck> prepareAckHandler = new Handler<PrepareAck>() {
        @Override
        public void handle(PrepareAck event) {

        }
    };

    Handler<Accept> acceptHandler = new Handler<Accept>() {
        @Override
        public void handle(Accept event) {
            int n = event.getNc();
            ArrayList<TMessage> v = event.getVc();
            if(np <= n ) {
                np = n;

                if(n > na) {
                    na = n;
                    va = v;
                }
            }
        }
    };

    Handler<AcceptAck> acceptAckHandler = new Handler<AcceptAck>() {
        @Override
        public void handle(AcceptAck event) {

        }
    };

    Handler<Decide> decideHandler = new Handler<Decide>() {
        @Override
        public void handle(Decide event) {

        }
    };












    public static class Init extends se.sics.kompics.Init<AbortableSequenceConsensus> {
    }

}
