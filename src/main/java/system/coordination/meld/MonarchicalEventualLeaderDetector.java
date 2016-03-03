package system.coordination.meld;

import se.sics.kompics.*;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timer;
import system.coordination.meld.event.CheckLeader;
import system.coordination.meld.event.Trust;
import system.epfd.event.Restore;
import system.epfd.event.Suspect;
import system.network.TAddress;
import system.port.epfd.FDPort;

import java.util.ArrayList;

/**
 * Created by Robin on 2016-02-27.
 */
public class MonarchicalEventualLeaderDetector extends ComponentDefinition {

    private ArrayList <TAddress> suspected;
    private TAddress leader;
    private Positive<FDPort> epfd = requires(FDPort.class);
    Negative<MELDPort> meld = provides(MELDPort.class);
    private ArrayList <TAddress> replicationGroup;

    public MonarchicalEventualLeaderDetector(Init init) {
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
        TAddress lowestIpNode = null;
        for(TAddress node : nodes) {
            if(lowestIpNode == null || lowestIpNode.getId() > node.getId()) {
                lowestIpNode = node;
            }
        }
        return lowestIpNode;
    };


    public static class Init extends se.sics.kompics.Init<MonarchicalEventualLeaderDetector> {

        public final ArrayList<TAddress> replicationGroup;

        public Init(ArrayList<TAddress> replicationGroup) {
            this.replicationGroup = replicationGroup;
        }
    }
















}
