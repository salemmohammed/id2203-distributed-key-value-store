package system.coordination.paxos.event;

import se.sics.kompics.KompicsEvent;
import system.client.event.CASRequest;
import system.client.event.CommandMessage;
import system.client.event.GETRequest;
import system.client.event.PUTRequest;

import java.util.ArrayList;

public class AscDecide implements KompicsEvent {
    private Object value;


    private ArrayList<Object> av;

    public AscDecide(Object value, ArrayList<Object> av) {
        this.setValue(value);
        this.av = av;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ArrayList<Object> getAv() {
        return av;
    }

    public void setAv(ArrayList<Object> av) {
        this.av = av;
    }

}
