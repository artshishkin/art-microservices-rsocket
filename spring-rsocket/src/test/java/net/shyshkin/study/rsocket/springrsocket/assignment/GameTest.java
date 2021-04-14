package net.shyshkin.study.rsocket.springrsocket.assignment;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.test.context.support.WithUserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@SpringBootTest
@WithUserDetails("client01")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameTest {

    RSocketRequester requester;

    @Autowired
    RSocketRequester.Builder builder;

    @BeforeAll
    void beforeAll() {
        requester = builder.tcp("localhost", 6565);
    }

    @Test
    void playGame() {
        //given
        Player player = new Player();
        Flux<Integer> guessFlux = player.guesses();

        //when
        Flux<GameResponse> gameResponseFlux = requester.route("game.play")
                .data(guessFlux, Integer.class)
                .retrieveFlux(GameResponse.class);
        player.play();

        //then
//        gameResponseFlux.subscribe(player.receives());
        StepVerifier.create(gameResponseFlux)
                .thenConsumeWhile(response -> true, player.receives())
                .verifyComplete();
    }

    @Test
    void playGame_vinothVersion() {
        //given
        Player player = new Player();
        Flux<Integer> guessFlux = player.guesses();

        //when
        Mono<Void> mono = requester.route("game.play")
                .data(guessFlux, Integer.class)
                .retrieveFlux(GameResponse.class)
                .doOnNext(player.receives())
                .doFirst(player::play)
                .then();

        //then
        StepVerifier.create(mono)
                .verifyComplete();
    }
}