// Created: 26.08.2020
package de.freese.sonstiges.disruptor.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="https://stackoverflow.com/questions/55827411/how-to-make-a-java-nio-non-blocking-io-based-tcp-server-using-disruptor">
 * how-to-make-a-java-nio-non-blocking-io-based-tcp-server-using-disruptor
 * </a>
 *
 * @author Thomas Freese
 */
@SuppressWarnings({"java:S2095", "java:S2245"})
public final class HttpEventMain {
    /**
     * -2 damit noch Platz für den CleaningEventHandler und sonstige Ressourcen bleibt.
     */
    public static final int THREAD_COUNT = Math.max(2, Runtime.getRuntime().availableProcessors() - 2);
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpEventMain.class);

    public static void main(final String[] args) throws Exception {
        LOGGER.info("----- Running the server on machine with {} cores -----", Runtime.getRuntime().availableProcessors());

        final HttpEventMain server = new HttpEventMain(null, 4333);

        final int bufferSize = 32;

        final Disruptor<HttpEvent> disruptor = new Disruptor<>(HttpEvent::new, bufferSize, DaemonThreadFactory.INSTANCE);

        final HttpEventHandler[] handlers = new HttpEventHandler[THREAD_COUNT];

        for (int ordinal = 0; ordinal < handlers.length; ordinal++) {
            handlers[ordinal] = new HttpEventHandler(THREAD_COUNT, ordinal, server.getMapResponseReady());
        }

        disruptor.handleEventsWith(handlers).then(new CleaningEventHandler());

        final RingBuffer<HttpEvent> ringBuffer = disruptor.start();

        server.setProducer(new HttpEventProducer(ringBuffer, server.getMapResponseReady()));

        LOGGER.info("==================== Details ====================");
        LOGGER.info("Server: {}:{}", InetAddress.getLocalHost().getCanonicalHostName(), server.getPort());

        try {
            server.start();
        }
        catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            System.exit(0);
        }
    }

    private final InetAddress address;
    private final Map<SelectionKey, String> mapKey;
    private final Map<String, Boolean> mapResponseReady;
    private final int port;

    private HttpEventProducer producer;
    private Selector selector;

    private HttpEventMain(final InetAddress address, final int port) {
        super();

        this.address = address;
        this.port = port;
        this.mapResponseReady = new ConcurrentHashMap<>();
        this.mapKey = new ConcurrentHashMap<>();
    }

    public Map<String, Boolean> getMapResponseReady() {
        return mapResponseReady;
    }

    public int getPort() {
        return port;
    }

    public void setProducer(final HttpEventProducer producer) {
        this.producer = producer;
    }

    private void accept(final SelectionKey key) throws IOException {
        final ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        final SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);

        // Socket socket = channel.socket();
        // SocketAddress remoteAddress = socket.getRemoteSocketAddress();

        channel.register(selector, SelectionKey.OP_READ);
    }

    private boolean isResponseReady(final SelectionKey key) {
        final String requestId = mapKey.get(key);
        final boolean responseReady = mapResponseReady.getOrDefault(requestId, false);

        if (!responseReady) {
            return false;
        }

        mapKey.remove(key);
        mapResponseReady.remove(requestId);

        return true;
    }

    private void read(final SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();

        final ByteBuffer buffer = ByteBuffer.allocate(8192);
        int numRead = -1;

        numRead = channel.read(buffer);

        if (numRead == -1) {
            // Socket socket = channel.socket();
            // SocketAddress remoteAddress = socket.getRemoteSocketAddress();
            channel.close();
            key.cancel();

            return;
        }

        final String remoteAddress = channel.getRemoteAddress().toString();
        String requestID = remoteAddress + "_" + RandomStringUtils.secureStrong().nextNumeric(4);

        while (mapKey.containsValue(requestID) || mapResponseReady.containsKey(requestID)) {
            requestID = remoteAddress + "_" + RandomStringUtils.secureStrong().nextNumeric(4);
        }

        mapKey.put(key, requestID);

        producer.onData(requestID, buffer, numRead);

        channel.register(selector, SelectionKey.OP_WRITE, buffer);
    }

    private void start() throws IOException {
        selector = Selector.open();
        final ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        final InetSocketAddress listenAddress = new InetSocketAddress(address, port);
        serverChannel.socket().bind(listenAddress);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        LOGGER.info("Server ready. Ctrl-C to stop.");

        while (!Thread.interrupted()) {
            final int readyChannels = selector.select();

            if (readyChannels == 0) {
                continue;
            }

            final Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                final SelectionKey key = keys.next();
                keys.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    accept(key);
                }
                else if (key.isReadable()) {
                    read(key);
                }
                else if (key.isWritable()) {
                    write(key);
                }
            }
        }
    }

    private void write(final SelectionKey key) throws IOException {
        if (isResponseReady(key)) {
            final SocketChannel channel = (SocketChannel) key.channel();
            final ByteBuffer buffer = (ByteBuffer) key.attachment();

            buffer.flip();
            channel.write(buffer);

            channel.close();
            key.cancel();
        }
    }
}
