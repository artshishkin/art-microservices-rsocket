package net.shyshkin.study.rsocket.springrsocket;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ChartResponseDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@WithMockUser
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

    @Test
    void requestResponse() {
        //given
        int input = 12;

        //when
        Mono<ComputationResponseDto> mono = requester.route("math.service.find_square")
                .data(new ComputationRequestDto(input))
                .retrieveMono(ComputationResponseDto.class)
                .doOnNext(dto -> log.debug("{}", dto));

        //then
        StepVerifier.create(mono)
                .assertNext(dto -> assertThat(dto)
                        .hasFieldOrPropertyWithValue("input", input)
                        .hasFieldOrPropertyWithValue("output", input * input)
                )
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {"math.service.table", "math.service.table_mono", "math.service.table_convert"})
    void requestStream(String route) {
        //given
        int input = 12;

        //when
        Flux<ComputationResponseDto> flux = requester.route(route)
                .data(new ComputationRequestDto(input))
                .retrieveFlux(ComputationResponseDto.class)
                .doOnNext(dto -> log.debug("{}", dto));

        //then
        StepVerifier.create(flux)
                .recordWith(ArrayList::new)
                .expectNextCount(10)
                .consumeRecordedWith(list -> {
                    for (int i = 0; i < list.size(); i++) {
                        assertThat(((ArrayList<ComputationResponseDto>) list).get(i))
                                .hasFieldOrPropertyWithValue("input", input)
                                .hasFieldOrPropertyWithValue("output", (i + 1) * input);
                    }
                })
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {"math.service.chart"})
    void requestChannel(String route) {
        //given
        int totalCount = 21;
        Flux<ComputationRequestDto> inputFlux = Flux.range(-10, totalCount)
                .map(ComputationRequestDto::new);

        //when
        Flux<ChartResponseDto> flux = requester.route(route)
                .data(inputFlux, ComputationRequestDto.class)
                .retrieveFlux(ChartResponseDto.class)
                .delayElements(Duration.ofMillis(100))
                .doOnNext(dto -> System.out.println("client: " + dto));

        //then
        StepVerifier.create(flux)
                .recordWith(ArrayList::new)
                .expectNextCount(totalCount)
                .consumeRecordedWith(list -> assertThat(list)
                        .allSatisfy(dto -> assertThat(dto.getOutput())
                                .isEqualTo(dto.getInput() * dto.getInput() + 1)))
                .verifyComplete();
    }
}
