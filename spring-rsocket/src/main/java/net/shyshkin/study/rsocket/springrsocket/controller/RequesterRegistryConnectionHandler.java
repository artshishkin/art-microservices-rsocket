package net.shyshkin.study.rsocket.springrsocket.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.service.MathClientManager;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RequesterRegistryConnectionHandler {

    private final MathClientManager mathClientManager;

//    @ConnectMapping
//    public Mono<Void> noEventConnection(RSocketRequester requester) {
//        log.debug("No event connection Setup for {}", requester);
//        return Mono.empty();
//    }

    @ConnectMapping("math.events.connection")
    public Mono<Void> mathEventConnection(RSocketRequester requester) {
        log.debug("Math event connection Setup for {}", requester);
        return Mono.fromRunnable(() -> mathClientManager.add(requester));
    }
}
