package system.riwm.event;

import se.sics.kompics.KompicsEvent;
import system.network.TAddress;

import java.util.ArrayList;

public class InitReadRequest implements KompicsEvent {

    private Integer key ;
    private ArrayList<TAddress> neighbours;

    public ArrayList<TAddress> getNeighbours() {
        return neighbours;
    }


    public Integer getKey() {
        return key;
    }

    public InitReadRequest(Integer key, ArrayList<TAddress> neighbours) {
        this.key = key;
        this.neighbours = neighbours;
    }
}
