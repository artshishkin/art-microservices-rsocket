package net.shyshkin.study.rsocket.userservice.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.rsocket.userservice.dto.TransactionRequest;
import net.shyshkin.study.rsocket.userservice.dto.TransactionResponse;
import net.shyshkin.study.rsocket.userservice.entity.User;
import net.shyshkin.study.rsocket.userservice.mapper.TransactionMapper;
import net.shyshkin.study.rsocket.userservice.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.UnaryOperator;

import static net.shyshkin.study.rsocket.userservice.dto.TransactionStatus.COMPLETED;
import static net.shyshkin.study.rsocket.userservice.dto.TransactionStatus.FAILED;
import static net.shyshkin.study.rsocket.userservice.dto.TransactionType.DEBIT;

@Service
@Primary
@RequiredArgsConstructor
@Profile("!my-transactions-service")
public class VinothTransactionService implements TransactionService {

    private final UserRepository repository;
    private final TransactionMapper tMapper;

    @Override
    public Mono<TransactionResponse> doTransaction(TransactionRequest request) {
        String userId = request.getUserId();
        UnaryOperator<Mono<User>> operator = request.getType().equals(DEBIT) ? debit(request) : credit(request);

        return repository.findById(userId)
                .transform(operator)
                .flatMap(repository::save)
                .map(u -> tMapper.toResponse(request, COMPLETED))
                .defaultIfEmpty(tMapper.toResponse(request, FAILED));
    }

    private UnaryOperator<Mono<User>> credit(TransactionRequest request) {
        return mono -> mono
                .doOnNext(u -> u.setBalance(u.getBalance() + request.getAmount()));
    }

    private UnaryOperator<Mono<User>> debit(TransactionRequest request) {
        return mono -> mono
                .filter(u -> u.getBalance() >= request.getAmount())
                .doOnNext(u -> u.setBalance(u.getBalance() - request.getAmount()));
    }
}
