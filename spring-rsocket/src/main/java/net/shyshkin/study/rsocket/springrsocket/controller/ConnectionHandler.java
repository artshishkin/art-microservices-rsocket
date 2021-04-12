package net.shyshkin.study.rsocket.springrsocket.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ClientConnectionRequest;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class ConnectionHandler {

    @ConnectMapping
    public Mono<Void> handleConnection(ClientConnectionRequest request) {
        log.debug("Connection Setup with {}", request);

        return validate(request.getClientId(), request.getSecretKey()) ?
                Mono.empty() : Mono.error(() -> new RuntimeException("You have no permission to use service"));
    }

    private boolean validate(String clientId, String secretKey) {
        return "mySecretKey".equals(secretKey);
    }
}
