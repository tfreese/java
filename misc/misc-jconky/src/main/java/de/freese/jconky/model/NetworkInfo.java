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
        final double time = (getTimestamp() - previous.getTimestamp()) / 1000D;

        this.downloadPerSecond = (getBytesReceived() - previous.getBytesReceived()) / time;
        this.uploadPerSecond = (getBytesTransmitted() - previous.getBytesTransmitted()) / time;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }

    public long getBytesTransmitted() {
        return bytesTransmitted;
    }

    public double getDownloadPerSecond() {
        return downloadPerSecond;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getIp() {
        return ip;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getUploadPerSecond() {
        return uploadPerSecond;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("interfaceName=").append(interfaceName);
        builder.append(", ip=").append(ip);
        builder.append(", bytesTransmitted=").append(bytesTransmitted);
        builder.append(", bytesReceived=").append(bytesReceived);
        builder.append("]");

        return builder.toString();
    }
}
