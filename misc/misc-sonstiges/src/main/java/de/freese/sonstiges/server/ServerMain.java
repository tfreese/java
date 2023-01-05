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

import de.freese.sonstiges.server.handler.HttpIoHandler;
import de.freese.sonstiges.server.handler.IoHandler;
import de.freese.sonstiges.server.multithread.ServerMultiThread;
import de.freese.sonstiges.server.singlethread.ServerSingleThread;

/**
 * @author Thomas Freese
 */
public final class ServerMain
{
    public static String getRemoteAddress(final SelectionKey selectionKey) throws IOException
    {
        SelectableChannel selectableChannel = selectionKey.channel();

        if (selectableChannel instanceof SocketChannel sc)
        {
            return getRemoteAddress(sc);
        }

        return null;
    }

    public static String getRemoteAddress(final SocketChannel socketChannel) throws IOException
    {
        return socketChannel.getRemoteAddress().toString();
    }

    public static void main(final String[] args) throws Exception
    {
        // final SelectorProvider selectorProvider = SelectorProvider.provider();

        // AbstractServer server = new ServerSingleThread(8001);
        AbstractServer server = new ServerMultiThread(8001, 2, 4);
        // AbstractServer server = new ServerAsync(8001, 4);

        server.setIoHandler(new HttpIoHandler());
        server.start();
        // ForkJoinPool.commonPool().execute(server);

        System.out.println();
        System.out.println();
        System.out.println("******************************************************************************************************************");
        System.out.println("You're using an IDE, click in this console and press ENTER to call System.exit() and trigger the shutdown routine.");
        System.out.println("******************************************************************************************************************");
        System.out.println();
        System.out.println();

        // Console für programmatische Eingabe simulieren.
        // PipedOutputStream pos = new PipedOutputStream();
        // PipedInputStream pis = new PipedInputStream(pos);
        // System.setIn(pis);

        while (!server.isStarted())
        {
            System.out.println("check started");
            TimeUnit.MILLISECONDS.sleep(100);
        }

        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8001);
        Charset charset = IoHandler.DEFAULT_CHARSET;

        // try (SocketChannel client = selectorProvider.openSocketChannel())
        try (SocketChannel client = SocketChannel.open(serverAddress))
        {
            // client.connect(serverAddress);

            while (!client.finishConnect())
            {
                TimeUnit.MILLISECONDS.sleep(10);
            }

            client.configureBlocking(true);

            requestResponse(client, charset);

            TimeUnit.SECONDS.sleep(1);

            requestResponse(client, charset);
        }

        // Console simulieren.
        // pos.write(0);

        try
        {
            System.in.read();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        server.stop();
        System.exit(0);
    }

    private static void requestResponse(final SocketChannel client, final Charset charset) throws Exception
    {
        // Request
        CharBuffer charBufferHeader = CharBuffer.allocate(256);
        charBufferHeader.put("GET / HTTP/1.1").put("\r\n");
        charBufferHeader.put("Host: localhost:8001").put("\r\n");
        charBufferHeader.put("User-Agent: " + ServerSingleThread.class.getSimpleName()).put("\r\n");
        charBufferHeader.put("Accept: text/html").put("\r\n");
        charBufferHeader.put("Accept-Language: de").put("\r\n");
        charBufferHeader.put("Accept-Encoding: gzip, deflate").put("\r\n");
        charBufferHeader.put("Connection: keep-alive").put("\r\n");
        charBufferHeader.put("").put("\r\n");
        charBufferHeader.flip();

        ByteBuffer buffer = charset.encode(charBufferHeader);

        while (buffer.hasRemaining())
        {
            client.write(buffer);
        }

        // Response
        buffer = ByteBuffer.allocate(1024);

        while (client.read(buffer) > 0)
        {
            buffer.flip();

            CharBuffer charBuffer = charset.decode(buffer);

            System.out.println();
            System.out.println(charBuffer.toString().strip());

            buffer.clear();
        }
    }

    private ServerMain()
    {
        super();
    }
}
