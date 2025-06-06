// Created: 26.08.2020
package de.freese.sonstiges.disruptor.http;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import de.freese.sonstiges.disruptor.AbstractLoadBalancedEventHandler;

/**
 * @author Thomas Freese
 */
public class HttpEventHandler extends AbstractLoadBalancedEventHandler<HttpEvent> {
    private final Map<String, Boolean> mapResponseReady;

    public HttpEventHandler(final int parallelism, final int ordinal, final Map<String, Boolean> mapResponseReady) {
        super(parallelism, ordinal);

        this.mapResponseReady = Objects.requireNonNull(mapResponseReady, "mapResponseReady required");
    }

    @Override
    protected void doOnEvent(final HttpEvent event, final long sequence, final boolean endOfBatch) throws Exception {
        getLogger().info("{}: HttpEventHandler.onEvent: RequestId={}, Sequence={}", Thread.currentThread().getName(), event.getRequestId(), sequence);

        final String requestId = event.getRequestId();
        final ByteBuffer buffer = event.getBuffer();
        final int numRead = event.getNumRead();

        final ByteBuffer responseBuffer = handleRequest(buffer, numRead, sequence);

        if (responseBuffer == null) {
            return;
        }

        mapResponseReady.put(requestId, Boolean.TRUE);
    }

    private ByteBuffer handleRequest(final ByteBuffer buffer, final int numRead, final long sequence) {
        buffer.flip();

        final byte[] data = new byte[numRead];
        buffer.get(data);

        final String request = new String(data, StandardCharsets.UTF_8);
        // request = request.split("\n")[0].trim();

        // HTTP-Request handling.
        if (!request.startsWith("GET")) {
            return null;
        }

        final String response = serverResponse(sequence);

        buffer.clear();
        buffer.put(response.getBytes(StandardCharsets.UTF_8));

        return buffer;
    }

    private String serverResponse(final long sequence) {
        final StringBuilder body = new StringBuilder();
        body.append("<html lang=\"de\">").append(System.lineSeparator());
        body.append(" <head>").append(System.lineSeparator());
        body.append("     <meta charset=\"UTF-8\">").append(System.lineSeparator());
        body.append("     <title>Disruptor-Demo</title>").append(System.lineSeparator());
        body.append(" </head>").append(System.lineSeparator());
        body.append(" <body>").append(System.lineSeparator());
        body.append("     Sample Response: ").append(LocalDateTime.now()).append("<br>").append(System.lineSeparator());
        body.append("     Sequence: ").append(sequence).append("<br>").append(System.lineSeparator());
        body.append(" </body>").append(System.lineSeparator());
        body.append("</html>").append(System.lineSeparator());

        final StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK").append(System.lineSeparator());
        sb.append("Server: disruptor").append(System.lineSeparator());
        sb.append("Content-type: text/html").append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(body);

        return sb.toString();
    }
}
