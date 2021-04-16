package net.shyshkin.study.rsocket.tradingservice.repository;

import net.shyshkin.study.rsocket.tradingservice.entity.UserStock;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserStockRepository extends ReactiveMongoRepository<UserStock, String> {

    Mono<UserStock> findByUserIdAndStockCode(String userId, String stockCode);

    Flux<UserStock> findByUserId(String userId);

}
