package net.shyshkin.study.rsocket.stockservice.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.stockservice.dto.StockTickDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StockControllerTest {

    @Autowired
    RSocketRequester.Builder builder;
    private RSocketRequester requester;

    @BeforeAll
    void beforeAll() {
        requester = builder.tcp("localhost", 6565);
    }

    @Test
    void getStockPrice() {
        //given

        //when
        Flux<StockTickDto> flux = requester.route("stock.price")
                .retrieveFlux(StockTickDto.class)
                .doOnNext(dto -> log.debug("{}", dto))
                .take(9);
        //then
        StepVerifier.create(flux)
                .expectNextCount(9)
                .verifyComplete();
    }
}