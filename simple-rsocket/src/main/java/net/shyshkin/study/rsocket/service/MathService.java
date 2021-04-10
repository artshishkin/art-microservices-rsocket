package net.shyshkin.study.rsocket.service;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import net.shyshkin.study.rsocket.dto.RequestDto;
import net.shyshkin.study.rsocket.util.ObjectUtil;
import reactor.core.publisher.Mono;

public class MathService implements RSocket {

    @Override
    public Mono<Void> fireAndForget(Payload payload) {
        System.out.println("Receiving: " + ObjectUtil.toObject(payload, RequestDto.class));
        return Mono.empty();
    }
}
