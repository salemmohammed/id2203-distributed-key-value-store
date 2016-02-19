package system.coordination;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import system.beb.BestEffortBroadcastPort;
import system.beb.event.BebBroadcastRequest;
import system.beb.event.BebDeliver;
import system.KVEntry;
import system.coordination.event.*;
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
    private ArrayList<KVEntry> readlist = new ArrayList<>();
    private boolean reading = false;
    private int readval;
    private Positive<BestEffortBroadcastPort> beb = requires(BestEffortBroadcastPort.class);
    private Positive<Network> net = requires(Network.class);
    private TAddress self;
    private HashMap<Integer, KVEntry> store;
    private ArrayList<TAddress> neighbours;





    // Algorithm 4.6: 1.1
    public ReadImposeWriteMajority(Init init) {
        this.wts = 0;
        this.acks = 0;
        this.rid = 0;
        this.readval = 0;
        self = init.self;

        this.store = init.store;

    }


    Handler<BebDeliver> bebDeliverHander = new Handler<BebDeliver>() {
        @Override
        public void handle(BebDeliver event) {

            if(event.getData() instanceof ReadRequest) {
                handleReadRequest(event.getSource(), (ReadRequest) event.getData());
            }

            if(event.getData() instanceof WriteRequest) {
                handleWriteRequest(event.getSource(), (WriteRequest) event.getData());
            }
        }
    };

    // Algorithm 4.6: 1.2
    Handler<InitReadRequest> initReadHandler = new Handler<InitReadRequest>() {
        @Override
        public void handle(InitReadRequest event) {
            rid += 1;
            acks = 0;
            readlist = new ArrayList<>();
            reading = true;

            neighbours = neighbours;


            trigger(new BebBroadcastRequest(
                    new BebDeliver(self, new ReadRequest(event.getKey(),rid, neighbours)),
                    neighbours),beb);
        }
    };


    // Algorithm 4.6: 1.3
    private void handleReadRequest(TAddress source,ReadRequest request) {
        Integer key = request.getKey();
        int r = request.getrId();
        KVEntry kv = store.get(key);
        kv.setTimestamp(r);
        trigger(new ReadResponseMessage(self,source,kv, r), net);
    }



    Handler<BebDeliver> readRequestHandler = new Handler<BebDeliver>() {
        @Override
        public void handle(BebDeliver event) {
            ReadRequest request = (ReadRequest) event.getData();
        }
    };

    // Algorithm 4.6: 1.4
    Handler<ReadResponseMessage> readResponseHandler = new Handler<ReadResponseMessage>() {
        @Override
        public void handle(ReadResponseMessage event) {

            int r = event.getrId();

            if(r == rid) {
                KVEntry vtp = event.getValueTimestampPair();
                readlist.add(vtp);

                if(readlist.size() >= 2) {
                    KVEntry maxPair = getMaxTimestampPair();
                    readlist = new ArrayList<>();


                    trigger(new BebBroadcastRequest(
                            new BebDeliver(self,new WriteRequest(maxPair)),
                            neighbours),beb);
                }
            }
        }
    };


    // Algorithm 4.7: 1.5
    Handler<InitWriteRequest> initWriteRequestHandler = new Handler<InitWriteRequest>() {
        @Override
        public void handle(InitWriteRequest event) {
            rid++;
            wts++;
            acks = 0;

            trigger(new BebBroadcastRequest(
                    new BebDeliver(self,new WriteRequest(event.getKVEntry(),wts)),
                    neighbours),beb);
        }
    };

    // Algorithm 4.7: 1.6
    private void handleWriteRequest(TAddress source, WriteRequest request) {
        KVEntry kv = request.getKVEntry();
        Integer key = kv.getKey();
        KVEntry localKv = store.get(key);
        if(localKv.getTimestamp() < kv.getTimestamp()) {
            localKv.setValue(kv.getValue());
            localKv.setTimestamp(kv.getTimestamp());
            store.put(key,localKv);
        }


        int val = kv.getValue();
        trigger(new AckWrite(self, source),net);
    }

    // Algorithm 4.7: 1.7
    Handler<AckWrite> handleAckWrite = new Handler<AckWrite>() {
        @Override
        public void handle(AckWrite event) {
            acks++;

            //// acks >= N/2
            if(acks >= 2) {

                acks = 0;
                if(reading = true) {
                    reading = false;
                    trigger();
                }




            }

        }
    };


    private KVEntry getMaxTimestampPair() {
        KVEntry max = new KVEntry(-1, -1, -1);
        for(KVEntry pair : readlist) {
            if(pair.getTimestamp() > max.getTimestamp()) {
                max = pair;
            }
        }
        return max;
    }




    public static class Init extends se.sics.kompics.Init<ReadImposeWriteMajority> {
        public final TAddress self;
        public final HashMap<Integer, KVEntry> store;

        public Init(TAddress self, HashMap<Integer, KVEntry> store) {
            this.self = self;
            this.store = store;
        }
    }

}
