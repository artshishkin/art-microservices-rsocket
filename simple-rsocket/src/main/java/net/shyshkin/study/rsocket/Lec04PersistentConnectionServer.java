package net.shyshkin.study.rsocket;

import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import net.shyshkin.study.rsocket.service.FastProducerService;
import reactor.core.publisher.Mono;

public class Lec04PersistentConnectionServer {

    public static void main(String[] args) {
        RSocketServer rSocketServer = RSocketServer.create((setup, sendingSocket) -> Mono.just(new FastProducerService()));
        CloseableChannel closeableChannel = rSocketServer.bindNow(TcpServerTransport.create(6565));

        //keep listening
        closeableChannel.onClose().block();
    }
}
