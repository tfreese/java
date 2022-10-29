// Created: 19.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class NetworkInfo
{
    /**
     *
     */
    private final long bytesReceived;
    /**
     *
     */
    private final long bytesTransmitted;
    /**
     *
     */
    private double downloadPerSecond;
    /**
     *
     */
    private final String interfaceName;
    /**
     *
     */
    private final String ip;
    /**
     *
     */
    private final long timestamp;
    /**
     *
     */
    private double uploadPerSecond;

    /**
     * Erstellt ein neues {@link NetworkInfo} Object.
     */
    public NetworkInfo()
    {
        this("", "", 0L, 0L);
    }

    /**
     * Erstellt ein neues {@link NetworkInfo} Object.
     *
     * @param interfaceName String
     * @param ip String
     * @param bytesReceived long
     * @param bytesTransmitted long
     */
    public NetworkInfo(final String interfaceName, final String ip, final long bytesReceived, final long bytesTransmitted)
    {
        super();

        this.interfaceName = interfaceName;
        this.ip = ip;
        this.bytesReceived = bytesReceived;
        this.bytesTransmitted = bytesTransmitted;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * @param previous {@link NetworkInfo}
     */
    public void calculateUpAndDownload(final NetworkInfo previous)
    {
        double time = (getTimestamp() - previous.getTimestamp()) / 1000D;

        this.downloadPerSecond = (getBytesReceived() - previous.getBytesReceived()) / time;
        this.uploadPerSecond = (getBytesTransmitted() - previous.getBytesTransmitted()) / time;
    }

    /**
     * @return long
     */
    public long getBytesReceived()
    {
        return this.bytesReceived;
    }

    /**
     * @return long
     */
    public long getBytesTransmitted()
    {
        return this.bytesTransmitted;
    }

    /**
     * @return double
     */
    public double getDownloadPerSecond()
    {
        return this.downloadPerSecond;
    }

    /**
     * @return String
     */
    public String getInterfaceName()
    {
        return this.interfaceName;
    }

    /**
     * @return String
     */
    public String getIp()
    {
        return this.ip;
    }

    /**
     * @return long
     */
    public long getTimestamp()
    {
        return this.timestamp;
    }

    /**
     * @return double
     */
    public double getUploadPerSecond()
    {
        return this.uploadPerSecond;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("interfaceName=").append(this.interfaceName);
        builder.append(", ip=").append(this.ip);
        builder.append(", bytesTransmitted=").append(this.bytesTransmitted);
        builder.append(", bytesReceived=").append(this.bytesReceived);
        builder.append("]");

        return builder.toString();
    }
}
