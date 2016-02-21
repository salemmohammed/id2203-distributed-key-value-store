package system.coordination.event;

import se.sics.kompics.KompicsEvent;

/**
 * Created by Robin on 2016-02-19.
 */
public class WriteReturn implements KompicsEvent
{

    private Integer key;
    private Integer value;

    public Integer getKey() {
        return key;
    }
    public Integer getValue() { return value; }

    public WriteReturn(Integer key, Integer value) {
        this.key = key;
        this.value = value;
    }
}