package net.shyshkin.study.rsocket.springrsocket.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@MessageMapping("math.exception")
public class ExceptionTestController {

    @MessageMapping("double.{input}")
    public Mono<Integer> doubleItSwitchIfEmpty(@DestinationVariable int input) {
        return Mono.just(input)
                .filter(i -> i < 31)
                .map(i -> i * 2)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("can not be > 30")));
    }

    @MessageExceptionHandler
    public Mono<Integer> defaultValueHandler(Exception exception) {
        return Mono.just(-1);
    }

}
