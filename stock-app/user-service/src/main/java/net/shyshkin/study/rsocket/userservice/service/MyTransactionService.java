package net.shyshkin.study.rsocket.userservice.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.rsocket.userservice.dto.TransactionRequest;
import net.shyshkin.study.rsocket.userservice.dto.TransactionResponse;
import net.shyshkin.study.rsocket.userservice.mapper.TransactionMapper;
import net.shyshkin.study.rsocket.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static net.shyshkin.study.rsocket.userservice.dto.TransactionStatus.COMPLETED;
import static net.shyshkin.study.rsocket.userservice.dto.TransactionStatus.FAILED;

@Service
@RequiredArgsConstructor
public class MyTransactionService implements TransactionService {

    private final UserRepository repository;
    private final TransactionMapper tMapper;

    @Override
    public Mono<TransactionResponse> doTransaction(TransactionRequest request) {
        String userId = request.getUserId();

        return repository.findById(userId)
                .doOnNext(u -> u.setBalance(newBalance(u.getBalance(), request)))
                .flatMap(repository::save)
                .map(u -> tMapper.toResponse(request, COMPLETED))
                .onErrorReturn(
                        NotEnoughBalanceException.class,
                        tMapper.toResponse(request, FAILED)
                )
                .defaultIfEmpty(tMapper.toResponse(request, FAILED));
    }

    private int newBalance(int oldBalance, TransactionRequest request) {
        int newBalance = 0;
        switch (request.getType()) {
            case CREDIT:
                newBalance = oldBalance + request.getAmount();
                break;
            case DEBIT:
                newBalance = oldBalance - request.getAmount();
                if (newBalance < 0) throw new NotEnoughBalanceException("Not enough balance");
                break;
        }
        return newBalance;
    }
}
