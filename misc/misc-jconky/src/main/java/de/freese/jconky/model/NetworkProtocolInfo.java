// Created: 20.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class NetworkProtocolInfo
{
    /**
     *
     */
    private long icmpIn;
    /**
     *
     */
    private long icmpOut;
    /**
     *
     */
    private long ipIn;
    /**
     *
     */
    private long ipOut;
    /**
     *
     */
    private int tcpConnections;
    /**
     *
     */
    private long tcpIn;
    /**
     *
     */
    private long tcpOut;
    /**
     *
     */
    private long udpIn;
    /**
     *
     */
    private long udpOut;

    /**
     * Erstellt ein neues {@link NetworkProtocolInfo} Object.
     */
    public NetworkProtocolInfo()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link NetworkProtocolInfo} Object.
     *
     * @param icmpIn long
     * @param icmpOut long
     * @param ipIn long
     * @param ipOut long
     * @param tcpConnections int
     * @param tcpIn long
     * @param tcpOut long
     * @param udpIn long
     * @param udpOut long
     */
    public NetworkProtocolInfo(final long icmpIn, final long icmpOut, final long ipIn, final long ipOut, final int tcpConnections, final long tcpIn,
            final long tcpOut, final long udpIn, final long udpOut)
    {
        super();
        this.icmpIn = icmpIn;
        this.icmpOut = icmpOut;
        this.ipIn = ipIn;
        this.ipOut = ipOut;
        this.tcpConnections = tcpConnections;
        this.tcpIn = tcpIn;
        this.tcpOut = tcpOut;
        this.udpIn = udpIn;
        this.udpOut = udpOut;
    }

    /**
     * @return long
     */
    public long getIcmpIn()
    {
        return this.icmpIn;
    }

    /**
     * @return long
     */
    public long getIcmpOut()
    {
        return this.icmpOut;
    }

    /**
     * @return long
     */
    public long getIpIn()
    {
        return this.ipIn;
    }

    /**
     * @return long
     */
    public long getIpOut()
    {
        return this.ipOut;
    }

    /**
     * @return int
     */
    public int getTcpConnections()
    {
        return this.tcpConnections;
    }

    /**
     * @return long
     */
    public long getTcpIn()
    {
        return this.tcpIn;
    }

    /**
     * @return long
     */
    public long getTcpOut()
    {
        return this.tcpOut;
    }

    /**
     * @return long
     */
    public long getUdpIn()
    {
        return this.udpIn;
    }

    /**
     * @return long
     */
    public long getUdpOut()
    {
        return this.udpOut;
    }
}
