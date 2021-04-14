package net.shyshkin.study.rsocket.springrsocket;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class Lec13SecurityTest {

    @Autowired
    RSocketRequester.Builder builder;

    @Test
    void requestResponse() {
        //given
        int input = 12;
        RSocketRequester requester = builder.tcp("localhost", 6565);

        //when
        Mono<ComputationResponseDto> mono = requester.route("math.service.secured.square")
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
}
