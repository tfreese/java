// Created: 26.08.2020
package de.freese.sonstiges.disruptor.http;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class HttpEventHandler implements EventHandler<HttpEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpEventHandler.class);

    private final Map<String, Boolean> mapResponseReady;

    private final int ordinal;

    public HttpEventHandler(final int ordinal, final Map<String, Boolean> mapResponseReady) {
        super();

        this.ordinal = ordinal;
        this.mapResponseReady = Objects.requireNonNull(mapResponseReady, "mapResponseReady required");
    }

    /**
     * @see EventHandler#onEvent(Object, long, boolean)
     */
    @Override
    public void onEvent(final HttpEvent event, final long sequence, final boolean endOfBatch) throws Exception {
        // Load-Balancing auf die Handler über die Sequence.
        // Sonst würden alle Handler gleichzeitig eine Sequence bearbeiten.
        if ((this.ordinal == -1) || (this.ordinal == (sequence % HttpEventMain.THREAD_COUNT))) {
            LOGGER.info("{}: HttpEventHandler.onEvent: RequestId={}, Sequence={}", Thread.currentThread().getName(), event.getRequestId(), sequence);

            String requestId = event.getRequestId();
            ByteBuffer buffer = event.getBuffer();
            int numRead = event.getNumRead();

            ByteBuffer responseBuffer = handleRequest(buffer, numRead, sequence);

            if (responseBuffer == null) {
                return;
            }

            this.mapResponseReady.put(requestId, Boolean.TRUE);
        }
    }

    private ByteBuffer handleRequest(final ByteBuffer buffer, final int numRead, final long sequence) {
        buffer.flip();

        byte[] data = new byte[numRead];
        buffer.get(data);

        String request = new String(data, StandardCharsets.UTF_8);
        // request = request.split("\n")[0].trim();

        // HTTP-Request handling.
        if (!request.startsWith("GET")) {
            return null;
        }

        String response = serverResponse(sequence);

        buffer.clear();
        buffer.put((response).getBytes(StandardCharsets.UTF_8));

        return buffer;
    }

    private String serverResponse(final long sequence) {
        StringBuilder body = new StringBuilder();
        body.append("<html lang=\"de\">").append("\r\n");
        body.append(" <head>").append("\r\n");
        body.append("     <meta charset=\"UTF-8\">").append("\r\n");
        body.append("     <title>Disruptor-Demo</title>").append("\r\n");
        body.append(" </head>").append("\r\n");
        body.append(" <body>").append("\r\n");
        body.append("     Sample Response: ").append(LocalDateTime.now()).append("<br>\r\n");
        body.append("     Sequence: ").append(sequence).append("<br>\r\n");
        body.append(" </body>").append("\r\n");
        body.append("</html>").append("\r\n");

        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK").append("\r\n");
        sb.append("Server: disruptor").append("\r\n");
        sb.append("Content-type: text/html").append("\r\n");
        sb.append("\r\n");
        sb.append(body);

        return sb.toString();
    }
}
