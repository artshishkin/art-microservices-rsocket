package net.shyshkin.study.rsocket.springrsocket.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExceptionTestControllerTest {

    @Autowired
    RSocketRequester.Builder builder;

    RSocketRequester requester;

    @BeforeAll
    void beforeAll() {
        requester = builder.tcp("localhost", 6565);
    }

    @Nested
    class ExceptionHandling {

        @Test
        void doubleIt_valid() {
            //given
            int input = 12;

            //when
            Mono<Integer> mono = requester.route("math.exception.double.{input}", input)
                    .retrieveMono(Integer.class)
                    .doOnNext(result -> log.debug("result: {}", result))
                    .doFinally(signal -> log.debug("{}", signal));

            //then
            StepVerifier.create(mono)
                    .expectNext(2 * input)
                    .verifyComplete();
        }

        @Test
        void doubleIt_invalid() {
            //given
            int input = 42;

            //when
            Mono<Integer> mono = requester.route("math.exception.double.{input}", input)
                    .retrieveMono(Integer.class)
                    .doOnNext(result -> log.debug("result: {}", result))
                    .doFinally(signal -> log.debug("{}", signal));

            //then
            StepVerifier.create(mono)
                    .expectNext(-1)
                    .verifyComplete();
        }
    }
}