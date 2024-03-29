package net.shyshkin.study.rsocket.springrsocket;

import io.rsocket.core.Resume;
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
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Lec09SessionResumptionManualTest - Manually start Server (SpringRsocketApplication) and Nginx then run test, during test stop Nginx and start it again")
@Disabled("Only for manual testing")
//@TestPropertySource(properties = {"spring.rsocket.server.port=6564"})
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration"
})
public class Lec09SessionResumptionManualTest {

    @Autowired
    RSocketRequester.Builder builder;

    @Autowired
    RSocketMessageHandler handler;

    @Test
    void resumeTest() {
        //given
        int input = 1;
        String route = "math.service.table";
        RSocketRequester requester = builder
                .rsocketConnector(c -> c
                        .resume(resumeStrategy())
                        .reconnect(retryStrategy()))
                .tcp("localhost", 6566);

        //when
        Flux<ComputationResponseDto> flux = requester.route(route)
                .data(new ComputationRequestDto(input))
                .retrieveFlux(ComputationResponseDto.class)
                .doOnNext(dto -> log.debug("Receive {}", dto));

        //then
        StepVerifier.create(flux)
                .expectNextCount(1000)
                .verifyComplete();
    }

    private Resume resumeStrategy() {
        return new Resume()
                .retry(Retry
                        .fixedDelay(1000, Duration.ofSeconds(2))
                        .doBeforeRetry(s -> log.debug("Resume session: retries in a row {}, total {}", s.totalRetriesInARow(), s.totalRetries())));
    }

    private Retry retryStrategy() {
        return Retry
                .fixedDelay(100, Duration.ofSeconds(1))
                .doBeforeRetry(r -> log.debug("Retrying connection: retries in a row {}, total {}", r.totalRetriesInARow(), r.totalRetries()));
    }
}

