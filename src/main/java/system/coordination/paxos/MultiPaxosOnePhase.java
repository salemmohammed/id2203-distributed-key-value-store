package system.coordination.paxos;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import system.network.TAddress;
import system.network.TMessage;

import java.util.ArrayList;

/**
 * Created by marcus on 23/02/16.
 */
public class MultiPaxosOnePhase extends ComponentDefinition {

    private int nc;
    private int np;
    private int na;

    private ArrayList<Promise> S = new ArrayList<>();
    private ArrayList<Integer> a = new ArrayList<>();
    private ArrayList<TMessage> vc = new ArrayList<>();
    private ArrayList<TMessage> va = new ArrayList<>();
    private int lambdaC;


    Handler<Propose> proposeHandler = new Handler<Propose>() {
        @Override
        public void handle(Propose event) {
            if(nc == 0) {
                // Do prepare phase
                nc = 1; // should be unique
                S = new ArrayList<>();
                a = new ArrayList<>();
                lambdaC = 0;
                //trigger(new Prepare(), tob);
            } else {

                TMessage c = event.getC();
                if(vc.contains(c)) {
                    vc.add(c);
                }
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

    Handler<Promise> promiseHandler = new Handler<Promise>() {
        @Override
        public void handle(Promise event) {


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

                //trigger(new Accepted, n, va);


            }





        }
    };







    public static class Init extends se.sics.kompics.Init<MultiPaxosOnePhase> {
    }

}
