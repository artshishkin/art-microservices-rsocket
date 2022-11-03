package net.shyshkin.study.rsocket;

import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.service.LimitAccessSocketAcceptor;

@Slf4j
public class Lec05ConnectionSetupServer {

    public static void main(String[] args) {
        RSocketServer rSocketServer = RSocketServer.create(new LimitAccessSocketAcceptor());
        CloseableChannel closeableChannel = rSocketServer.bindNow(TcpServerTransport.create(6565));
        log.debug("Server started successfully: {}", rSocketServer);
        //keep listening
        closeableChannel.onClose().block();
    }
}
