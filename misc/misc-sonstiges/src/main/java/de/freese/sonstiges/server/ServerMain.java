// Created: 05.09.2020
package de.freese.sonstiges.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.server.handler.HttpIoHandler;
import de.freese.sonstiges.server.handler.IoHandler;
import de.freese.sonstiges.server.multithread.ServerMultiThread;
import de.freese.sonstiges.server.singlethread.ServerSingleThread;

/**
 * @author Thomas Freese
 */
public final class ServerMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMain.class);

    public static String getRemoteAddress(final SelectionKey selectionKey) throws IOException {
        final SelectableChannel selectableChannel = selectionKey.channel();

        if (selectableChannel instanceof SocketChannel sc) {
            return getRemoteAddress(sc);
        }

        return null;
    }

    public static String getRemoteAddress(final SocketChannel socketChannel) throws IOException {
        return socketChannel.getRemoteAddress().toString();
    }

    public static void main(final String[] args) throws Exception {
        // final SelectorProvider selectorProvider = SelectorProvider.provider();

        // final AbstractServer server = new ServerSingleThread(8001);
        final AbstractServer server = new ServerMultiThread(8001, 2, 4);
        // final AbstractServer server = new ServerAsync(8001, 4);

        server.setIoHandler(new HttpIoHandler());
        server.start();
        // ForkJoinPool.commonPool().execute(server);

        LOGGER.info("******************************************************************************************************************");
        LOGGER.info("You're using an IDE, click in this console and press ENTER to call System.exit() and trigger the shutdown routine.");
        LOGGER.info("******************************************************************************************************************");

        // Console für programmatische Eingabe simulieren.
        // final PipedOutputStream pos = new PipedOutputStream();
        // final PipedInputStream pis = new PipedInputStream(pos);
        // System.setIn(pis);

        while (!server.isStarted()) {
            LOGGER.info("check started");
            TimeUnit.MILLISECONDS.sleep(100);
        }

        final InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8001);
        final Charset charset = IoHandler.DEFAULT_CHARSET;

        // try (SocketChannel client = selectorProvider.openSocketChannel())
        try (SocketChannel client = SocketChannel.open(serverAddress)) {
            // client.connect(serverAddress);

            while (!client.finishConnect()) {
                TimeUnit.MILLISECONDS.sleep(10);
            }

            client.configureBlocking(true);

            requestResponse(client, charset);

            TimeUnit.SECONDS.sleep(1);

            requestResponse(client, charset);
        }

        // Console simulieren.
        // pos.write(0);

        try {
            System.in.read();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        server.stop();
        System.exit(0);
    }

    private static void requestResponse(final SocketChannel client, final Charset charset) throws Exception {
        // Request
        final CharBuffer charBufferHeader = CharBuffer.allocate(256);
        charBufferHeader.put("GET / HTTP/1.1").put(System.lineSeparator());
        charBufferHeader.put("Host: localhost:8001").put(System.lineSeparator());
        charBufferHeader.put("User-Agent: " + ServerSingleThread.class.getSimpleName()).put(System.lineSeparator());
        charBufferHeader.put("Accept: text/html").put(System.lineSeparator());
        charBufferHeader.put("Accept-Language: de").put(System.lineSeparator());
        charBufferHeader.put("Accept-Encoding: gzip, deflate").put(System.lineSeparator());
        charBufferHeader.put("Connection: keep-alive").put(System.lineSeparator());
        charBufferHeader.put("").put(System.lineSeparator());
        charBufferHeader.flip();

        ByteBuffer buffer = charset.encode(charBufferHeader);

        while (buffer.hasRemaining()) {
            client.write(buffer);
        }

        // Response
        buffer = ByteBuffer.allocate(1024);

        while (client.read(buffer) > 0) {
            buffer.flip();

            final CharBuffer charBuffer = charset.decode(buffer);

            LOGGER.info(charBuffer.toString().strip());

            buffer.clear();
        }
    }

    private ServerMain() {
        super();
    }
}
