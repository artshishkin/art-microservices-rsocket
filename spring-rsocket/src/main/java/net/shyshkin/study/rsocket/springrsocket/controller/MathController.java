package net.shyshkin.study.rsocket.springrsocket.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.rsocket.springrsocket.dto.ChartResponseDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import net.shyshkin.study.rsocket.springrsocket.service.MathService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class MathController {

    private final MathService service;

    @MessageMapping("math.service.print")
    public Mono<Void> print(Mono<ComputationRequestDto> requestDtoMono) {
        return service.print(requestDtoMono);
    }

    @MessageMapping("math.service.find_square")
    public Mono<ComputationResponseDto> findSquare(Mono<ComputationRequestDto> requestDtoMono) {
        return service.findSquare(requestDtoMono);
    }

    @MessageMapping("math.service.table")
    public Flux<ComputationResponseDto> tableStream(ComputationRequestDto requestDto) {
        return service.tableStream(requestDto);
    }

    @MessageMapping("math.service.table_mono")
    public Flux<ComputationResponseDto> tableStreamUsingMono(Mono<ComputationRequestDto> requestDtoMono) {
        return service.tableStreamUsingMono(requestDtoMono);
    }

    @MessageMapping("math.service.table_convert")
    public Flux<ComputationResponseDto> tableStream(Mono<ComputationRequestDto> requestDtoMono) {
        return requestDtoMono.flatMapMany(service::tableStream);
    }

    @MessageMapping("math.service.chart")
    public Flux<ChartResponseDto> chartStream(Flux<ComputationRequestDto> requestDtoFlux) {
        return service.chartStream(requestDtoFlux);
    }
}
