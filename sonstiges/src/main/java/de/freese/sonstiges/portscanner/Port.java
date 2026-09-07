package de.freese.sonstiges.portscanner;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scannt einen Port eines Hosts.
 *
 * @author Thomas Freese
 */
final class Port implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Port.class);

    private final InetAddress host;
    private final Map<Integer, Port> openPorts;
    private final int portNumber;

    Port(final Map<Integer, Port> openPorts, final InetAddress host, final int portNumber) {
        super();

        this.openPorts = Objects.requireNonNull(openPorts, "openPorts required");
        this.host = Objects.requireNonNull(host, "host required");
        this.portNumber = portNumber;
    }

    /**
     * Scannt den Port.
     */
    @Override
    public void run() {
        LOGGER.info("Scan Port {}:{}", host, portNumber);

        try (Socket socket = new Socket(host, portNumber)) {
            // Verbindungsaufbau reicht zum Testen.
            openPorts.put(socket.getPort(), this);
        }
        catch (IOException _) {
            // NOOP
            // LOGGER.warn(ex.getMessage());
        }
    }

    @Override
    public String toString() {
        return String.format("%s:%d", host, portNumber);
    }
}
