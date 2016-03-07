package system.riwm.event;

import se.sics.kompics.KompicsEvent;

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