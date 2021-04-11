package net.shyshkin.study.rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketClient;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@Slf4j
@DisplayName("Start server `Lec04PersistentConnectionServer`, run test, during 15s pause restart server - now all ok, NO exception java.nio.channels.ClosedChannelException")
class Lec04PersistentConnectionManualTest {

    private static RSocketClient rSocketClient;

    @BeforeAll
    static void beforeAll() {

        Mono<RSocket> rSocketMono = RSocketConnector.create()
                .connect(TcpClientTransport.create(6565))
                .doOnNext(rs -> log.debug("going to connect"));

        rSocketClient = RSocketClient.from(rSocketMono);
    }

    @Test
    void connectionTest() throws InterruptedException {
        //given
        Payload payload = DefaultPayload.create("NO Matter");

        //when
        Flux<String> flux1 = rSocketClient.requestStream(Mono.just(payload))
                .delayElements(Duration.ofMillis(50)) //slow client process emulation
                .map(Payload::getDataUtf8)
                .doOnNext(dto -> log.debug("client: receive {}", dto))
                .take(10);

        StepVerifier
                .create(flux1)
                .expectNextCount(10)
                .verifyComplete();

        log.debug("going to sleep");
        Thread.sleep(15000);
        log.debug("woke up");

        Flux<String> flux2 = rSocketClient.requestStream(Mono.just(payload))
                .delayElements(Duration.ofMillis(50)) //slow client process emulation
                .map(Payload::getDataUtf8)
                .doOnNext(dto -> log.debug("client: receive {}", dto))
                .take(10);

        StepVerifier
                .create(flux2)
                .expectNextCount(10)
                .verifyComplete();
    }
}