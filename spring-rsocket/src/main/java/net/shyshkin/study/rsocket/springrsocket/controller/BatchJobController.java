package net.shyshkin.study.rsocket.springrsocket.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Controller
@MessageMapping("batch.job")
public class BatchJobController {

    @MessageMapping("request")
    public Mono<Void> batchJob(Mono<ComputationRequestDto> requestDtoMono, RSocketRequester requester) {
        findCube(requestDtoMono, requester);
        return Mono.empty();
    }

    private void findCube(Mono<ComputationRequestDto> requestDtoMono, RSocketRequester requester) {

        requestDtoMono
                .doOnNext(dto -> log.debug("server: received {}", dto))
                .delayElement(Duration.ofSeconds(2))
                .map(ComputationRequestDto::getInput)
                .map(i -> new ComputationResponseDto(i, i * i * i))
                .doOnNext(resp -> log.debug("server: emitting {}", resp))
                .flatMap(resp -> requester
                        .route("batch.job.response")
                        .data(resp)
                        .send())
                .subscribe();
    }

}
