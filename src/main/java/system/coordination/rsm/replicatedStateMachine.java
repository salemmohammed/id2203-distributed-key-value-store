package system.coordination.rsm;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import system.KVEntry;
import system.client.event.*;
import system.coordination.rsm.event.ExecuteCommand;
import system.coordination.rsm.event.ExecuteReponse;
import system.coordination.rsm.port.RSMPort;
import system.data.Bound;
import system.network.TAddress;

import java.util.HashMap;

public class ReplicatedStateMachine extends ComponentDefinition {
    private TAddress self;
    private Bound partition;
    private HashMap<Integer, KVEntry> store;

    Negative<RSMPort> rsm = provides(RSMPort.class);

    public ReplicatedStateMachine(Init init) {
        this.self = init.self;
        this.partition = init.partition;
        this.store = init.store;

        subscribe(executeHandler, rsm);
    }

    Handler<ExecuteCommand> executeHandler = new Handler<ExecuteCommand>() {
        @Override
        public void handle(ExecuteCommand event) {

            Command command = event.getCommand();

            ExecuteReponse response = null;

            if(command instanceof GETRequest) {
                response = executeGet(command);
            }

            if(command instanceof PUTRequest) {
                response = executePut(command);
            }

            if(command instanceof CASRequest) {
               response = executeCas(command);
            }

            trigger(response, rsm);
        }
    };



    private ExecuteReponse executeGet(Command command) {
        ExecuteReponse response = null;
        KVEntry kv = ((GETRequest) command).getKv();
        kv = store.get(kv.getKey());

        GETReply getReply = null;
        if(kv != null) {
            getReply = new GETReply(self, command.getSource(), kv);
            getReply.successful = true;
        } else {
            getReply = new GETReply(self, command.getSource(), null);
            getReply.successful = false;
        }

        response = new ExecuteReponse(getReply);

        return response;
    }

    private ExecuteReponse executePut(Command command) {
        ExecuteReponse response = null;
        KVEntry kv = ((PUTRequest) command).getKv();
        PUTReply putReply = null;
        if(withinPartitionSpace(kv.getKey())) {
            store.put(kv.getKey(), kv);
            putReply = new PUTReply(self, command.getSource(), kv);
            putReply.successful = true;
        } else {
            putReply = new PUTReply(self, command.getSource(), null);
            putReply.successful = false;
        }

        response = new ExecuteReponse(putReply);

        return response;
    }

    private ExecuteReponse executeCas(Command command) {
        ExecuteReponse response = null;
        CASReply casReply = null;
        KVEntry kv = ((CASRequest) command).getKVEntry();
        if(withinPartitionSpace(kv.getKey())) {
            int storeValue = store.get(kv.getKey()).getValue();
            if(storeValue == kv.getValue()) {
                kv = new KVEntry(kv.getKey(), ((CASRequest) command).getNewValue(), 0);
                store.put(kv.getKey(), kv);
                casReply = new CASReply(self, command.getSource(), kv, storeValue);
                casReply.successful = true;
            } else {
                casReply = new CASReply(self, command.getSource(), kv, -1);
                casReply.successful = false;
            }
        }

        response = new ExecuteReponse(casReply);

        return response;
    }

    private boolean withinPartitionSpace(Integer key) {
        if(key > partition.getLowerBound() && key < partition.getUpperBound()) {
            return true;
        }
        return false;
    }

    public static class Init extends se.sics.kompics.Init<ReplicatedStateMachine> {
        private TAddress self;
        private Bound partition;
        public HashMap<Integer, KVEntry> store;

        public Init(TAddress self, Bound partition, HashMap<Integer, KVEntry> store) {
            this.partition = partition;
            this.self = self;
            this.store = store;
        }
    }
}

