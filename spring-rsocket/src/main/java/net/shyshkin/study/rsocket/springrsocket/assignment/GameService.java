package net.shyshkin.study.rsocket.springrsocket.assignment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class GameService {

    public Flux<GameResponse> playGame(Flux<Integer> guessFlux) {
        int serverNumber = ThreadLocalRandom.current().nextInt(1, 1_000_000);
        log.debug("Server number is {}", serverNumber);
        return guessFlux
                .doOnNext(i -> log.debug("server: received {}", i))
                .map(i -> compare(serverNumber, i))
                .doOnNext(i -> log.debug("server: sent {}", i));
    }

    private GameResponse compare(int serverNumber, int clientNumber) {
        int compare = Integer.compare(serverNumber, clientNumber);
        return compare == 0 ? GameResponse.EQUAL :
                compare > 0 ? GameResponse.MORE :
                        GameResponse.LESS;
    }
}