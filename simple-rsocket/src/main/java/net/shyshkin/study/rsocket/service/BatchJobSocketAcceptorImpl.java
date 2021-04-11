package net.shyshkin.study.rsocket.service;

import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import reactor.core.publisher.Mono;

public class BatchJobSocketAcceptorImpl implements SocketAcceptor {
    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
        System.out.println("BatchJobSocketAcceptorImpl - accept method");
        return Mono.fromCallable(() -> new BatchJobService(sendingSocket));
    }
}
