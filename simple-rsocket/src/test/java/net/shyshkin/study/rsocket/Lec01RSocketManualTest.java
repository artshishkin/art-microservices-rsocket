package net.shyshkin.study.rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.transport.netty.client.TcpClientTransport;
import net.shyshkin.study.rsocket.dto.RequestDto;
import net.shyshkin.study.rsocket.util.ObjectUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Disabled("Only for manual Testing")
@DisplayName("Lec01RSocketManualTest - Start Server first")
class Lec01RSocketManualTest {

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
        Payload payload = ObjectUtil.toPayload(RequestDto.builder().input(123).build());

        //when
        Mono<Void> fireAndForget = rSocket.fireAndForget(payload);

        //then
        StepVerifier.create(fireAndForget)
                .verifyComplete();

    }
}