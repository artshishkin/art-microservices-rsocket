package net.shyshkin.study.rsocket;

import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.service.FastProducerService;
import reactor.core.publisher.Mono;

@Slf4j
public class Lec04PersistentConnectionServer {

    public static void main(String[] args) {
        RSocketServer rSocketServer = RSocketServer.create((setup, sendingSocket) -> Mono.just(new FastProducerService()));
        CloseableChannel closeableChannel = rSocketServer.bindNow(TcpServerTransport.create(6565));
        log.debug("Server started successfully: {}", rSocketServer);
        //keep listening
        closeableChannel.onClose().block();
    }
}
