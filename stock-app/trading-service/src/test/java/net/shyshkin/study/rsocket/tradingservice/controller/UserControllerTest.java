package net.shyshkin.study.rsocket.tradingservice.controller;

import net.shyshkin.study.rsocket.tradingservice.dto.UserStockDto;
import net.shyshkin.study.rsocket.tradingservice.entity.UserStock;
import net.shyshkin.study.rsocket.tradingservice.repository.UserStockRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {

    @Autowired
    UserStockRepository repository;

    @Autowired
    UserController userController;

    WebTestClient webClient;

    @BeforeAll
    void beforeAll() {
        webClient = WebTestClient.bindToController(userController).build();
    }

    @Test
    void getAllStocksOfUser() throws InterruptedException {
        //given
        UserStock sample = UserStock.builder().quantity(123).stockCode("AMZN").userId("foo").build();
        repository.save(sample).block();

        //when
        webClient.get().uri("/users/{userId}/stocks", "foo")
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBodyList(UserStockDto.class)
                .hasSize(1)
                .value(dtoList -> assertThat(dtoList.get(0))
                        .hasFieldOrPropertyWithValue("quantity", 123)
                        .hasFieldOrPropertyWithValue("stockCode", "AMZN")
                        .hasFieldOrPropertyWithValue("userId", "foo")
                );
    }
}