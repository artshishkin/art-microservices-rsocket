package net.shyshkin.study.rsocket.springrsocket;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Lec06ConnectionSetupTest {

    RSocketRequester requester;

    @Autowired
    RSocketRequester.Builder builder;

    @BeforeAll
    void beforeAll() {
        requester = builder.tcp("localhost", 6565);
    }

    @RepeatedTest(3)
    @DisplayName("Despite we run test 3 times `Connection Setup` happens only once")
    void connectionTest() {
        //given
        int input = ThreadLocalRandom.current().nextInt(1, 100);

        //when
        Mono<ComputationResponseDto> mono = requester.route("math.service.find_square")
                .data(new ComputationRequestDto(input))
                .retrieveMono(ComputationResponseDto.class)
                .doOnNext(dto -> log.debug("client receives {}", dto));

        //then
        StepVerifier.create(mono)
                .expectNextCount(1)
                .verifyComplete();
    }
}
