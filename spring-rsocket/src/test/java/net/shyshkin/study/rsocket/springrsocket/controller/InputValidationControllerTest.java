package net.shyshkin.study.rsocket.springrsocket.controller;

import io.rsocket.exceptions.ApplicationErrorException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("secure")
class InputValidationControllerTest {

    @Autowired
    RSocketRequester.Builder builder;

    RSocketRequester requester;

    @BeforeAll
    void beforeAll() {
        requester = builder.tcp("localhost", 6565);
    }

    @Nested
    class SimpleError {

        @Test
        void doubleIt_valid() {
            //given
            int input = 12;

            //when
            Mono<Integer> mono = requester.route("math.validation.double.{input}", input)
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
            Mono<Integer> mono = requester.route("math.validation.double.{input}", input)
                    .retrieveMono(Integer.class)
                    .doOnNext(result -> log.debug("result: {}", result))
                    .doFinally(signal -> log.debug("{}", signal));

            //then
            StepVerifier.create(mono)
                    .expectErrorSatisfies(ex -> assertThat(ex)
                            .isInstanceOf(ApplicationErrorException.class)
                            .hasMessage("can not be > 30")
                    )
                    .verify();
        }
    }

    @Nested
    @WithUserDetails("client01")
    class EmptyReturn {

        @Test
        void doubleIt_valid() {
            //given
            int input = 12;

            //when
            Mono<Integer> mono = requester.route("math.validation.double_empty.{input}", input)
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
            Mono<Integer> mono = requester.route("math.validation.double_empty.{input}", input)
                    .retrieveMono(Integer.class)
                    .doOnNext(result -> log.debug("result: {}", result))
                    .doFinally(signal -> log.debug("{}", signal));

            //then
            StepVerifier.create(mono)
                    .verifyComplete();
        }
    }

    @Nested
    @WithUserDetails("client01")
    class DefaultIfEmptyReturn {

        @Test
        void doubleIt_valid() {
            //given
            int input = 12;

            //when
            Mono<Integer> mono = requester.route("math.validation.double_empty_default.{input}", input)
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
            Mono<Integer> mono = requester.route("math.validation.double_empty_default.{input}", input)
                    .retrieveMono(Integer.class)
                    .doOnNext(result -> log.debug("result: {}", result))
                    .doFinally(signal -> log.debug("{}", signal));

            //then
            StepVerifier.create(mono)
                    .expectNext(Integer.MIN_VALUE)
                    .verifyComplete();
        }
    }

    @Nested
    @WithUserDetails("client01")
    class SwitchIfEmptyError {

        @Test
        void doubleIt_valid() {
            //given
            int input = 12;

            //when
            Mono<Integer> mono = requester.route("math.validation.double_empty_switch_error.{input}", input)
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
            Mono<Integer> mono = requester.route("math.validation.double_empty_switch_error.{input}", input)
                    .retrieveMono(Integer.class)
                    .doOnNext(result -> log.debug("result: {}", result))
                    .doFinally(signal -> log.debug("{}", signal));

            //then
            StepVerifier.create(mono)
                    .expectErrorSatisfies(ex -> assertThat(ex)
                            .isInstanceOf(ApplicationErrorException.class)
                            .hasMessage("can not be > 30")
                    )
                    .verify();
        }

        @Test
        void doubleIt_invalid_onErrorReturn() {
            //given
            int input = 42;

            //when
            Mono<Integer> mono = requester.route("math.validation.double_empty_switch_error.{input}", input)
                    .retrieveMono(Integer.class)
                    .onErrorReturn(Integer.MIN_VALUE)
                    .doOnNext(result -> log.debug("result: {}", result))
                    .doFinally(signal -> log.debug("{}", signal));

            //then
            StepVerifier.create(mono)
                    .expectNext(Integer.MIN_VALUE)
                    .verifyComplete();
        }



    }

}