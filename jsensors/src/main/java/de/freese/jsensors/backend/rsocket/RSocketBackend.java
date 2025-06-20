// Created: 28.10.2020
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
import reactor.core.publisher.Mono;
import reactor.netty.resources.LoopResources;
import reactor.netty.tcp.TcpClient;
import reactor.util.retry.Retry;

import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.sensor.SensorValue;
import de.freese.jsensors.utils.LifeCycle;

/**
 * @author Thomas Freese
 */
public class RSocketBackend extends AbstractBackend implements LifeCycle {
    private final int parallelism;
    private final URI uri;

    private RSocketClient client;

    public RSocketBackend(final URI uri, final int parallelism) {
        super();

        this.uri = Objects.requireNonNull(uri, "uri required");

        if (parallelism < 1) {
            throw new IllegalArgumentException("parallelism < 1: " + parallelism);
        }

        this.parallelism = parallelism;
    }

    @Override
    public void start() {
        final TcpClient tcpClient = TcpClient.create()
                .host(uri.getHost())
                .port(uri.getPort())
                .runOn(LoopResources.create("jSensor-client-" + uri.getPort(), parallelism, true));

        final RSocketConnector connector = RSocketConnector.create()
                .payloadDecoder(PayloadDecoder.DEFAULT)
                .reconnect(Retry.fixedDelay(3, Duration.ofSeconds(1)));

        final Mono<RSocket> rSocket = connector.connect(TcpClientTransport.create(tcpClient));

        client = RSocketClient.from(rSocket);
    }

    @Override
    public void stop() {
        client.dispose();
    }

    protected ByteBuf encode(final SensorValue sensorValue) {
        final ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();

        // byteBuf.writeCharSequence(sensorValue.getName(), StandardCharsets.UTF_8);
        byte[] bytes = sensorValue.getName().getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        // byteBuf.writeCharSequence(sensorValue.getValue(), StandardCharsets.UTF_8);
        bytes = sensorValue.getValue().getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

        byteBuf.writeLong(sensorValue.getTimestamp());

        return byteBuf;
    }

    @Override
    protected void storeValue(final SensorValue sensorValue) {
        final ByteBuf byteBuf = encode(sensorValue);

        client.fireAndForget(Mono.just(ByteBufPayload.create(byteBuf)))
                .block()
        ;

        // byteBuf.release();
    }
}
