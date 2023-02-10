// Created: 19.12.2020
package de.freese.jconky.model;

/**
 * @author Thomas Freese
 */
public class NetworkInfo {
    private final long bytesReceived;

    private final long bytesTransmitted;
    private final String interfaceName;
    private final String ip;
    private final long timestamp;
    private double downloadPerSecond;
    private double uploadPerSecond;

    public NetworkInfo() {
        this("", "", 0L, 0L);
    }

    public NetworkInfo(final String interfaceName, final String ip, final long bytesReceived, final long bytesTransmitted) {
        super();

        this.interfaceName = interfaceName;
        this.ip = ip;
        this.bytesReceived = bytesReceived;
        this.bytesTransmitted = bytesTransmitted;
        this.timestamp = System.currentTimeMillis();
    }

    public void calculateUpAndDownload(final NetworkInfo previous) {
        double time = (getTimestamp() - previous.getTimestamp()) / 1000D;

        this.downloadPerSecond = (getBytesReceived() - previous.getBytesReceived()) / time;
        this.uploadPerSecond = (getBytesTransmitted() - previous.getBytesTransmitted()) / time;
    }

    public long getBytesReceived() {
        return this.bytesReceived;
    }

    public long getBytesTransmitted() {
        return this.bytesTransmitted;
    }

    public double getDownloadPerSecond() {
        return this.downloadPerSecond;
    }

    public String getInterfaceName() {
        return this.interfaceName;
    }

    public String getIp() {
        return this.ip;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public double getUploadPerSecond() {
        return this.uploadPerSecond;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
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
