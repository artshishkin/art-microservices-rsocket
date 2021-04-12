package net.shyshkin.study.rsocket.springrsocket.client.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
public class BatchJobResponseController {

    @MessageMapping("batch.job.response")
    public Mono<Void> batchJobResponse(Mono<ComputationResponseDto> responseDtoMono) {
        return responseDtoMono
                .doOnNext(dto -> log.debug("client: received {}", dto))
                .then();
    }

}
