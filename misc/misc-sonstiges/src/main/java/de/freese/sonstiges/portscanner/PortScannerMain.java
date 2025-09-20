package de.freese.sonstiges.portscanner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Port-Scanner.
 *
 * @author Thomas Freese
 */
public final class PortScannerMain {
    public static final int FIRST_PORT = 1;
    public static final int LAST_PORT = 65535;
    private static final Logger LOGGER = LoggerFactory.getLogger(PortScannerMain.class);

    /**
     * @param args -host ip -threads num -ports firstPort lastPort
     */
    static void main(final String[] args) {
        int firstPort = FIRST_PORT;
        int lastPort = LAST_PORT;
        int threads = 8;
        InetAddress host = null;

        final ListIterator<String> listIterator = List.of(args).listIterator();

        while (listIterator.hasNext()) {
            final String arg = listIterator.next();

            try {
                if ("-host".equalsIgnoreCase(arg)) {
                    host = InetAddress.getByName(listIterator.next());
                }
                else if ("-threads".equalsIgnoreCase(arg)) {
                    threads = Integer.parseInt(listIterator.next());
                }
                else if ("-ports".equalsIgnoreCase(arg)) {
                    firstPort = Integer.parseInt(listIterator.next());
                    lastPort = Integer.parseInt(listIterator.next());
                }
                else if ("-?".equalsIgnoreCase(arg) || "-h".equalsIgnoreCase(arg) || "-help".equalsIgnoreCase(arg)) {
                    LOGGER.error("Syntax: java [-jar] PortScannerMain[.class/.jar] -host ip -threads num -ports firstPort lastPort");
                    System.exit(0);
                }
                else {
                    badArg("Unknown command-line argument: " + arg);
                }
            }
            catch (NoSuchElementException _) {
                badArg("missing item after " + arg);
            }
            catch (NumberFormatException _) {
                badArg("bad number format for " + arg + ": " + listIterator.previous());
            }
            catch (UnknownHostException _) {
                badArg(listIterator.previous() + " is not a valid host name.");
            }
        }

        if (host == null) {
            badArg("No host specified");
        }

        if (LOGGER.isInfoEnabled()) {
            final String message = String.format("host: %s, ports: %d-%d, threads: %d", host, firstPort, lastPort, threads);
            LOGGER.info(message);
        }

        try (ExecutorService executor = Executors.newFixedThreadPool(threads)) {
            // new ThreadPoolExecutor(1, threads, 5L, TimeUnit.SECONDS,
            // new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());

            final Map<Integer, Port> openPorts = Collections.synchronizedMap(new TreeMap<>());

            for (int port = firstPort; port < (lastPort + 1); port++) {
                executor.execute(new Port(openPorts, host, port));
            }

            executor.execute(() -> {
                LOGGER.info("ThreadPool (Active): {}", ((ThreadPoolExecutor) executor).getActiveCount());
                LOGGER.info("ThreadPool (Tasks): {}", ((ThreadPoolExecutor) executor).getTaskCount());
                LOGGER.info("ThreadPool (Queue): {}", ((ThreadPoolExecutor) executor).getQueue().size());

                executor.shutdown();

                LOGGER.info("");

                for (Port port : openPorts.values()) {
                    LOGGER.info("Open Port on {}", port);
                }

                openPorts.clear();
            });
        }
    }

    private static void badArg(final String param) {
        LOGGER.error(param);

        System.exit(1);
    }

    private PortScannerMain() {
        super();
    }
}
