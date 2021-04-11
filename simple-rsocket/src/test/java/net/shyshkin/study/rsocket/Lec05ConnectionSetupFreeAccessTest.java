package net.shyshkin.study.rsocket;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.core.RSocketClient;
import io.rsocket.core.RSocketConnector;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.CloseableChannel;
import io.rsocket.transport.netty.server.TcpServerTransport;
import net.shyshkin.study.rsocket.dto.ChartResponseDto;
import net.shyshkin.study.rsocket.dto.RequestDto;
import net.shyshkin.study.rsocket.dto.ResponseDto;
import net.shyshkin.study.rsocket.service.LimitAccessSocketAcceptor;
import net.shyshkin.study.rsocket.util.ObjectUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Lec05ConnectionSetupFreeAccessTest {

    public static final int SERVER_COUNT_LIMIT = 3;
    private static RSocketClient rSocketClient;
    private static CloseableChannel closeableChannel;

    @BeforeAll
    static void beforeAll() {

        RSocketServer rSocketServer = RSocketServer.create(new LimitAccessSocketAcceptor());
        closeableChannel = rSocketServer.bindNow(TcpServerTransport.create(6565));

        Mono<RSocket> rSocketMono = RSocketConnector.create()
                .connect(TcpClientTransport.create(6565));
        rSocketClient = RSocketClient.from(rSocketMono);
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
        Mono<Void> fireAndForget = rSocketClient.fireAndForget(Mono.just(payload));

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
        Mono<Payload> requestResponse = rSocketClient.requestResponse(Mono.just(payload));

        //then
        StepVerifier.create(requestResponse)
                .verifyComplete();
    }

    @Test
    void requestStream() {
        //given
        int input = 8;
        Payload payload = ObjectUtil.toPayload(RequestDto.builder().input(input).build());

        //when
        Flux<Payload> requestResponse = rSocketClient.requestStream(Mono.just(payload));

        //then
        Flux<ResponseDto> dtoFlux = requestResponse
                .map(p -> ObjectUtil.toObject(p, ResponseDto.class))
                .doOnNext(System.out::println);

        StepVerifier.create(dtoFlux)
                .expectNextCount(SERVER_COUNT_LIMIT)
                .verifyComplete();
    }


    @Test
    void requestChannel() {
        //given
        int totalCount = 21;

        Flux<Payload> inputFlux = Flux.range(-10, totalCount)
                .map(RequestDto::new)
                .map(ObjectUtil::toPayload);

        //when
        Flux<Payload> requestChannel = rSocketClient.requestChannel(inputFlux);

        //then
        Flux<ChartResponseDto> dtoFlux = requestChannel
                .map(p -> ObjectUtil.toObject(p, ChartResponseDto.class))
                .delayElements(Duration.ofMillis(100))
                .doOnNext(dto -> System.out.println("client: " + dto));

        StepVerifier.create(dtoFlux)
                .recordWith(ArrayList::new)
                .thenConsumeWhile(dto -> true, dto -> assertEquals(dto.getInput() * dto.getInput() + 1, dto.getOutput()))
                .consumeRecordedWith(dtoList -> assertEquals(SERVER_COUNT_LIMIT, dtoList.size()))
                .expectNextCount(SERVER_COUNT_LIMIT)
                .verifyComplete();
    }
}