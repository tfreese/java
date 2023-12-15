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
class Port implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Port.class);

    private final InetAddress host;
    private final Map<Integer, Port> openPorts;
    private final int port;

    Port(final Map<Integer, Port> openPorts, final InetAddress host, final int port) {
        super();

        this.openPorts = Objects.requireNonNull(openPorts, "openPorts required");
        this.host = Objects.requireNonNull(host, "host required");
        this.port = port;
    }

    /**
     * Scannt den Port.
     */
    @Override
    public void run() {
        LOGGER.info("Scan Port {}:{}", this.host, this.port);

        try (Socket socket = new Socket(this.host, this.port)) {
            // Verbindungsaufbau reicht zum Testen.
            this.openPorts.put(socket.getPort(), this);
        }
        catch (IOException ex) {
            // NOOP
            // LOGGER.warn(ex.getMessage());
        }
    }

    @Override
    public String toString() {
        return String.format("%s:%d", this.host, this.port);
    }
}
