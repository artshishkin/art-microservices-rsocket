package net.shyshkin.study.rsocket.springrsocket.security;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import net.shyshkin.study.rsocket.springrsocket.service.MathService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@MessageMapping("math.service.secured")
@RequiredArgsConstructor
public class SecuredMathController {

    private final MathService service;

    @MessageMapping("square")
    public Mono<ComputationResponseDto> findSquare(Mono<ComputationRequestDto> requestDtoMono) {
        return service.findSquare(requestDtoMono);
    }

    @MessageMapping("table")
    public Flux<ComputationResponseDto> tableStreamUsingMono(Mono<ComputationRequestDto> requestDtoMono) {
        return service.tableStreamUsingMono(requestDtoMono);
    }
}
