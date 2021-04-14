package net.shyshkin.study.rsocket.userservice.service;

import net.shyshkin.study.rsocket.userservice.dto.TransactionRequest;
import net.shyshkin.study.rsocket.userservice.dto.TransactionResponse;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<TransactionResponse> doTransaction(TransactionRequest request);
}
