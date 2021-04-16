package net.shyshkin.study.rsocket.tradingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.tradingservice.client.StockClient;
import net.shyshkin.study.rsocket.tradingservice.client.UserClient;
import net.shyshkin.study.rsocket.tradingservice.dto.StockTradeRequest;
import net.shyshkin.study.rsocket.tradingservice.dto.StockTradeResponse;
import net.shyshkin.study.rsocket.tradingservice.dto.TradeType;
import net.shyshkin.study.rsocket.tradingservice.mapper.DtoMapper;
import net.shyshkin.study.rsocket.tradingservice.service.trade.BuyOperation;
import net.shyshkin.study.rsocket.tradingservice.service.trade.SellOperation;
import net.shyshkin.study.rsocket.tradingservice.service.trade.TradeOperation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Profile("refactored-trade")
@Slf4j
@Service
@RequiredArgsConstructor
public class RefactoredTradeService implements TradeService {

    private final UserStockService stockService;
    private final UserClient userClient;
    private final StockClient stockClient;
    private final DtoMapper mapper;

    @Override
    public Mono<StockTradeResponse> trade(StockTradeRequest tradeRequest) {

        TradeOperation tradeOperation = (tradeRequest.getTradeType() == TradeType.BUY) ?
                new BuyOperation(stockService, userClient, stockClient, mapper, tradeRequest) :
                new SellOperation(stockService, userClient, stockClient, mapper, tradeRequest);
        return tradeOperation.trade();
    }
}
