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

    @MessageMapping("double_empty.{input}")
    public Mono<Integer> doubleItEmpty(@DestinationVariable int input) {
        return Mono.just(input)
                .filter(i -> i < 31)
                .map(i -> i * 2);
    }

    @MessageMapping("double_empty_default.{input}")
    public Mono<Integer> doubleItDefaultIfEmpty(@DestinationVariable int input) {
        return Mono.just(input)
                .filter(i -> i < 31)
                .map(i -> i * 2)
                .defaultIfEmpty(Integer.MIN_VALUE);
    }

    @MessageMapping("double_empty_switch_error.{input}")
    public Mono<Integer> doubleItSwitchIfEmpty(@DestinationVariable int input) {
        return Mono.just(input)
                .filter(i -> i < 31)
                .map(i -> i * 2)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("can not be > 30")));
    }

}
