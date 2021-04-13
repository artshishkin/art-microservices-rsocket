package net.shyshkin.study.rsocket.springrsocket;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Manually start Server - SpringRsocketApplication then run test")
@Disabled("Only for manual testing")
//@TestPropertySource(properties = {"spring.rsocket.server.port=6564"})
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration"
})
public class Lec07ConnectionManagerTest {

    @Autowired
    RSocketRequester.Builder builder;

    @Autowired
    RSocketMessageHandler handler;

    @Test
    void connectionTest() throws InterruptedException {
        //given
        RSocketRequester requester1 = builder
                .rsocketConnector(connector -> connector.acceptor(handler.responder()))
                .tcp("localhost", 6565);
        RSocketRequester requester2 = builder
                .setupRoute("math.events.connection")
                .rsocketConnector(connector -> connector.acceptor(handler.responder()))
                .tcp("localhost", 6565);

        //when
        requester1.route("math.service.print").data(new ComputationRequestDto(ThreadLocalRandom.current().nextInt(1, 100))).send().subscribe();
        Thread.sleep(2000);
        requester2.route("math.service.print").data(new ComputationRequestDto(ThreadLocalRandom.current().nextInt(1, 100))).send().subscribe();

        //then
        Thread.sleep(7000);
    }
}

