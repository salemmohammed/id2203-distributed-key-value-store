package system.client.event;

import java.io.Serializable;

/**
 * Created by Robin on 2016-02-14.
 */
public class ValueTimestampPair implements Serializable {

    private int value;
    private int timestamp;

    public ValueTimestampPair(int timestamp, int value) {
        this.timestamp = timestamp;
        this.value = value;

    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
