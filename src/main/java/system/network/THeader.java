package system.network;

import se.sics.kompics.network.Header;
import se.sics.kompics.network.Transport;
import system.network.TAddress;

import java.io.Serializable;

public class THeader implements Header<TAddress>, Serializable {

    public final TAddress src;
    public TAddress dst;
    public final Transport proto;

    public THeader(TAddress src, TAddress dst, Transport proto) {
        this.src = src;
        this.dst = dst;
        this.proto = proto;
    }

    @Override
    public TAddress getSource() {
        return src;
    }

    @Override
    public TAddress getDestination() {
        return dst;
    }

    @Override
    public Transport getProtocol() {
        return proto;
    }

}
