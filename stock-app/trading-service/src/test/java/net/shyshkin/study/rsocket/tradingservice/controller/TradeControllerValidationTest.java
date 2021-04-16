package net.shyshkin.study.rsocket.tradingservice.controller;

import net.shyshkin.study.rsocket.tradingservice.dto.StockTradeRequest;
import net.shyshkin.study.rsocket.tradingservice.dto.StockTradeResponse;
import net.shyshkin.study.rsocket.tradingservice.dto.TradeStatus;
import net.shyshkin.study.rsocket.tradingservice.dto.TradeType;
import net.shyshkin.study.rsocket.tradingservice.mapper.DtoMapper;
import net.shyshkin.study.rsocket.tradingservice.service.TradeService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Argument validation test for Trade Controller")
class TradeControllerValidationTest {

    @Autowired
    TradeController tradeController;

    @MockBean
    TradeService tradeService;

    @Autowired
    DtoMapper dtoMapper;

    private WebTestClient webClient;

    @BeforeAll
    void beforeAll() {
        webClient = WebTestClient.bindToController(tradeController).build();
    }

    @ParameterizedTest(name="[{index}] {4}")
    @CsvSource({
            "low,12,AMZN,BUY,Too short user ID",
            "normalLength,-12,AMZN,BUY,Negative quantity to buy",
            "normalLength,12,TOO_LONG_CODE,BUY,Too long stock code",
    })
    @DisplayName("Requests with invalid arguments must be rejected with 400 BAD_REQUEST status code")
    void trade_invalidArguments(String userId, int quantity, String stockCode, TradeType type, String testDescription) {

        //given
        StockTradeRequest tradeRequest = StockTradeRequest.builder()
                .userId(userId)
                .quantity(quantity)
                .stockCode(stockCode)
                .tradeType(type)
                .build();

        //when
        webClient.post().uri("/stock/trade")
                .body(Mono.just(tradeRequest), StockTradeRequest.class)
                .exchange()

                //then
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Valid requests should be processed")
    void trade_validArguments() {

        //given
        StockTradeRequest tradeRequest = StockTradeRequest.builder()
                .userId("123456")
                .quantity(1)
                .stockCode("AMZN")
                .tradeType(TradeType.BUY)
                .build();

        StockTradeResponse tradeResponse = dtoMapper.toStockTradeResponse(tradeRequest, TradeStatus.COMPLETED, 123);

        given(tradeService.trade(any(StockTradeRequest.class))).willReturn(Mono.just(tradeResponse));

        //when
        webClient.post().uri("/stock/trade")
                .body(Mono.just(tradeRequest), StockTradeRequest.class)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(StockTradeResponse.class)
                .isEqualTo(tradeResponse);
    }
}