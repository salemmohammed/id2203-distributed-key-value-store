package system.coordination.rsm;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import system.data.KVEntry;
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
            CommandMessage commandMessage = event.getCommandMessage();

            ExecuteReponse response = null;

            if(commandMessage instanceof GETRequest) {
                response = executeGet(commandMessage);
            }

            if(commandMessage instanceof PUTRequest) {
                response = executePut(commandMessage);
            }

            if(commandMessage instanceof CASRequest) {
                response = executeCas(commandMessage);
            }
            trigger(response, rsm);
        }
    };



    private ExecuteReponse executeGet(CommandMessage commandMessage) {
        ExecuteReponse response = null;
        KVEntry kv = ((GETRequest) commandMessage).getKv();
        kv = store.get(kv.getKey());

        GETReply getReply = null;
        if(kv != null) {
            getReply = new GETReply(self, commandMessage.getSource(), kv);
            getReply.successful = true;
        } else {
            getReply = new GETReply(self, commandMessage.getSource(), null);
            getReply.successful = false;
        }


        response = new ExecuteReponse(getReply);

        return response;
    }

    private ExecuteReponse executePut(CommandMessage commandMessage) {
        ExecuteReponse response = null;
        KVEntry kv = ((PUTRequest) commandMessage).getKv();
        PUTReply putReply = null;
        if(withinPartitionSpace(kv.getKey())) {
            store.put(kv.getKey(), kv);
            putReply = new PUTReply(self, commandMessage.getSource(), kv);
            putReply.successful = true;
        } else {
            putReply = new PUTReply(self, commandMessage.getSource(), null);
            putReply.successful = false;
        }
        response = new ExecuteReponse(putReply);

        return response;
    }

    private ExecuteReponse executeCas(CommandMessage commandMessage) {
        ExecuteReponse response = null;
        CASReply casReply = null;
        KVEntry kv = ((CASRequest) commandMessage).getKVEntry();
        if(withinPartitionSpace(kv.getKey())) {
            int storeValue = store.get(kv.getKey()).getValue();
            if(storeValue == kv.getValue()) {
                kv = new KVEntry(kv.getKey(), ((CASRequest) commandMessage).getNewValue(), 0);
                store.put(kv.getKey(), kv);
                casReply = new CASReply(self, commandMessage.getSource(), kv, storeValue);
                casReply.successful = true;
            } else {
                casReply = new CASReply(self, commandMessage.getSource(), kv, -1);
                casReply.successful = false;
            }
        }
        else {
            casReply = new CASReply(self, commandMessage.getSource(), kv, -1);
            casReply.successful = false;
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

