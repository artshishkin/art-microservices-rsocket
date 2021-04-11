package net.shyshkin.study.rsocket.springrsocket.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ChartResponseDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class MathService {

    // fire-and-forget
    public Mono<Void> print(Mono<ComputationRequestDto> requestDtoMono) {
        return requestDtoMono
                .doOnNext(dto -> log.debug("received {}", dto))
                .then();
    }

    //request-response
    public Mono<ComputationResponseDto> findSquare(Mono<ComputationRequestDto> requestDtoMono) {
        return requestDtoMono
                .map(ComputationRequestDto::getInput)
                .map(i -> new ComputationResponseDto(i, i * i));
    }

    //request-stream
    public Flux<ComputationResponseDto> tableStream(ComputationRequestDto requestDto) {
        int input = requestDto.getInput();
        return Flux.range(1, 10)
                .map(i -> new ComputationResponseDto(input, i * input));
    }

    //request-stream
    public Flux<ComputationResponseDto> tableStreamUsingMono(Mono<ComputationRequestDto> requestDtoMono) {
        return requestDtoMono
                .map(ComputationRequestDto::getInput)
                .flatMapMany(
                        input -> Flux.range(1, 10)
                                .map(i -> new ComputationResponseDto(input, i * input))
                );
    }

    //request-channel
    public Flux<ChartResponseDto> chartStream(Flux<ComputationRequestDto> requestDtoFlux) {
        return requestDtoFlux
                .map(ComputationRequestDto::getInput)
                .map(i -> new ChartResponseDto(i, i * i + 1));
    }
}
