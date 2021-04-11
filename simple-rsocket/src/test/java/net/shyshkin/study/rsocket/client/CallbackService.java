package net.shyshkin.study.rsocket.client;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.dto.ResponseDto;
import net.shyshkin.study.rsocket.util.ObjectUtil;
import reactor.core.publisher.Mono;

@Slf4j
public class CallbackService implements RSocket {

    @Override
    public Mono<Void> fireAndForget(Payload payload) {

        ResponseDto responseDto = ObjectUtil.toObject(payload, ResponseDto.class);
        log.debug("client: received {}", responseDto);
        return Mono.empty();
    }
}
