package system.coordination.paxos;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Address;
import se.sics.kompics.network.Network;
import system.coordination.paxos.event.*;
import system.coordination.paxos.port.AbortableSequenceConsensusPort;
import system.network.TAddress;
import system.network.TMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by marcus on 23/02/16.
 */
public class AbortableSequenceConsensus extends ComponentDefinition {

    private int t;
    private int prepts;
    private int ats;
    private ArrayList<Object> av;
    private int al;
    private int pts;
    private ArrayList<Object> pv;
    private int pl;
    private ArrayList<Object> proposedValues;
    private HashMap<TAddress, ReadItem> readlist;
    private HashMap<TAddress, Integer> accepted;
    private HashMap<TAddress, Integer> decided;
    private Integer tsPrime;
    private Integer counter;
    private ArrayList<Object> vsufPrime;

    private ArrayList<TAddress> topology;
    private TAddress self;

    Negative<AbortableSequenceConsensusPort> asc = provides(AbortableSequenceConsensusPort.class);
    Positive<Network> net = requires(Network.class);

    public AbortableSequenceConsensus(Init init) {


        readlist = new HashMap<TAddress, ReadItem>();
        accepted = new HashMap<>();
        decided = new HashMap<>();


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
            t = t + 1;
            Object proposal = event.getProposal();
            if(pts == 0) {
                pts = 1; //Should be assigned to unique number
                pv = prefix(av, al);
                pl = 0;
                proposedValues.add(proposal);
                readlist.clear();
                accepted = new HashMap<>();
                decided = new HashMap<>();
                for(TAddress address : topology) {
                    trigger(new Prepare(self, address, pts, al, t), net);
                }
            }
            else if(readlist.size() <= topology.size()/2) {
                proposedValues.add(proposal);
            }
            else if(!pv.contains(proposal)) {
                pv.add(proposal);
                for (TAddress p : topology){
                    if (readlist.containsKey(p)){
                        ArrayList<Object> temp = new ArrayList<Object>();
                        temp.add(proposal);
                        trigger(new Accept(self,p, pts, temp, pv.size()-1, t), net);
                    }
                }
            }
            }

    };

    private ArrayList prefix(ArrayList <Object> av, int al){
        ArrayList<Object> prefix =  new ArrayList<>();
        for(int i = 0; i < al; i++) {
            prefix.add(av.get(i));
        }
        return prefix;
        }

    private ArrayList<Object> suffix(ArrayList<Object> vsuf, int al) {
        return new ArrayList<Object>(av.subList(al, av.size()));
    }

    Handler<Prepare> prepareHandler = new Handler<Prepare>() {
        @Override
        public void handle(Prepare event) {
            t = Math.max(event.getT(), t) + 1;
            if(event.getPts() < prepts) {
                trigger(new Nack(self, event.getSource(), event.getPts(), t), net);
            }
            else {
                prepts = event.getPts();
                trigger(new PrepareAck(self, event.getSource(), event.getPts(), ats, suffix(av, al), al, t), net);
            }

        }
    };


    Handler<Nack> nackHandler = new Handler<Nack>() {
        @Override
        public void handle(Nack event) {
            t = Integer.max(t, event.getT()) + 1;
            if(event.getTs() == pts) {
                pts = 0;
                trigger(new AscAbort(), asc);
            }
        }
    };



    Handler<PrepareAck> prepareAckHandler = new Handler<PrepareAck>() {
        @Override
        public void handle(PrepareAck event) {
            t = Math.max(event.getT(), t) + 1;
            List<Object> vsuf = event.getVsuf();
            if(event.getTs() == pts) {
                readlist.put(event.getSource(), new ReadItem(event.getAts(), vsuf));
                decided.put(event.getSource(), event.getAl());
                if (readlist.size() == (topology.size() / 2 + 1)) {
                    tsPrime = new Integer(0);
                    vsufPrime = new ArrayList<>();

                    readlist.forEach(new BiConsumer<TAddress, ReadItem>() {
                        @Override
                        public void accept(TAddress source, ReadItem readItem) {
                            if ((tsPrime > readItem.getTs()) || ((tsPrime.equals(readItem.getTs())) && (vsufPrime.size() < readItem.getVsuf().size()))) {
                                tsPrime = new Integer(readItem.getTs());
                                vsufPrime = new ArrayList<Object>(readItem.getVsuf());
                            }
                        }
                    });

                    pv.addAll(vsufPrime);
                    for (Object value : proposedValues) {
                        if (!pv.contains(value)) {
                            pv.add(value);
                        }
                    }

                    for (TAddress node : topology) {
                        if (readlist.get(node) != null) {
                            Integer lPrime = new Integer(decided.get(node));
                            trigger(new Accept(self, node, pts, suffix(pv, lPrime), lPrime, t), net);
                        }
                    }
                }
                else if(readlist.size() > (topology.size()/2 + 1)) {
                    trigger(new Accept(self, event.getSource(), pts, suffix(pv, event.getAl()), event.getAl(), t), net);
                    if(pl != 0) {
                        trigger(new Decide(self, event.getSource(), pts, pl, t), net);

                    }
            }
            }
        }
    };

    Handler<Accept> acceptHandler = new Handler<Accept>() {
        @Override
        public void handle(Accept event) {
            t = Math.max(t, event.getT()) + 1;
            if(event.getTs() != prepts) {
                trigger(new Nack(self, event.getSource(), event.getTs(), t), net);
            }
            else {
                ats = event.getTs();
                if(event.getOffs() < av.size()) {
                    av = prefix(av, event.getOffs());
                }
                av.addAll(event.getVsuf());
                trigger(new AcceptAck(self, event.getSource(), event.getTs(), av.size(), t), net);
            }
        }
    };

    Handler<AcceptAck> acceptAckHandler = new Handler<AcceptAck>() {
        @Override
        public void handle(AcceptAck event) {
            t = Math.max(t, event.getT()) + 1;
            if(event.getPts() == pts) {
                accepted.put(event.getSource(), event.getL());
                //Counts the numbers of acceptors with the >= accepted sequence length compared to the proposer sequence length
                //acceptorSeqLength >= proposerSeqLength
                counter = new Integer(0);
                accepted.forEach(new BiConsumer<TAddress, Integer>() {
                    @Override
                    public void accept(TAddress acceptor, Integer sequenceLength) {
                        if(sequenceLength >= event.getL()) {
                             counter++;
                        }
                    }
                });
                if((pl < event.getL()) && counter > (topology.size()/2)) {
                    pl = new Integer(event.getL());
                    for(TAddress node : topology) {
                        if ((readlist.get(node) != null)) {
                            trigger(new Decide(self, node, pts, pl, t), net);

                        }
                    }
                }
            }
        }
    };

    Handler<Decide> decideHandler = new Handler<Decide>() {
        @Override
        public void handle(Decide event) {
            t = Math.max(t, event.getT()) + 1;
            if(event.getPts() == prepts) {
                while(al < event.getPl()) {
                    trigger(new AscDecide(av.get(al)), asc);
                    al++;
                }
            }

        }
    };












    public static class Init extends se.sics.kompics.Init<AbortableSequenceConsensus> {
    }

}
