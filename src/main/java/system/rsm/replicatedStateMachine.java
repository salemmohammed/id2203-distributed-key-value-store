package system.rsm;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import system.data.KVEntry;
import client.event.*;
import system.rsm.event.ExecuteCommand;
import system.rsm.event.ExecuteReponse;
import system.rsm.port.RSMPort;
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
            System.out.println(self + " RSM Response: " + response.getCommandMessage().toString() + " to " + response.getCommandMessage().getDestination());

            trigger(response, rsm);
        }
    };



    private ExecuteReponse executeGet(CommandMessage commandMessage) {
        ExecuteReponse response = null;
        KVEntry kv = ((GETRequest) commandMessage).getKVEntry();
        kv = store.get(kv.getKey());
        System.out.println("\n" + self + " RSM Executing: " + ((GETRequest) commandMessage).toString());
        GETReply getReply = null;
        if(kv != null) {
            getReply = new GETReply(self, commandMessage.getSource(), kv, commandMessage.getPid(), commandMessage.getSeqNum());
            getReply.successful = true;
        } else {
            getReply = new GETReply(self, commandMessage.getSource(), null, commandMessage.getPid(), commandMessage.getSeqNum());
            getReply.successful = false;
        }


        response = new ExecuteReponse(getReply);

        return response;
    }

    private ExecuteReponse executePut(CommandMessage commandMessage) {
        ExecuteReponse response = null;
        KVEntry kv = ((PUTRequest) commandMessage).getKVEntry();
        PUTReply putReply = null;
        if(withinPartitionSpace(kv.getKey())) {
            store.put(kv.getKey(), kv);
            System.out.println("\n" + self + " RSM Executing: " + ((PUTRequest) commandMessage).toString());
            putReply = new PUTReply(self, commandMessage.getSource(), kv, commandMessage.getPid(), commandMessage.getSeqNum());
            putReply.successful = true;
        } else {
            putReply = new PUTReply(self, commandMessage.getSource(), null, commandMessage.getPid(), commandMessage.getSeqNum());
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
                System.out.println("\n" + self + " RSM Executing: " + ((CASRequest) commandMessage).toString());
                store.put(kv.getKey(), kv);
                casReply = new CASReply(self, commandMessage.getSource(), kv, storeValue, commandMessage.getPid(), commandMessage.getSeqNum());
                casReply.successful = true;
            } else {
                casReply = new CASReply(self, commandMessage.getSource(), kv, -1, commandMessage.getPid(), commandMessage.getSeqNum());
                casReply.successful = false;
            }
        }
        else {
            casReply = new CASReply(self, commandMessage.getSource(), kv, -1, commandMessage.getPid(), commandMessage.getSeqNum());
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

