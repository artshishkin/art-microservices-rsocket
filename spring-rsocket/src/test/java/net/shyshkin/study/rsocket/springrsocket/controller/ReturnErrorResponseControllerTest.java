package net.shyshkin.study.rsocket.springrsocket.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.Response;
import net.shyshkin.study.rsocket.springrsocket.dto.error.StatusCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("secure")
class ReturnErrorResponseControllerTest {

    @Autowired
    RSocketRequester.Builder builder;

    RSocketRequester requester;

    @BeforeAll
    void beforeAll() {
        requester = builder.tcp("localhost", 6565);
    }

    @Nested
    @WithUserDetails("client01")
    class ReturnErrorEvent {

        @Test
        void doubleIt_valid() {
            //given
            int input = 12;
            ParameterizedTypeReference<Response<Integer>> typeReference = new ParameterizedTypeReference<>() {
            };

            //when
            Mono<Response<Integer>> mono = requester.route("math.response.double.{input}", input)
                    .retrieveMono(typeReference)
                    .doOnNext(result -> log.debug("result: {}", result))
                    .doFinally(signal -> log.debug("{}", signal));

            //then
            StepVerifier.create(mono.map(Response::getSuccessResponse))
                    .expectNext(2 * input)
                    .verifyComplete();
        }

        @Test
        void doubleIt_invalid() {
            //given
            int input = 42;
            ParameterizedTypeReference<Response<Integer>> typeReference = new ParameterizedTypeReference<>() {
            };

            //when
            Mono<Response<Integer>> mono = requester.route("math.response.double.{input}", input)
                    .retrieveMono(typeReference)
                    .doOnNext(result -> log.debug("result: {}", result))
                    .doFinally(signal -> log.debug("{}", signal));

            //then
            StepVerifier.create(mono)
                    .assertNext(response -> assertAll(
                            () -> assertThat(response.hasError()).isTrue(),
                            () -> assertThat(response.getErrorResponse().getDate())
                                    .isNotNull()
                                    .isBefore(LocalDateTime.now()),
                            () -> assertThat(response.getErrorResponse().getStatusCode())
                                    .isEqualTo(StatusCode.EC001)
                            )
                    )
                    .verifyComplete();
        }
    }
}