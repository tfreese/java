package de.freese.jconky.model;

/**
 * @author Thomas Freese
 * @since 20.12.2020
 */
public class NetworkProtocolInfo {
    private long icmpIn;
    private long icmpOut;
    private long ipIn;
    private long ipOut;
    private long tcpIn;
    private long tcpOut;
    private long udpIn;
    private long udpOut;

    public NetworkProtocolInfo() {
        super();
    }

    public NetworkProtocolInfo(final long icmpIn, final long icmpOut, final long ipIn, final long ipOut, final long tcpIn, final long tcpOut, final long udpIn, final long udpOut) {
        super();
        this.icmpIn = icmpIn;
        this.icmpOut = icmpOut;
        this.ipIn = ipIn;
        this.ipOut = ipOut;
        this.tcpIn = tcpIn;
        this.tcpOut = tcpOut;
        this.udpIn = udpIn;
        this.udpOut = udpOut;
    }

    public long getIcmpIn() {
        return icmpIn;
    }

    public long getIcmpOut() {
        return icmpOut;
    }

    public long getIpIn() {
        return ipIn;
    }

    public long getIpOut() {
        return ipOut;
    }

    public long getTcpIn() {
        return tcpIn;
    }

    public long getTcpOut() {
        return tcpOut;
    }

    public long getUdpIn() {
        return udpIn;
    }

    public long getUdpOut() {
        return udpOut;
    }
}
