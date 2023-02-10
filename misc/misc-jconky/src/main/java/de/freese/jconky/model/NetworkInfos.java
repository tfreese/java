// Created: 19.12.2020
package de.freese.jconky.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class NetworkInfos {
    private static final NetworkInfo DEFAULT_NETWORK_INFO = new NetworkInfo();

    private final Map<String, NetworkInfo> interfaces;

    private final NetworkProtocolInfo protocolInfo;

    public NetworkInfos() {
        this(new HashMap<>(), new NetworkProtocolInfo());
    }

    public NetworkInfos(final Map<String, NetworkInfo> interfaces, final NetworkProtocolInfo protocolInfo) {
        super();

        this.interfaces = interfaces;
        this.protocolInfo = protocolInfo;
    }

    public void calculateUpAndDownload(final NetworkInfos previous) {
        this.interfaces.keySet().forEach(name -> {
            NetworkInfo niPrevious = previous.getByName(name);
            NetworkInfo ni = getByName(name);

            // Den ersten Durchlauf ignorieren, sonst stimmen die Zahlen nicht.
            if (niPrevious.getBytesReceived() > 0L) {
                ni.calculateUpAndDownload(niPrevious);
            }
        });
    }

    public NetworkInfo getByIp(final String ip) {
        return this.interfaces.values().stream().filter(ni -> ni.getIp().equals(ip)).findFirst().orElse(DEFAULT_NETWORK_INFO);
    }

    public NetworkInfo getByName(final String interfaceName) {
        return this.interfaces.computeIfAbsent(interfaceName, key -> DEFAULT_NETWORK_INFO);
    }

    public NetworkProtocolInfo getProtocolInfo() {
        return this.protocolInfo;
    }

    public int size() {
        return this.interfaces.size();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("size=").append(size());
        builder.append("]");

        return builder.toString();
    }
}
