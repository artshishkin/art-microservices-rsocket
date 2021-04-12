package net.shyshkin.study.rsocket.springrsocket.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@MessageMapping("math.validation")
public class InputValidationController {

    @MessageMapping("double.{input}")
    public Mono<Integer> doubleIt(@DestinationVariable int input) {
        return input < 31 ?
                Mono.just(input * 2) :
                Mono.error(() -> new IllegalArgumentException("can not be > 30"));
    }

}
