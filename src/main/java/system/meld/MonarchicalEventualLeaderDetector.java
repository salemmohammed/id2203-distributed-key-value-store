package system.meld;

import se.sics.kompics.*;
import system.meld.event.CheckLeader;
import system.meld.event.Trust;
import system.epfd.event.Restore;
import system.epfd.event.Suspect;
import system.network.TAddress;
import system.epfd.port.FDPort;

import java.util.ArrayList;

public class MonarchicalEventualLeaderDetector extends ComponentDefinition {

    private ArrayList <TAddress> suspected;
    private TAddress leader;
    private TAddress self;
    private Positive<FDPort> epfd = requires(FDPort.class);
    Negative<MELDPort> meld = provides(MELDPort.class);
    private ArrayList <TAddress> replicationGroup;

    public MonarchicalEventualLeaderDetector(Init init) {
        this.self = init.self;
        this.replicationGroup = init.replicationGroup;
        suspected = new ArrayList<>();

        leader = maxRank(getAliveNotSuspectedNodes());
        trigger(new Trust(leader), meld);

        subscribe(suspectHandler, epfd);
        subscribe(restoreHandler, epfd);
        subscribe(checkLeaderHandler, meld);
    }

    Handler<Suspect> suspectHandler = new Handler<Suspect>() {
        @Override
        public void handle(Suspect suspect) {
            suspected.add(suspect.getNode());
            if(leader.equals(suspect.getNode())) {
                leader = maxRank(getAliveNotSuspectedNodes());
                trigger(new Trust(leader), meld);
            }
        }
    };

    Handler<Restore> restoreHandler = new Handler<Restore>() {
        @Override
        public void handle(Restore restore) {
            suspected.remove(restore.getNode());
            TAddress bestFitLeader = maxRank(getAliveNotSuspectedNodes());
            if(!leader.equals(bestFitLeader)) {
                leader = bestFitLeader;
                trigger(new Trust(leader), meld);
            }

        }
    };

    Handler<CheckLeader> checkLeaderHandler = new Handler<CheckLeader>() {
        @Override
        public void handle(CheckLeader checkLeader) {
            leader = maxRank(getAliveNotSuspectedNodes());
            trigger(new Trust(leader), meld);
        }
    };

    private ArrayList<TAddress> getAliveNotSuspectedNodes() {
        ArrayList<TAddress> aliveNodes = new ArrayList<>();
        for(TAddress node : replicationGroup) {
            if(!suspected.contains(node)) {
                aliveNodes.add(node);
            }
        }
        return aliveNodes;
    }

    private TAddress maxRank(ArrayList <TAddress> nodes) {
        TAddress lowestIpNode = self;
        for(TAddress node : nodes) {
            if(lowestIpNode == null || lowestIpNode.getId() > node.getId() || lowestIpNode.getPort() > node.getPort()) {
                lowestIpNode = node;
            }
        }
        return lowestIpNode;
    };


    public static class Init extends se.sics.kompics.Init<MonarchicalEventualLeaderDetector> {

        private ArrayList<TAddress> replicationGroup;
        private TAddress self;

        public Init(TAddress self, ArrayList<TAddress> replicationGroup) {
            this.self = self;
            this.replicationGroup = replicationGroup;
        }
    }
















}
