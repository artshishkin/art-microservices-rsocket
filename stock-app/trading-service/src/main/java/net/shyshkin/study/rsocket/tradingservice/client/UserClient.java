package net.shyshkin.study.rsocket.tradingservice.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.tradingservice.dto.TransactionRequest;
import net.shyshkin.study.rsocket.tradingservice.dto.TransactionResponse;
import net.shyshkin.study.rsocket.tradingservice.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserClient {

    private final RSocketRequester requester;

    public UserClient(RSocketRequester.Builder builder,
                      RSocketConnectorConfigurer connectorConfigurer,
                      @Value("${app.clients.user-service.host}") String host,
                      @Value("${app.clients.user-service.port}") int port) {

        this.requester = builder
                .rsocketConnector(connectorConfigurer)
                .tcp(host, port);
    }

    public Mono<TransactionResponse> doTransaction(TransactionRequest request) {
        return requester.route("user.transaction")
                .data(request)
                .retrieveMono(TransactionResponse.class)
                .doOnNext(resp -> log.debug("Received: {}", resp));
    }

    public Flux<UserDto> getAllUsers() {
        return requester.route("user.get.all")
                .retrieveFlux(UserDto.class)
                .doOnNext(user -> log.debug("Get {}", user));
    }
}