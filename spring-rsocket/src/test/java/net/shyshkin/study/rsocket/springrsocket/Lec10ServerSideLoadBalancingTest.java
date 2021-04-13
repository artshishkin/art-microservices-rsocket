package net.shyshkin.study.rsocket.springrsocket;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Manually start 3 instances of a Server (ports 6563,6564,6565) and docker-compose-lb then run test")
@Disabled("Only for manual testing")
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration"
})
public class Lec10ServerSideLoadBalancingTest {

    @Autowired
    RSocketRequester.Builder builder;

    @Autowired
    RSocketMessageHandler handler;

    @Test
    void loadBalancingTest() throws InterruptedException {
        //given
        RSocketRequester requester1 = builder
                .rsocketConnector(connector -> connector.acceptor(handler.responder()))
                .tcp("localhost", 6566);
        RSocketRequester requester2 = builder
                .rsocketConnector(connector -> connector.acceptor(handler.responder()))
                .tcp("localhost", 6566);

        //when
        for (int i = 0; i < 50; i++) {
            requester1.route("math.service.print").data(i).send().subscribe();
            requester2.route("math.service.print").data(i).send().subscribe();
            Thread.sleep(2000);
        }

        //then
    }
}

