package net.shyshkin.study.rsocket.springrsocket.assignment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameController {

    private final GameService service;

    @MessageMapping("game.play")
    public Flux<GameResponse> playGame(Flux<Integer> guessFlux) {
        return service.playGame(guessFlux);
    }
}
