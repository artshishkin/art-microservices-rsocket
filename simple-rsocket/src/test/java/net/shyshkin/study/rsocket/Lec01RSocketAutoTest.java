package net.shyshkin.study.rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketConnector;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import net.shyshkin.study.rsocket.dto.RequestDto;
import net.shyshkin.study.rsocket.dto.ResponseDto;
import net.shyshkin.study.rsocket.service.SocketAcceptorImpl;
import net.shyshkin.study.rsocket.util.ObjectUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void requestResponse() {
        //given
        int input = 12;
        Payload payload = ObjectUtil.toPayload(RequestDto.builder().input(input).build());

        //when
        Mono<Payload> requestResponse = rSocket.requestResponse(payload);

        //then
        StepVerifier.create(requestResponse)
                .assertNext(responsePayload ->
                        assertEquals(
                                input * input,
                                ObjectUtil.toObject(responsePayload, ResponseDto.class).getOutput()
                        )
                )
                .verifyComplete();
    }

    @Test
    void requestStream() {
        //given
        int input = 8;
        Payload payload = ObjectUtil.toPayload(RequestDto.builder().input(input).build());

        //when
        Flux<Payload> requestResponse = rSocket.requestStream(payload);

        //then
        Flux<ResponseDto> dtoFlux = requestResponse
                .map(p -> ObjectUtil.toObject(p, ResponseDto.class))
                .doOnNext(System.out::println);

        StepVerifier.create(dtoFlux)
                .assertNext(dto ->
                        assertEquals(input * 1, dto.getOutput())
                )
                .assertNext(dto ->
                        assertEquals(input * 2, dto.getOutput())
                )
                .expectNextCount(7)
                .expectNext(ResponseDto.builder().input(input).output(10 * input).build())
                .verifyComplete();
    }

    @Test
    void requestStream_take4() {
        //given
        int input = 8;
        Payload payload = ObjectUtil.toPayload(RequestDto.builder().input(input).build());

        //when
        Flux<Payload> requestResponse = rSocket.requestStream(payload);

        //then
        Flux<ResponseDto> dtoFlux = requestResponse
                .map(p -> ObjectUtil.toObject(p, ResponseDto.class))
                .doOnNext(dto -> System.out.println("client: " + dto))
                .take(4);

        StepVerifier.create(dtoFlux)
                .expectNextCount(4)
                .verifyComplete();
    }
}