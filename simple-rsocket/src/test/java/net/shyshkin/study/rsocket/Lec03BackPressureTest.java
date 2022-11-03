package net.shyshkin.study.rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.service.FastProducerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.concurrent.Queues;

import java.time.Duration;

@Slf4j
class Lec03BackPressureTest {

    private static RSocket rSocket;
    private static CloseableChannel closeableChannel;

    @BeforeAll
    static void beforeAll() {

        RSocketServer rSocketServer = RSocketServer
                .create(
                        (setup, sendingSocket) -> Mono.just(new FastProducerService())
                );
        closeableChannel = rSocketServer.bindNow(TcpServerTransport.create(6565));

        rSocket = RSocketConnector.create()
                .connect(TcpClientTransport.create(6565))
                .block();
    }

    @AfterAll
    static void afterAll() {
        closeableChannel.dispose();
    }

    @Test
    void backpressure() {
        //given
        log.debug("Default buffer size: {} can be overrode by `reactor.bufferSize.x` system property", Queues.XS_BUFFER_SIZE);
        Payload payload = DefaultPayload.create("NO Matter");

        //when
        Flux<Payload> requestStream = rSocket.requestStream(payload);

        //then
        Flux<String> responseDtoFlux = requestStream
                .delayElements(Duration.ofMillis(50)) //slow client process emulation
                .map(Payload::getDataUtf8)
                .doOnNext(dto -> log.debug("client: receive {}", dto));

        StepVerifier
                .create(responseDtoFlux)
                .expectNextCount(100)
                .verifyComplete();
    }
}