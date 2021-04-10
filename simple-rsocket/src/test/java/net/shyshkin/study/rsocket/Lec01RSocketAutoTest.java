package net.shyshkin.study.rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import net.shyshkin.study.rsocket.dto.RequestDto;
import net.shyshkin.study.rsocket.service.SocketAcceptorImpl;
import net.shyshkin.study.rsocket.util.ObjectUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class Lec01RSocketAutoTest {

    private static RSocket rSocket;
    private static CloseableChannel closeableChannel;

    @BeforeAll
    static void beforeAll() {

        RSocketServer rSocketServer = RSocketServer.create(new SocketAcceptorImpl());
        closeableChannel = rSocketServer.bindNow(TcpServerTransport.create(6565));

        rSocket = RSocketConnector.create()
                .connect(TcpClientTransport.create(6565))
                .block();
    }

    @AfterAll
    static void afterAll() {
        closeableChannel.dispose();
    }

    @Test
    void fireAndForget() throws InterruptedException {
        //given
        Payload payload = ObjectUtil.toPayload(RequestDto.builder().input(123).build());

        //when
        Mono<Void> fireAndForget = rSocket.fireAndForget(payload);

        //then
        StepVerifier.create(fireAndForget)
                .verifyComplete();

        Thread.sleep(100);
    }
}