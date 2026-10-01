package de.freese.jconky.sensor.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.freese.jconky.sensor.AbstractSensor;

/**
 * @author Thomas Freese
 * @since 01.10.26
 */
public final class NetworkSensor extends AbstractSensor {
    private NetworkInfos networkInfos;

    public NetworkInfos getNetworkInfos() {
        return networkInfos;
    }

    @Override
    public void update() {
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", "ip -s -4 addr");
        final List<String> lines = readContent(processBuilder);

        // Separate Interfaces.
        final Map<Integer, List<String>> map = new HashMap<>();
        int n = 0;

        for (final String line : lines) {
            if (line.contains(": <")) {
                n++;
            }

            if (n > 0) {
                map.computeIfAbsent(n, key -> new ArrayList<>()).add(line);
            }
        }

        final Map<String, NetworkInfo> networkInfoMap = new HashMap<>();

        for (final List<String> ifLines : map.values()) {
            String interfaceName = null;
            String ip = null;
            long bytesReceived = 0L;
            long bytesTransmitted = 0L;

            do {
                String line = ifLines.removeFirst().strip();

                if (line.contains(": <")) {
                    // Interface Name
                    final int index = line.indexOf(':');
                    interfaceName = line.substring(index + 1, line.indexOf(":", index + 1)).strip();
                }
                else if (line.startsWith("inet ")) {
                    // IP
                    final String[] splits = SPACE_PATTERN.split(line);
                    ip = splits[1];

                    if (ip.contains("/")) {
                        ip = ip.substring(0, ip.indexOf("/"));
                    }
                }
                else if (line.startsWith("RX:")) {
                    // Bytes Received
                    line = ifLines.removeFirst().strip();
                    final String[] splits = SPACE_PATTERN.split(line);
                    bytesReceived = Long.parseLong(splits[0]);
                }
                else if (line.startsWith("TX:")) {
                    // Bytes Transmitted
                    line = ifLines.removeFirst().strip();
                    final String[] splits = SPACE_PATTERN.split(line);
                    bytesTransmitted = Long.parseLong(splits[0]);
                }
            }
            while (!ifLines.isEmpty());

            if (interfaceName != null && !interfaceName.isEmpty()) {
                final NetworkInfo networkInfo = new NetworkInfo(interfaceName, ip, bytesReceived, bytesTransmitted);
                networkInfoMap.put(interfaceName, networkInfo);
            }
        }

        // Protokoll Infos.
        // nstat -a
        processBuilder = new ProcessBuilder("/bin/sh", "-c", "nstat -a");
        final List<String> nStatLines = readContent(processBuilder);

        long icmpIn = 0L;
        long icmpOut = 0L;
        long ipIn = 0L;
        long ipOut = 0L;
        long tcpIn = 0L;
        long tcpOut = 0L;
        long udpIn = 0L;
        long udpOut = 0L;

        for (String line : nStatLines) {
            line = line.strip();

            if (line.contains("TcpInSegs")) {
                final String[] splits = SPACE_PATTERN.split(line);
                tcpIn = Long.parseLong(splits[1]);
            }
            else if (line.contains("TcpOutSegs")) {
                final String[] splits = SPACE_PATTERN.split(line);
                tcpOut = Long.parseLong(splits[1]);
            }
            else if (line.contains("UdpInDatagrams")) {
                final String[] splits = SPACE_PATTERN.split(line);
                udpIn = Long.parseLong(splits[1]);
            }
            else if (line.contains("UdpOutDatagrams")) {
                final String[] splits = SPACE_PATTERN.split(line);
                udpOut = Long.parseLong(splits[1]);
            }
            else if (line.contains("IpInReceives")) {
                final String[] splits = SPACE_PATTERN.split(line);
                ipIn = Long.parseLong(splits[1]);
            }
            else if (line.contains("IpOutRequests")) {
                final String[] splits = SPACE_PATTERN.split(line);
                ipOut = Long.parseLong(splits[1]);
            }
            else if (line.contains("Icmp6InMsgs")) {
                // IcmpInEchos
                final String[] splits = SPACE_PATTERN.split(line);
                icmpIn = Long.parseLong(splits[1]);
            }
            else if (line.contains("Icmp6OutMsgs")) {
                // IcmpOutEchoReps
                final String[] splits = SPACE_PATTERN.split(line);
                icmpOut = Long.parseLong(splits[1]);
            }
        }

        final NetworkProtocolInfo protocolInfo = new NetworkProtocolInfo(icmpIn, icmpOut, ipIn, ipOut, tcpIn, tcpOut, udpIn, udpOut);

        final NetworkInfos previous = networkInfos;

        networkInfos = new NetworkInfos(networkInfoMap, protocolInfo);
        networkInfos.calculateUpAndDownload(previous);
    }
}
