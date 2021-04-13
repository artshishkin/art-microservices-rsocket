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
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Manually start Server - SpringRsocketApplication then run test")
@Disabled("Only for manual testing")
//@TestPropertySource(properties = {"spring.rsocket.server.port=6564"})
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration"
})
public class Lec09SessionResumptionTest {

    @Autowired
    RSocketRequester.Builder builder;

    @Autowired
    RSocketMessageHandler handler;

    @Test
    void retryTest() throws InterruptedException {
        //given
        int input = 3;
        String route = "math.service.table";
        RSocketRequester requester = builder
                .tcp("localhost", 6565);

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
}

