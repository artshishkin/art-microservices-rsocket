package net.shyshkin.study.rsocket.service;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import net.shyshkin.study.rsocket.dto.RequestDto;
import net.shyshkin.study.rsocket.dto.ResponseDto;
import net.shyshkin.study.rsocket.util.ObjectUtil;
import reactor.core.publisher.Mono;

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
}
