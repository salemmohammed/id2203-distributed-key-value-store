package system.riwm.event;

import se.sics.kompics.KompicsEvent;

public class ReadRequest implements KompicsEvent {

    private Integer key ;
    private int rId;


    public Integer getKey() {
        return key;
    }

    public int getrId() {
        return rId;
    }

    public ReadRequest(Integer key,int rId) {
        this.key = key;
        this.rId = rId;
    }
}
