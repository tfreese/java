package de.freese.sonstiges.portscanner;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;
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
    public static void main(final String[] args) {
        int firstPort = FIRST_PORT;
        int lastPort = LAST_PORT;
        int threads = 8;
        InetAddress host = null;

        int nextArg = 0;

        while (nextArg < args.length) {
            final String arg = args[nextArg++];

            try {
                if ("-threads".equalsIgnoreCase(arg)) {
                    threads = Integer.parseInt(args[nextArg++]);
                }
                else if ("-host".equalsIgnoreCase(arg)) {
                    host = InetAddress.getByName(args[nextArg++]);
                }
                else if ("-ports".equalsIgnoreCase(arg)) {
                    firstPort = Integer.parseInt(args[nextArg++]);
                    lastPort = Integer.parseInt(args[nextArg++]);
                }
                else if ("-?".equalsIgnoreCase(arg) || "-h".equalsIgnoreCase(arg) || "-help".equalsIgnoreCase(arg)) {
                    LOGGER.error("Syntax: java [-jar] PortScannerMain[.class/.jar] -host ip -threads num -ports firstPort lastPort");
                    System.exit(0);
                }
                else {
                    badArg("Unknown command-line argument: " + arg);
                }
            }
            catch (ArrayIndexOutOfBoundsException ex) {
                badArg("missing item after " + arg);
            }
            catch (NumberFormatException ex) {
                badArg("bad number format for " + arg + ": " + args[nextArg - 1]);
            }
            catch (UnknownHostException ex) {
                badArg(args[nextArg - 1] + " is not a valid host name.");
            }
        }

        if (host == null) {
            badArg("No host specified");
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(String.format("host: %s, ports: %d-%d, threads: %d", host, firstPort, lastPort, threads));
        }

        final ExecutorService executor = Executors.newFixedThreadPool(threads);
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

    private static void badArg(final String param) {
        LOGGER.error(param);

        System.exit(1);
    }

    private PortScannerMain() {
        super();
    }
}
