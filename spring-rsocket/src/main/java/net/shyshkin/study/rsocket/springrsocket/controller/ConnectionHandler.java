package net.shyshkin.study.rsocket.springrsocket.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ClientConnectionRequest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import reactor.core.publisher.Mono;

@Slf4j
//@Controller
public class ConnectionHandler {

    @ConnectMapping
    public Mono<Void> handleConnection(ClientConnectionRequest request, RSocketRequester requester) {
        log.debug("Connection Setup with {}", request);

        return validate(request.getClientId(), request.getSecretKey()) ?
                Mono.empty() : Mono.fromRunnable(() -> requester.rsocketClient().dispose());
    }

    private boolean validate(String clientId, String secretKey) {
        return "mySecretKey".equals(secretKey);
    }
}
