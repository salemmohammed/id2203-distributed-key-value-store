package system.riwm.event;

import se.sics.kompics.KompicsEvent;

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