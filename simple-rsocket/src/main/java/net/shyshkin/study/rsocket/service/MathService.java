package net.shyshkin.study.rsocket.service;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import net.shyshkin.study.rsocket.dto.ChartResponseDto;
import net.shyshkin.study.rsocket.dto.RequestDto;
import net.shyshkin.study.rsocket.dto.ResponseDto;
import net.shyshkin.study.rsocket.util.ObjectUtil;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class MathService implements RSocket {

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        System.out.println("Receiving: " + ObjectUtil.toObject(payload, RequestDto.class));
        return Mono.empty();
    }

    @Override
    public Mono<Payload> requestResponse(Payload payload) {

        return Mono.fromSupplier(
                () -> {
                    RequestDto requestDto = ObjectUtil.toObject(payload, RequestDto.class);
                    int input = requestDto.getInput();

                    ResponseDto responseDto = ResponseDto.builder()
                            .input(input)
                            .output(input * input)
                            .build();

                    return ObjectUtil.toPayload(responseDto);
                }
        );
    }


//    @Override
//    public Mono<Payload> requestResponse(Payload payload) {
//
//        RequestDto requestDto = ObjectUtil.toObject(payload, RequestDto.class);
//        int input = requestDto.getInput();
//
//        ResponseDto responseDto = ResponseDto.builder()
//                .input(input)
//                .output(input * input)
//                .build();
//
//        return Mono.just(ObjectUtil.toPayload(responseDto));
//    }

    @Override
    public Flux<Payload> requestStream(Payload payload) {

        RequestDto requestDto = ObjectUtil.toObject(payload, RequestDto.class);
        int input = requestDto.getInput();
        return Flux
                .range(1, 10)
                .map(i -> i * input)
                .map(output -> new ResponseDto(input, output))
                .delayElements(Duration.ofMillis(10))
                .doOnNext(dto -> System.out.println("server: " + dto))
                .doFinally(f -> System.out.println("server: " + f))
                .map(ObjectUtil::toPayload);
    }

    @Override
    public Flux<Payload> requestChannel(Publisher<Payload> payloads) {

        return Flux.from(payloads)
                .map(p -> ObjectUtil.toObject(p, RequestDto.class))
                .map(RequestDto::getInput)
                .map(i -> new ChartResponseDto(i, i * i + 1))
                .doOnNext(System.out::println)
                .doFinally(System.out::println)
                .map(ObjectUtil::toPayload);
    }
}
