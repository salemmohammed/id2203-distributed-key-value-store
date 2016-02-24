package system.coordination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.Transport;
import system.beb.BestEffortBroadcastPort;
import system.beb.event.BebBroadcastRequest;
import system.beb.event.BebDeliver;
import system.KVEntry;
import system.coordination.event.*;
import system.coordination.port.RIWMPort;
import system.data.Bound;
import system.network.TAddress;
import system.port.epfd.FDPort;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by marcus on 17/02/16.
 */
public class ReadImposeWriteMajority extends ComponentDefinition {

    private Positive<Network> net = requires(Network.class);
    private TAddress self;

    private static final Logger LOG = LoggerFactory.getLogger(ReadImposeWriteMajority.class);

    private HashMap<Integer, KVEntry> store;
    private HashMap<Integer, Integer> readvals = new HashMap<>();
    private HashMap<Integer, Integer> rids = new HashMap<>();
    private HashMap<Integer, ArrayList<KVEntry>> readlists = new HashMap<>();
    private HashMap<Integer, Integer> acks = new HashMap<>();
    private HashMap<Integer, Boolean> readings = new HashMap<>();
    private HashMap<Integer, Integer> wts = new HashMap<>();
    private Bound bounds;

    Negative<RIWMPort> riwm = provides(RIWMPort.class);
    private Positive<BestEffortBroadcastPort> beb = requires(BestEffortBroadcastPort.class);

    private ArrayList<TAddress> neighbours;
    // Algorithm 4.6: 1.1
    public ReadImposeWriteMajority(Init init) {
        self = init.self;
        this.store = init.store;
        this.bounds = init.bounds;

        this.neighbours = init.neighbours;

        subscribe(bebDeliverHandler, beb);
        subscribe(initReadHandler, riwm);
        subscribe(initWriteRequestHandler, riwm);
        subscribe(readResponseHandler, net);
        subscribe(handleAckWrite, net);
    }


    Handler<BebDeliver> bebDeliverHandler = new Handler<BebDeliver>() {
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
            LOG.info(self + " Starting read at time " + System.currentTimeMillis() + " Key-" + event.getKey());
            Integer key = event.getKey();
            if(!rids.containsKey(key)) {
                rids.put(key, 0);
            }
                rids.put(key, rids.get(key) + 1);

                acks.put(key, 0);

            readlists.put(key, new ArrayList<>());
            readings.put(key, true);

            trigger(new BebBroadcastRequest(
                    new BebDeliver(self, new ReadRequest(key, rids.get(key))),
                    neighbours),beb);
        }
    };


    // Algorithm 4.6: 1.3
    private void handleReadRequest(TAddress source,ReadRequest request) {
        Integer key = request.getKey();
        int r = request.getrId();
        KVEntry kv;
        if(!store.containsKey(key)) {
            kv = new KVEntry(key, -1, -1);
        }
        else {
            kv = store.get(key);
        }
        trigger(new ReadResponseMessage(self,source,kv, r), net);
    }

    // Algorithm 4.6: 1.4 //TODO we receive duplicate read response from one node
    Handler<ReadResponseMessage> readResponseHandler = new Handler<ReadResponseMessage>() {
        @Override
        public void handle(ReadResponseMessage event) {
            int r = event.getrId();
            Integer key = event.getKv().getKey();
            if(r == rids.get(key)) {
                ArrayList readlist = readlists.get(key);
                readlist.add(event.getKv());
                if(readlist.size() >= 2) {
                    KVEntry maxPair = getMaxTimestampPair(key);
                    readlists.put(key, new ArrayList<>());
                    readvals.put(maxPair.getKey(), maxPair.getValue());
                    trigger(new BebBroadcastRequest(
                            new BebDeliver(self,new WriteRequest(maxPair, rids.get(key))),
                            neighbours),beb);
                }
            }
        }
    };


    // Algorithm 4.7: 1.5
    Handler<InitWriteRequest> initWriteRequestHandler = new Handler<InitWriteRequest>() {
        @Override
        public void handle(InitWriteRequest event) {
            LOG.info(self + " Starting write at time " + System.currentTimeMillis() + " Key-" + event.getKVEntry().getKey() + " Val-"+ event.getKVEntry().getValue());
            KVEntry kv = event.getKVEntry();
            Integer key = kv.getKey();
            if(!rids.containsKey(key)) {
                rids.put(key, 0);
            }
            // r++;
            rids.put(key, rids.get(key)+1);

            if(!wts.containsKey(key)) {
                wts.put(key, 0);
            }
            wts.put(key, wts.get(key) + 1);
            acks.put(key, 0);
            KVEntry entry = event.getKVEntry();
            entry.setTimestamp(wts.get(key));

            trigger(new BebBroadcastRequest(
                    new BebDeliver(self, new WriteRequest(entry, rids.get(key))),
                    neighbours),beb);
        }
    };

    // Algorithm 4.7: 1.6
    private void handleWriteRequest(TAddress source, WriteRequest request) {
        KVEntry kv = request.getKVEntry();
        Integer key = kv.getKey();
        if(withinBounds(key)) { //if we have the key
            KVEntry localKv = new KVEntry(0,0,0);
            if (localKv.getTimestamp() < kv.getTimestamp()) {
                localKv.setKey(key);
                localKv.setValue(kv.getValue());
                localKv.setTimestamp(kv.getTimestamp());
                store.put(key, localKv);
            }
            trigger(new AckWrite(self, source, key, request.getRid()), net);
        }
    }

    // Algorithm 4.7: 1.7
    Handler<AckWrite> handleAckWrite = new Handler<AckWrite>() {
        @Override
        public void handle(AckWrite event) {
            Integer key = event.getKey();
            if(event.getRid() == rids.get(key)) {
                LOG.info("got new ack from " + event.getSource());
                acks.put(key, acks.get(key) + 1);
                if (acks.get(key) >= 2) {
                    acks.put(key, 0);
                    if(readings.get(key) == null) {
                        readings.put(key, false);
                    }
                    if (readings.get(key) == true) { //TODO found bug, if someone does a get and then a subsequent put operation for the same key then the last ack from the read will count in on the put operation
                        readings.put(key , false);
                        trigger(new ReadReturn(event.getKey(), readvals.get(event.getKey())), riwm);
                        LOG.info(self + " Ending read at time " + System.currentTimeMillis() + " Key-" + event.getKey() + " Val-"+ readvals.get(event.getKey()));
                    } else {
                        trigger(new WriteReturn(event.getKey(), store.get(event.getKey()).getValue()), riwm);
                        LOG.info(self + " Ending write at time " + System.currentTimeMillis() + " Key-" + event.getKey() + " Val-"+ store.get(event.getKey()).getValue());
                    }
                }
            }
        }
    };


    private KVEntry getMaxTimestampPair(Integer key) {
        KVEntry max = new KVEntry(-1, -1, -1);
        ArrayList <KVEntry> readlist = readlists.get(key);
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
        private ArrayList <TAddress> neighbours;
        public Bound bounds;
        public Init(TAddress self, HashMap <Integer, KVEntry> store, ArrayList <TAddress> neighbours, Bound bounds) {
            this.self = self;
            this.store = store;
            this.neighbours = neighbours;
            this.bounds = bounds;
        }
    }

    private boolean withinBounds(Integer key) {
        if(key > bounds.getLowerBound() && key < bounds.getUpperBound()) {
            return true;
        }
        return false;
    }
}