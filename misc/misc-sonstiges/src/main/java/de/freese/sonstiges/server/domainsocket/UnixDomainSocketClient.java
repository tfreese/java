package de.freese.sonstiges.server.domainsocket;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UnixDomainSocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnixDomainSocketClient.class);

    static void main() throws IOException {
        final Path socketPath = Path.of(System.getProperty("java.io.tmpdir")).resolve("unixDomainSocket.socket");

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
             SocketChannel client = SocketChannel.open(StandardProtocolFamily.UNIX)) {
            client.connect(UnixDomainSocketAddress.of(socketPath));

            // Read Server Greeting.
            String response = readMessage(client);
            LOGGER.info("Response: {}", response);

            LOGGER.info("Enter Message (End with Ctrl+D/Ctrl+Z):");

            while (scanner.hasNextLine()) {
                final String message = scanner.nextLine();

                if ("close".equals(message)) {
                    break;
                }

                sendMessage(client, message);

                response = readMessage(client);
                LOGGER.info("Response: {}", response);
            }
        }
    }

    private static String readMessage(final SocketChannel client) throws IOException {
        final ByteBuffer lenBuf = ByteBuffer.allocate(4);

        while (lenBuf.hasRemaining()) {
            if (client.read(lenBuf) == -1) {
                throw new IOException("Connection closed");
            }
        }

        lenBuf.flip();

        final int length = lenBuf.getInt();
        final ByteBuffer payload = ByteBuffer.allocate(length);

        while (payload.hasRemaining()) {
            if (client.read(payload) == -1) {
                throw new IOException("Connection closed");
            }
        }

        payload.flip();

        final byte[] data = new byte[length];
        payload.get(data);

        return new String(data, StandardCharsets.UTF_8);
    }

    private static void sendMessage(final SocketChannel client, final String message) throws IOException {
        final byte[] payload = message.getBytes(StandardCharsets.UTF_8);
        final ByteBuffer prefix = ByteBuffer.allocate(4);

        prefix.putInt(payload.length);
        prefix.flip();

        client.write(new ByteBuffer[]{prefix, ByteBuffer.wrap(payload)});
    }

    private UnixDomainSocketClient() {
        super();
    }
}
