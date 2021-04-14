package net.shyshkin.study.rsocket.userservice.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.rsocket.userservice.dto.TransactionRequest;
import net.shyshkin.study.rsocket.userservice.dto.TransactionResponse;
import net.shyshkin.study.rsocket.userservice.service.TransactionService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@Controller
@MessageMapping("user")
@RequiredArgsConstructor
public class UserTransactionController {

    private final TransactionService transactionService;

    @MessageMapping("transaction")
    public Mono<TransactionResponse> doTransaction(Mono<TransactionRequest> requestMono) {
        return requestMono.flatMap(transactionService::doTransaction);
    }

}
