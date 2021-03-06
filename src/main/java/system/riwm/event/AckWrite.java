package system.riwm.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

import java.io.Serializable;

public class AckWrite extends TMessage implements Serializable{
    private Integer key;
    private Integer rid;

    public AckWrite(TAddress src, TAddress dst, Integer key, Integer rid) {
        super(src, dst, Transport.TCP);
        this.key = key;
        this.setRid(rid);

    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }
}
