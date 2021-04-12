package net.shyshkin.study.rsocket.springrsocket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class ConnectionHandler {

    @ConnectMapping
    public Mono<Void> handleConnection() {
        log.debug("Connection Setup");
        return Mono.empty();
    }
}
