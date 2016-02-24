package system.coordination.riwm.event;

import se.sics.kompics.KompicsEvent;

/**
 * Created by Robin on 2016-02-19.
 */
public class ReadReturn   implements KompicsEvent {

    private Integer key;
    private Integer value;


    public Integer getKey() {
        return key;
    }
    public Integer getValue() { return value; }

    public ReadReturn(Integer key, Integer value) {
        this.key = key;
        this.value = value;
    }
}