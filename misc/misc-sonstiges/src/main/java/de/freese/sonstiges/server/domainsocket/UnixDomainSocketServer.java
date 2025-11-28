package de.freese.sonstiges.server.domainsocket;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UnixDomainSocketServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnixDomainSocketServer.class);
    private static final int MAX_MESSAGE_SIZE = 1024 * 1024; // 1 MB Limit
    private static final Path SOCKET_PATH = Path.of(System.getProperty("java.io.tmpdir")).resolve("unixDomainSocket.socket");

    private static class ClientState {
        final ByteBuffer lengthBuffer = ByteBuffer.allocate(4); // For Prefix
        final Deque<ByteBuffer> writeQueue = new ArrayDeque<>();
        boolean closing = false;
        ByteBuffer payloadBuffer = null;
    }

    static void main() throws IOException {
        new UnixDomainSocketServer().run();
    }

    // Optional Pool for Business-Logic.
    private final ExecutorService workers = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors() / 2));

    private UnixDomainSocketServer() {
        super();
    }

    private void closeKey(final SelectionKey key) {
        try {
            key.cancel();
            key.channel().close();
        }
        catch (IOException _) {
            // Empty
        }

        LOGGER.info("Client closed.");
    }

    private void enableWrite(final SelectionKey key, final Selector selector) {
        selector.wakeup();

        try {
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        }
        catch (CancelledKeyException _) {
            // Empty
        }
    }

    private void enqueueMessage(final ClientState state, final String msg) {
        final byte[] payload = msg.getBytes(StandardCharsets.UTF_8);
        final ByteBuffer prefix = ByteBuffer.allocate(4);

        prefix.putInt(payload.length);
        prefix.flip();

        state.writeQueue.add(prefix);
        state.writeQueue.add(ByteBuffer.wrap(payload));
    }

    private void handleAccept(final ServerSocketChannel server, final Selector selector) throws IOException {
        final SocketChannel client = server.accept();

        if (client == null) {
            return;
        }

        client.configureBlocking(false);

        final ClientState state = new ClientState();
        final SelectionKey key = client.register(selector, SelectionKey.OP_READ, state);
        LOGGER.info("Client connected: {}", client);

        // Welcome Message in the Write-Queue.
        enqueueMessage(state, "Welcome! Send Messages and i will write back." + System.lineSeparator());

        // Activate OP_WRITE (if it is not already set).
        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
    }

    private void handleRead(final SelectionKey key, final Selector selector) throws IOException {
        final SocketChannel client = (SocketChannel) key.channel();
        final ClientState state = (ClientState) key.attachment();

        // Step 1: Read Message length
        if (state.payloadBuffer == null) {
            final int read = client.read(state.lengthBuffer);

            if (read == -1) {
                closeKey(key);
                return;
            }

            if (state.lengthBuffer.hasRemaining()) {
                // Not enough data read.
                return;
            }

            state.lengthBuffer.flip();
            final int length = state.lengthBuffer.getInt();
            state.lengthBuffer.clear();

            if (length <= 0 || length > MAX_MESSAGE_SIZE) {
                LOGGER.warn("Invalid Length: {}", length);
                closeKey(key);
                return;
            }

            state.payloadBuffer = ByteBuffer.allocate(length);
        }

        // Step 2: Read Payload
        final int read = client.read(state.payloadBuffer);

        if (read == -1) {
            closeKey(key);
            return;
        }

        if (state.payloadBuffer.hasRemaining()) {
            // Still not complete.
            return;
        }

        // Complete Message.
        state.payloadBuffer.flip();
        final byte[] data = new byte[state.payloadBuffer.remaining()];
        state.payloadBuffer.get(data);
        state.payloadBuffer = null;

        final String message = new String(data, StandardCharsets.UTF_8);
        LOGGER.info("Received: {}", message);

        if ("close".equals(message)) {
            closeKey(key);
        }

        // Response
        workers.submit(() -> {
            final String response = "Server: " + message;

            synchronized (state) {
                enqueueMessage(state, response);
            }

            enableWrite(key, selector);
        });
    }

    private void handleWrite(final SelectionKey key) throws IOException {
        final SocketChannel client = (SocketChannel) key.channel();
        final ClientState state = (ClientState) key.attachment();

        while (!state.writeQueue.isEmpty()) {
            final ByteBuffer buf = state.writeQueue.peek();
            final int written = client.write(buf);

            if (written == 0) {
                break;
            }

            if (!buf.hasRemaining()) {
                state.writeQueue.poll();
            }
        }

        if (state.writeQueue.isEmpty()) {
            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);

            if (state.closing) {
                closeKey(key);
            }
        }
    }

    private void run() throws IOException {
        // Cleanup Socket-File.
        Files.deleteIfExists(SOCKET_PATH);

        try (ServerSocketChannel server = ServerSocketChannel.open(StandardProtocolFamily.UNIX)) {
            server.bind(UnixDomainSocketAddress.of(SOCKET_PATH));

            // Random Socket-File.
            // server.bind(null);
            // final Path socketPath = ((UnixDomainSocketAddress) server.getLocalAddress()).getPath();

            server.configureBlocking(false);

            final Selector selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);

            LOGGER.info("Server started: {}", SOCKET_PATH);

            while (!Thread.interrupted()) {
                final int readyChannels = selector.select();

                LOGGER.debug("readyChannels = {}", readyChannels);

                if (!selector.isOpen()) {
                    break;
                }

                final Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    final SelectionKey key = iterator.next();
                    iterator.remove();

                    try {
                        if (!key.isValid()) {
                            continue;
                        }

                        if (key.isAcceptable()) {
                            handleAccept(server, selector);
                        }
                        else if (key.isReadable()) {
                            handleRead(key, selector);
                        }
                        else if (key.isWritable()) {
                            handleWrite(key);
                        }
                        else {
                            closeKey(key);
                        }
                    }
                    catch (IOException _) {
                        closeKey(key);
                    }
                }
            }
        }
        finally {
            workers.shutdown();
            Files.deleteIfExists(SOCKET_PATH);
        }
    }
}
