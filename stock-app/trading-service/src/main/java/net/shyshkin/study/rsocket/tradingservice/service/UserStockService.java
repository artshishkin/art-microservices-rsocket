package net.shyshkin.study.rsocket.tradingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.tradingservice.dto.StockTradeRequest;
import net.shyshkin.study.rsocket.tradingservice.entity.UserStock;
import net.shyshkin.study.rsocket.tradingservice.mapper.DtoMapper;
import net.shyshkin.study.rsocket.tradingservice.repository.UserStockRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserStockService {

    private final UserStockRepository stockRepository;
    private final DtoMapper mapper;

    //buy
    public Mono<UserStock> buyStock(StockTradeRequest request) {
        return stockRepository.findByUserIdAndStockCode(request.getUserId(), request.getStockCode())
                .defaultIfEmpty(mapper.toUserStock(request))
                .doOnNext(us -> us.setQuantity(us.getQuantity() + request.getQuantity()))
                .flatMap(stockRepository::save);
    }

    //sell
    public Mono<UserStock> sellStock(StockTradeRequest request) {
        return stockRepository.findByUserIdAndStockCode(request.getUserId(), request.getStockCode())
                .filter(us -> us.getQuantity() >= request.getQuantity())
                .doOnNext(us -> us.setQuantity(us.getQuantity() - request.getQuantity()))
                .flatMap(stockRepository::save);
    }
}
