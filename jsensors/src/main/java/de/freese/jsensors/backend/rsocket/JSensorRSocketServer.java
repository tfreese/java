package de.freese.jsensors.backend.rsocket;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

import io.netty.buffer.ByteBuf;
import io.rsocket.Payload;
import io.rsocket.SocketAcceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.core.Resume;
import io.rsocket.frame.decoder.PayloadDecoder;
import io.rsocket.transport.netty.server.TcpServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpServer;
import reactor.util.retry.Retry;

import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.backend.RoutingBackend;
import de.freese.jsensors.sensor.DefaultSensorValue;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.sensor.SensorValue;

/**
 * Use this with {@link RoutingBackend} to support multiple {@link Sensor}s.
 *
 * @author Thomas Freese
 * @since 19.10.2020
 */
public class JSensorRSocketServer implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSensorRSocketServer.class);

    public static class Builder {
        private Backend backend;
        private int parallelism;
        private int port;

        Builder() {
            super();
        }

        public Builder backend(final Backend backend) {
            this.backend = backend;

            return this;
        }

        public JSensorRSocketServer build() {
            if (port < 1) {
                throw new IllegalArgumentException("port < 1: " + port);
            }

            if (parallelism < 1) {
                throw new IllegalArgumentException("parallelism < 1: " + parallelism);
            }

            Objects.requireNonNull(backend, "backend required");

            LOGGER.info("starting jSensor-rSocket server on port: {}", port);

            // Error message, if the Client closes the Connection.
            // Hooks.onErrorDropped(th -> LOGGER.error(th.getMessage()));
            Hooks.onErrorDropped(th -> {
                // Empty
            });

            final Resume resume = new Resume()
                    .sessionDuration(Duration.ofMinutes(5L))
                    .retry(
                            Retry
                                    .fixedDelay(10L, Duration.ofSeconds(1L))
                                    .doBeforeRetry(s -> LOGGER.debug("Disconnected. Trying to resume..."))
                    );

            final TcpServer tcpServer = TcpServer.create()
                    .host("localhost")
                    .port(port)
                    .runOn(LoopResources.create("jSensor-server-" + port, parallelism, false));

            return new JSensorRSocketServer(backend, resume, tcpServer);
        }

        public Builder parallelism(final int parallelism) {
            this.parallelism = parallelism;

            return this;
        }

        public Builder port(final int port) {
            this.port = port;

            return this;
        }
    }

    public static Builder builder() {
        return new JSensorRSocketServer.Builder();
    }

    private final Backend backend;
    private final Disposable disposable;

    JSensorRSocketServer(final Backend backend, final Resume resume, final TcpServer tcpServer) {
        super();

        this.backend = backend;

        final SocketAcceptor socketAcceptor = SocketAcceptor.forFireAndForget(this::forFireAndForget);

        disposable = RSocketServer.create()
                .acceptor(socketAcceptor)
                .resume(resume)
                .payloadDecoder(PayloadDecoder.DEFAULT)
                .bindNow(TcpServerTransport.create(tcpServer));
    }

    @Override
    public void close() {
        getLogger().info("stopping jSensor-rSocket server");

        disposable.dispose();
    }

    protected SensorValue decode(final Payload payload) {
        final ByteBuf byteBuf = payload.data();

        int length = byteBuf.readInt();
        final String name = byteBuf.readCharSequence(length, StandardCharsets.UTF_8).toString();

        length = byteBuf.readInt();
        final String value = byteBuf.readCharSequence(length, StandardCharsets.UTF_8).toString();

        final long timeStamp = byteBuf.readLong();

        return new DefaultSensorValue(name, value, timeStamp);
    }

    protected Mono<Void> forFireAndForget(final Payload payload) {
        final SensorValue sensorValue = decode(payload);

        backend.store(sensorValue);

        return Mono.empty();
    }

    protected Logger getLogger() {
        return LOGGER;
    }
}
