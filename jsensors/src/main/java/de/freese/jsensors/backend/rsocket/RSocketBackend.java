package de.freese.jsensors.backend.rsocket;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketClient;
import io.rsocket.core.RSocketConnector;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.ByteBufPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpClient;
import reactor.util.retry.Retry;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.SensorValue;

/**
 * @author Thomas Freese
 * @since 28.10.2020
 */
public class RSocketBackend implements Backend, AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RSocketBackend.class);

    public static class Builder {
        private int parallelism;
        private URI uri;

        Builder() {
            super();
        }

        public RSocketBackend build() {
            if (parallelism < 1) {
                throw new IllegalArgumentException("parallelism < 1: " + parallelism);
            }

            Objects.requireNonNull(uri, "uri required");

            final TcpClient tcpClient = TcpClient.create()
                    .host(uri.getHost())
                    .port(uri.getPort())
                    .runOn(LoopResources.create("jSensor-client-" + uri.getPort(), parallelism, true));

            final RSocketConnector connector = RSocketConnector.create()
                    .payloadDecoder(PayloadDecoder.DEFAULT)
                    .reconnect(Retry.fixedDelay(3, Duration.ofSeconds(1)));

            final Mono<RSocket> rSocket = connector.connect(TcpClientTransport.create(tcpClient));

            final RSocketClient client = RSocketClient.from(rSocket);

            return new RSocketBackend(client);
        }

        public Builder parallelism(final int parallelism) {
            this.parallelism = parallelism;

            return this;
        }

        public Builder uri(final URI uri) {
            this.uri = uri;

            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final RSocketClient client;

    RSocketBackend(final RSocketClient client) {
        super();

        this.client = client;
    }

    @Override
    public void close() {
        client.dispose();
    }

    @Override
    public void store(final SensorValue sensorValue) {
        if (sensorValue == null) {
            LOGGER.warn("sensorValue is null");
            return;
        }

        if (sensorValue.value() == null || sensorValue.value().isEmpty()) {
            LOGGER.warn("sensorValue without content");
            return;
        }

        final ByteBuf byteBuf = encode(sensorValue);

        client.fireAndForget(Mono.just(ByteBufPayload.create(byteBuf)))
                .block()
        ;

        // byteBuf.release();
    }

    protected ByteBuf encode(final SensorValue sensorValue) {
        final ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();

        // byteBuf.writeCharSequence(sensorValue.getName(), StandardCharsets.UTF_8);
        byte[] bytes = sensorValue.name().getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        // byteBuf.writeCharSequence(sensorValue.getValue(), StandardCharsets.UTF_8);
        bytes = sensorValue.value().getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        byteBuf.writeLong(sensorValue.timestamp());

        return byteBuf;
    }
}
