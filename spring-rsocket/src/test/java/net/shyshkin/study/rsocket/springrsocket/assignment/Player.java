package net.shyshkin.study.rsocket.springrsocket.assignment;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

@Slf4j
public class Player {

    private final Sinks.Many<Integer> sink = Sinks.many().unicast().onBackpressureBuffer();
    private int lower = 0;
    private int upper = 100;
    private int mid = 0;
    private int attempts = 0;

    public Flux<Integer> guesses() {
        return sink.asFlux();
    }

    public Consumer<GameResponse> receives() {
        return this::processResponse;
    }

    public void play() {
        emit();
    }

    private void processResponse(GameResponse gameResponse) {

        switch (gameResponse) {
            case EQUAL:
                sink.tryEmitComplete();
                return;
            case LESS:
                lower = mid;
                break;
            case MORE:
                upper = mid;
                break;
        }

        attempts++;
        log.debug("{} : {} : {}", attempts, mid, gameResponse);
        emit();
    }

    private void emit() {
        mid = (lower + upper) / 2;
        //emit
        sink.tryEmitNext(mid);
    }

}
