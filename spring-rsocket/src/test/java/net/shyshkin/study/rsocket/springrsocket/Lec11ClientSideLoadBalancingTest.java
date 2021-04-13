package net.shyshkin.study.rsocket.springrsocket;

import io.rsocket.loadbalance.LoadbalanceStrategy;
import io.rsocket.loadbalance.LoadbalanceTarget;
import io.rsocket.loadbalance.RoundRobinLoadbalanceStrategy;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Manually start 3 instances of a Server (ports 6563,6564,6565) then run test")
@Disabled("Only for manual testing")
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration"
})
public class Lec11ClientSideLoadBalancingTest {

    @Autowired
    RSocketRequester.Builder builder;

    @Autowired
    private Publisher<List<LoadbalanceTarget>> targetPublisher;

    private final LoadbalanceStrategy loadbalanceStrategy = new RoundRobinLoadbalanceStrategy();

    @Test
    void clientSideTest1Client() throws InterruptedException {
        //given
        RSocketRequester requester1 = builder
                .transports(targetPublisher, loadbalanceStrategy);

        //when
        for (int i = 0; i < 50; i++) {
            log.debug("sending {}", i);
            requester1.route("math.service.print").data(i).send().subscribe();
            Thread.sleep(2000);
        }

        //then
    }

    @Test
    void clientSideTest2Clients() throws InterruptedException {
        //given
        RSocketRequester requester1 = builder
                .transports(targetPublisher, loadbalanceStrategy);
        RSocketRequester requester2 = builder
                .transports(targetPublisher, loadbalanceStrategy);

        //when
        for (int i = 0; i < 50; i++) {
            log.debug("sending {}", i);
            requester1.route("math.service.print").data(i).send().subscribe();
            requester2.route("math.service.print").data(i).send().subscribe();
            Thread.sleep(2000);
        }

        //then
    }
}

