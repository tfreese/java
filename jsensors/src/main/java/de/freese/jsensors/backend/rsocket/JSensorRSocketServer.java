// Created: 19.10.2020
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
import de.freese.jsensors.utils.LifeCycle;

/**
 * Use this with {@link RoutingBackend} to support multiple {@link Sensor}s.
 *
 * @author Thomas Freese
 */
public class JSensorRSocketServer implements LifeCycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSensorRSocketServer.class);

    private final Backend backend;
    private final int parallelism;
    private final int port;

    private Disposable server;

    public JSensorRSocketServer(final Backend backend, final int port, final int parallelism) {
        super();

        this.backend = Objects.requireNonNull(backend, "backend required");

        if (port < 1) {
            throw new IllegalArgumentException("port < 1: " + port);
        }

        this.port = port;

        if (parallelism < 1) {
            throw new IllegalArgumentException("parallelism < 1: " + parallelism);
        }

        this.parallelism = parallelism;
    }

    @Override
    public void start() {
        getLogger().info("starting jSensor-rSocket server on port: {}", this.port);

        // Error message, if the Client closes the Connection.
        // Hooks.onErrorDropped(th -> LOGGER.error(th.getMessage()));
        Hooks.onErrorDropped(th -> {
            // Empty
        });

        final Resume resume = new Resume()
                .sessionDuration(Duration.ofMinutes(5))
                .retry(
                        Retry
                                .fixedDelay(10, Duration.ofSeconds(1))
                                .doBeforeRetry(s -> LOGGER.debug("Disconnected. Trying to resume..."))
                );

        final TcpServer tcpServer = TcpServer.create()
                .host("localhost")
                .port(this.port)
                .runOn(LoopResources.create("jSensor-server-", this.parallelism, false));

        final SocketAcceptor socketAcceptor = SocketAcceptor.forFireAndForget(this::forFireAndForget);

        this.server = RSocketServer.create()
                .acceptor(socketAcceptor)
                .resume(resume)
                .payloadDecoder(PayloadDecoder.DEFAULT)
                .bindNow(TcpServerTransport.create(tcpServer))
        ;
    }

    @Override
    public void stop() {
        getLogger().info("stopping jSensor-rSocket server");

        this.server.dispose();
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

        this.backend.store(sensorValue);

        return Mono.empty();
    }

    protected Logger getLogger() {
        return LOGGER;
    }
}
