package system.network;

import se.sics.kompics.network.Msg;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

public abstract class TMessage implements Msg<TAddress, THeader>, Serializable {
    
    public final THeader header;
    
    public TMessage(TAddress src, TAddress dst, Transport protocol) {
        this.header = new THeader(src, dst, protocol);

    }
    
    public TMessage(THeader header) {
        this.header = header;
    }


    @Override
    public THeader getHeader() {
        return this.header;
    }

    @Override
    public TAddress getSource() {
        return this.header.src;
    }

    @Override
    public TAddress getDestination() {
        return this.header.dst;
    }

    @Override
    public Transport getProtocol() {
        return this.header.proto;
    }
    
}
