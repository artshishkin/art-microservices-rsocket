package net.shyshkin.study.rsocket.userservice;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.userservice.dto.TransactionRequest;
import net.shyshkin.study.rsocket.userservice.dto.TransactionResponse;
import net.shyshkin.study.rsocket.userservice.dto.TransactionStatus;
import net.shyshkin.study.rsocket.userservice.dto.TransactionType;
import net.shyshkin.study.rsocket.userservice.entity.User;
import net.shyshkin.study.rsocket.userservice.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static net.shyshkin.study.rsocket.userservice.dto.TransactionType.CREDIT;
import static net.shyshkin.study.rsocket.userservice.dto.TransactionType.DEBIT;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@ActiveProfiles("my-transactions-service")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties ={
        "spring.rsocket.server.port=7070"
})
class MyTransactionsTest {

    RSocketRequester requester;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RSocketRequester.Builder builder;

    @BeforeAll
    void beforeAll() {
        requester = builder.tcp("localhost", 7070);
    }

    @ParameterizedTest
    @MethodSource("transactionTypeSource")
    void transaction_success(TransactionType type) throws InterruptedException {

        //given
        User user = randomUser();
        String userId = user.getId();
        int initialBalance = user.getBalance();
        int amount = ThreadLocalRandom.current().nextInt(initialBalance);
        int expectedFinalBalance = CREDIT.equals(type) ?
                (initialBalance + amount) :
                (initialBalance - amount);
        TransactionRequest request = TransactionRequest.builder()
                .userId(userId)
                .amount(amount)
                .type(type)
                .build();

        //when
        Mono<TransactionResponse> mono = requester.route("user.transaction")
                .data(request)
                .retrieveMono(TransactionResponse.class)
                .doOnNext(dto -> log.debug("{}", dto));

        //then
        StepVerifier.create(mono)
                .assertNext(response -> assertThat(response)
                        .isEqualToIgnoringGivenFields(request, "status")
                        .hasFieldOrPropertyWithValue("status", TransactionStatus.COMPLETED)
                )
                .verifyComplete();

        Thread.sleep(200);
        StepVerifier.create(userRepository.findById(userId).map(User::getBalance))
                .expectNext(expectedFinalBalance)
                .verifyComplete();
    }

    Stream<TransactionType> transactionTypeSource() {
        return Stream.of(CREDIT, DEBIT);
    }


    private User randomUser() {
        return userRepository.findAll()
                .next()
                .block();
    }

    @ParameterizedTest
    @MethodSource("transactionTypeSource")
    void transaction_userAbsent(TransactionType type) {

        //given
        String userId = UUID.randomUUID().toString();
        int amount = 1;
        TransactionRequest request = TransactionRequest.builder()
                .userId(userId)
                .amount(amount)
                .type(type)
                .build();

        //when
        Mono<TransactionResponse> mono = requester.route("user.transaction")
                .data(request)
                .retrieveMono(TransactionResponse.class)
                .doOnNext(dto -> log.debug("{}", dto));

        //then
        StepVerifier.create(mono)
                .assertNext(response -> assertThat(response)
                        .isEqualToIgnoringGivenFields(request, "status")
                        .hasFieldOrPropertyWithValue("status", TransactionStatus.FAILED)
                )
                .verifyComplete();
    }

    @Test
    void transaction_amountExceeded() throws InterruptedException {

        //given
        User user = randomUser();
        String userId = user.getId();
        TransactionType type = DEBIT;
        int initialBalance = user.getBalance();
        int amount = initialBalance + 100;
        int expectedFinalBalance = initialBalance;

        TransactionRequest request = TransactionRequest.builder()
                .userId(userId)
                .amount(amount)
                .type(type)
                .build();

        //when
        Mono<TransactionResponse> mono = requester.route("user.transaction")
                .data(request)
                .retrieveMono(TransactionResponse.class)
                .doOnNext(dto -> log.debug("{}", dto));

        //then
        StepVerifier.create(mono)
                .assertNext(response -> assertThat(response)
                        .isEqualToIgnoringGivenFields(request, "status")
                        .hasFieldOrPropertyWithValue("status", TransactionStatus.FAILED)
                )
                .verifyComplete();

        Thread.sleep(200);
        StepVerifier.create(userRepository.findById(userId).map(User::getBalance))
                .expectNext(expectedFinalBalance)
                .verifyComplete();
    }
}


