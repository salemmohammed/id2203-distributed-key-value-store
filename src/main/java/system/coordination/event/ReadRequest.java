package system.coordination.event;

import se.sics.kompics.KompicsEvent;
import system.network.TAddress;

import java.util.ArrayList;

/**
 * Created by marcus on 17/02/16.
 */
public class ReadRequest implements KompicsEvent {

    private Integer key ;
    private ArrayList<TAddress> neighbours;

    public ArrayList<TAddress> getNeighbours() {
        return neighbours;
    }


    public Integer getKey() {
        return key;
    }

    public ReadRequest(Integer key, ArrayList<TAddress> neighbours) {
        this.key = key;
        this.neighbours = neighbours;
    }
}
