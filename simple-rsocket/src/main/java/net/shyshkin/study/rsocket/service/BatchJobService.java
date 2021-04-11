package net.shyshkin.study.rsocket.service;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import lombok.RequiredArgsConstructor;
import net.shyshkin.study.rsocket.dto.RequestDto;
import net.shyshkin.study.rsocket.dto.ResponseDto;
import net.shyshkin.study.rsocket.util.ObjectUtil;
import reactor.core.publisher.Mono;

import java.time.Duration;


@RequiredArgsConstructor
public class BatchJobService implements RSocket {

    private final RSocket clientRSocket;

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        RequestDto requestDto = ObjectUtil.toObject(payload, RequestDto.class);
        Mono
                .just(requestDto)
                .doOnNext(dto -> System.out.println("server: received " + dto))
                .delayElement(Duration.ofMillis(1000))
                .flatMap(this::findCube)
                .subscribe();

        return Mono.empty();
    }

    private Mono<Void> findCube(RequestDto requestDto) {

        int input = requestDto.getInput();
        int cube = input * input * input;
        ResponseDto responseDto = ResponseDto.builder().input(input).output(cube).build();
        System.out.println("Server: emitting " + responseDto);

        return clientRSocket.fireAndForget(ObjectUtil.toPayload(responseDto));
    }
}
