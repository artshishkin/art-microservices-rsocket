package net.shyshkin.study.rsocket.springrsocket;

import io.rsocket.exceptions.RejectedSetupException;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ClientConnectionRequest;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class Lec06ConnectionSetupTest {

    RSocketRequester requester;

    @Autowired
    RSocketRequester.Builder builder;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ValidCredentials {

        @BeforeAll
        void beforeAll() {
            requester = builder
                    .setupData(new ClientConnectionRequest("myClientId", "mySecretKey"))
                    .tcp("localhost", 6565);
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class InvalidCredentials {

        @BeforeAll
        void beforeAll() {
            requester = builder
                    .setupData(new ClientConnectionRequest("myClientId", "invalid key"))
                    .tcp("localhost", 6565);
        }

        @Test
        @DisplayName("Connection with invalid credentials must be refused")
        void connectionTest() {
            //given
            int input = ThreadLocalRandom.current().nextInt(1, 100);

            //when
            Mono<ComputationResponseDto> mono = requester.route("math.service.find_square")
                    .data(new ComputationRequestDto(input))
                    .retrieveMono(ComputationResponseDto.class)
                    .doOnNext(dto -> log.debug("client receives {}", dto))
                    .doFinally(s -> log.debug("{}", s));

            //then
            StepVerifier.create(mono)
                    .verifyErrorSatisfies(ex -> assertThat(ex)
                            .isInstanceOf(RejectedSetupException.class)
                            .hasMessage("You have no permission to use service"));
        }
    }

}
