package net.shyshkin.study.rsocket.springrsocket.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.service.MathClientManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RequesterRegistryConnectionHandler {

    private final MathClientManager mathClientManager;

    @Value("${app.register-clients:false}")
    private boolean registerClients;

    @ConnectMapping
    public Mono<Void> handleConnection(RSocketRequester requester) {
        log.debug("Connection Setup for {}", requester);
        return registerClients ?
                Mono.fromRunnable(() -> mathClientManager.add(requester)) :
                Mono.empty();
    }
}
