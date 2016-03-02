package system.data;

import system.network.TAddress;

import java.util.ArrayList;

/**
 * Created by Robin on 2016-03-01.
 */
public class ReplicationGroup {
    private Bound bound;
    private ArrayList<TAddress> nodes;

    public ReplicationGroup(Bound bound, ArrayList<TAddress> nodes) {
        this.bound = bound;
        this.nodes = nodes;
    }

    public Bound getBound() {
        return bound;
    }

    public void setBound(Bound bound) {
        this.bound = bound;
    }

    public ArrayList<TAddress> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<TAddress> nodes) {
        this.nodes = nodes;
    }

    public boolean withinPartitionSpace(Integer key) {
        if(key > bound.getLowerBound() && key < bound.getUpperBound()) {
            return true;
        }
        return false;
    }
}
