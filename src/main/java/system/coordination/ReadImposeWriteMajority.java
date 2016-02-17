package system.coordination;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import system.beb.BestEffortBroadcastPort;
import system.beb.event.BebBroadcastRequest;
import system.beb.event.BebDeliver;
import system.client.event.ValueTimestampPair;
import system.coordination.event.ReadRequest;
import system.coordination.event.ReadResponseMessage;
import system.network.TAddress;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by marcus on 17/02/16.
 */
public class ReadImposeWriteMajority extends ComponentDefinition {

    private int wts;
    private int acks;
    private int rid;
    private ArrayList<Integer> readlist = new ArrayList<>();
    private boolean reading = false;
    private int readval;
    private Positive<BestEffortBroadcastPort> beb = requires(BestEffortBroadcastPort.class);
    private Positive<Network> net = requires(Network.class);
    private TAddress self;
    private HashMap<Integer, ValueTimestampPair> store;



    public ReadImposeWriteMajority(Init init) {
        this.wts = 0;
        this.acks = 0;
        this.rid = 0;
        this.readval = 0;
        self = init.self;

        this.store = init.store;

    }

    Handler<ReadRequest> initReadHandler = new Handler<ReadRequest>() {
        @Override
        public void handle(ReadRequest event) {
            rid += 1;
            acks = 0;
            readlist = new ArrayList<>();
            reading = true;

            trigger(new BebBroadcastRequest(new BebDeliver(self,new Integer(event.getKey())), event.getNeighbours()),beb);
        }
    };


    Handler<BebDeliver> readRequestHandler = new Handler<BebDeliver>() {
        @Override
        public void handle(BebDeliver event) {
            Integer key = (Integer) event.getData();
            ValueTimestampPair vtp = store.get(key);
            trigger(new ReadResponseMessage(self,event.getSource(),vtp), net);


        }
    };


    Handler<BebDeliver> ackHandler = new Handler<BebDeliver>() {
        @Override
        public void handle(BebDeliver event) {

        }
    };

    public static class Init extends se.sics.kompics.Init<ReadImposeWriteMajority> {
        public final TAddress self;
        public final HashMap<Integer, ValueTimestampPair> store;

        public Init(TAddress self, HashMap<Integer, ValueTimestampPair> store) {
            this.self = self;
            this.store = store;
        }
    }

}
