package system.data;

import java.io.Serializable;

public class KVEntry implements Serializable {

    private int key;
    private int value;
    private int timestamp;

    public KVEntry(int key, int value, int timestamp) {
        this.key = key;
        this.timestamp = timestamp;
        this.value = value;

    }

    public KVEntry(int key) {
        this.key = key;
        this.timestamp = 0;
        this.value = 0;
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
