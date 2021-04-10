package net.shyshkin.study.rsocket;

import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class Lec01RSocketTest {

    private static RSocket rSocket;

    @BeforeAll
    static void beforeAll() {
        rSocket = RSocketConnector.create()
                .connect(TcpClientTransport.create(6565))
                .block();
    }

    @Test
    void fireAndForget() {
        //given

        //when

        //then

    }
}