package net.shyshkin.study.rsocket.springrsocket.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MathVariableControllerTest {

    @Autowired
    RSocketRequester.Builder builder;

    RSocketRequester requester;

    @BeforeAll
    void beforeAll() {
        requester = builder.tcp("localhost", 6565);
    }

    @Test
    void print() {
        //when
        Mono<Void> mono = requester.route("math.service.print.123")
                .send();

        //then
        StepVerifier.create(mono)
                .verifyComplete();
    }
}