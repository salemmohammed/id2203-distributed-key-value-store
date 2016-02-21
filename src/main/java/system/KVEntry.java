package system;

import java.io.Serializable;

/**
 * Created by Robin on 2016-02-14.
 */
public class KVEntry implements Serializable {

    private int key;
    private int value;
    private int timestamp;

    public KVEntry(int key, int value, int timestamp) {
        this.key = key;
        this.timestamp = timestamp;
        this.value = value;

    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
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

    public String toString() {
        return "Key: " +key + " Value: " + value + " Timestamp: " +timestamp;
    }
}
