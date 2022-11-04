package net.shyshkin.study.rsocket.springrsocket;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Lec08ConnectionRetryManualTest - Manually start Server - SpringRsocketApplication then run test")
@Disabled("Only for manual testing")
//@TestPropertySource(properties = {"spring.rsocket.server.port=6564"})
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration"
})
public class Lec08ConnectionRetryManualTest {

    @Autowired
    RSocketRequester.Builder builder;

    @Autowired
    RSocketMessageHandler handler;

    @Test
    void retryTest() throws InterruptedException {
        //given
        RSocketRequester requester = builder
                .rsocketConnector(connector -> connector.reconnect(
                        Retry.fixedDelay(10, Duration.ofSeconds(1))
                                .doBeforeRetry(retrySignal -> log.debug("retrying {}", retrySignal.totalRetriesInARow()))))
                .tcp("localhost", 6565);

        //when
        for (int i = 0; i < 50; i++) {
            Mono<ComputationResponseDto> mono = requester
                    .route("math.service.find_square")
                    .data(new ComputationRequestDto(i))
                    .retrieveMono(ComputationResponseDto.class)
                    .doOnNext(dto -> log.debug("response: {}", dto));

            //then
            StepVerifier.create(mono)
                    .expectNextCount(1)
                    .verifyComplete();
            log.debug("sleep");
            Thread.sleep(2000);
        }
    }

    @Test
    @DisplayName("Retry register client test - Manually Start Server and Nginx, play with Start/Stop Nginx")
    void retryTest_forClientManager() throws InterruptedException {
        //given
        RSocketRequester requester = builder
                .setupRoute("math.events.connection")
                .rsocketConnector(connector -> connector.reconnect(
                        Retry.fixedDelay(10, Duration.ofSeconds(1))
                                .doBeforeRetry(retrySignal -> log.debug("retrying {}", retrySignal.totalRetriesInARow()))))
                .tcp("localhost", 6566);

        //when
        for (int i = 0; i < 50; i++) {
            Mono<ComputationResponseDto> mono = requester
                    .route("math.service.find_square")
                    .data(new ComputationRequestDto(i))
                    .retrieveMono(ComputationResponseDto.class)
                    .doOnNext(dto -> log.debug("response: {}", dto));

            //then
            StepVerifier.create(mono)
                    .expectNextCount(1)
                    .verifyComplete();
            log.debug("sleep");
            Thread.sleep(2000);
        }
    }
}

