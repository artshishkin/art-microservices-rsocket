package net.shyshkin.study.rsocket.service;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.util.DefaultPayload;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class FastProducerService implements RSocket {

    @Override
    public Flux<Payload> requestStream(Payload payload) {
        return Flux.range(1, 100)
                .map(i -> i + "")
                .doOnNext(i -> log.debug("server: emitting {}", i))
                .doFinally(s -> log.debug("server finished emitting: {}", s))
                .map(DefaultPayload::create);
    }
}
