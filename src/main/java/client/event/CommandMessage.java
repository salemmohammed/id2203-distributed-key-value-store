package client.event;

import se.sics.kompics.network.Transport;
import system.network.TAddress;
import system.network.TMessage;

import java.io.Serializable;

public class CommandMessage extends TMessage implements Serializable{

    private int pid;
    private int seqNum;

    public CommandMessage(TAddress source, TAddress destination, int pid, int seqNum) {
        super(source, destination, Transport.TCP);
        this.setPid(pid);
        this.setSeqNum(seqNum);
    }

    public CommandMessage(TAddress source, TAddress destination) {
        super(source, destination, Transport.TCP);
    }

    public void setDestination(TAddress destination) {
        super.getHeader().dst = destination;
    }

    public boolean equals(Object obj) {
        CommandMessage command = (CommandMessage)obj;
        if(this.getPid() == command.getPid() && this.getSeqNum() == command.getSeqNum()) {
            return true;
        }
        return false;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

}
