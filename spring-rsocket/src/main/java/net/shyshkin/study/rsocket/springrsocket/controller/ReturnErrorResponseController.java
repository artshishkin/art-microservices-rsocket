package net.shyshkin.study.rsocket.springrsocket.controller;

import net.shyshkin.study.rsocket.springrsocket.dto.Response;
import net.shyshkin.study.rsocket.springrsocket.dto.error.ErrorEvent;
import net.shyshkin.study.rsocket.springrsocket.dto.error.StatusCode;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@MessageMapping("math.response")
public class ReturnErrorResponseController {

    @MessageMapping("double.{input}")
    public Mono<Response<Integer>> doubleItWithErrorResponse(@DestinationVariable int input) {
        return Mono.just(input)
                .filter(i -> i < 31)
                .map(i -> i * 2)
                .map(Response::with)
                .defaultIfEmpty(Response.with(new ErrorEvent(StatusCode.EC001)));
    }
}
