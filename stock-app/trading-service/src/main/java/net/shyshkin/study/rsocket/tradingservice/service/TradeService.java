package net.shyshkin.study.rsocket.tradingservice.service;

import net.shyshkin.study.rsocket.tradingservice.dto.StockTradeRequest;
import net.shyshkin.study.rsocket.tradingservice.dto.StockTradeResponse;
import reactor.core.publisher.Mono;

public interface TradeService {
    Mono<StockTradeResponse> trade(StockTradeRequest tradeRequest);
}
