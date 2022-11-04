package net.shyshkin.study.rsocket.springrsocket.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("secure")
class BatchJobControllerTest {

    RSocketRequester requester;

    @Autowired
    RSocketRequester.Builder builder;

    @Autowired
    RSocketMessageHandler handler;

    @BeforeAll
    void beforeAll() {
        requester = builder
                .rsocketConnector(connector -> connector.acceptor(handler.responder()))
                .tcp("localhost", 6565);
    }

    @Test
    void batchJob_callbackTest() throws InterruptedException {
        //given
        int input = 4;

        //when
        Mono<Void> mono = requester.route("batch.job.request")
                .data(new ComputationRequestDto(input))
                .send();

        //then
        StepVerifier.create(mono)
                .verifyComplete();

        log.debug("going to sleep");
        Thread.sleep(3000);
        log.debug("woke up");
    }
}