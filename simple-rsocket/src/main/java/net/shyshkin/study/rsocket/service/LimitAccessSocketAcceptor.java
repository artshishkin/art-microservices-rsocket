package net.shyshkin.study.rsocket.service;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class LimitAccessSocketAcceptor implements SocketAcceptor {
    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
        log.debug("accept method");
        String setupData = setup.getDataUtf8();
        return Mono.just(isValidClient(setupData) ? new MathService() : new FreeMathService());
    }

    private boolean isValidClient(String credentials) {
        return "user:password".equals(credentials);
    }
}
