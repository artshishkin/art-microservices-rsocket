package net.shyshkin.study.rsocket.springrsocket;

import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Lec01RSocketTest {

    RSocketRequester requester;

    @Autowired
    RSocketRequester.Builder builder;

    @BeforeAll
    void setUp() {
        requester = builder.tcp("localhost", 6565);
//        requester = builder.transport(TcpClientTransport.create("localhost", 6565));
    }

    @Test
    void fireAndForgetTest() {
        //given
        int input = 6;

        //when
        Mono<Void> voidMono = requester.route("math.service.print")
                .data(new ComputationRequestDto(input))
                .send();

        //then
        StepVerifier
                .create(voidMono)
                .verifyComplete();
    }
}
