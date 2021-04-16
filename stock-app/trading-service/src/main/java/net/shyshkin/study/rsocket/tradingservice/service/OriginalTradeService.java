package net.shyshkin.study.rsocket.tradingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.tradingservice.client.StockClient;
import net.shyshkin.study.rsocket.tradingservice.client.UserClient;
import net.shyshkin.study.rsocket.tradingservice.dto.*;
import net.shyshkin.study.rsocket.tradingservice.mapper.DtoMapper;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static net.shyshkin.study.rsocket.tradingservice.dto.TransactionStatus.COMPLETED;

@Primary
@Profile("!refactored-trade")
@Slf4j
@Service
@RequiredArgsConstructor
public class OriginalTradeService implements TradeService {

    private final UserStockService stockService;
    private final UserClient userClient;
    private final StockClient stockClient;
    private final DtoMapper mapper;

    @Override
    public Mono<StockTradeResponse> trade(StockTradeRequest tradeRequest) {

        int currentStockPrice = stockClient.getCurrentStockPrice(tradeRequest.getStockCode());
        int amount = tradeRequest.getQuantity() * currentStockPrice;
        TransactionRequest transactionRequest = mapper.toTransactionRequest(tradeRequest, amount);

        Mono<StockTradeResponse> tradeResult = (tradeRequest.getTradeType() == TradeType.BUY) ?
                buyStock(tradeRequest, transactionRequest, currentStockPrice) :
                sellStock(tradeRequest, transactionRequest, currentStockPrice);
        return tradeResult
                .defaultIfEmpty(mapper.toStockTradeResponse(tradeRequest, TradeStatus.FAILED, currentStockPrice));
    }

    public Mono<StockTradeResponse> sellStock(StockTradeRequest tradeRequest, TransactionRequest transactionRequest, int currentStockPrice) {
        return stockService.sellStock(tradeRequest)
                .flatMap(us -> userClient.doTransaction(transactionRequest))
                .filter(resp -> resp.getStatus() == COMPLETED)
                .map(resp -> mapper.toStockTradeResponse(tradeRequest, TradeStatus.COMPLETED, currentStockPrice));
    }

    public Mono<StockTradeResponse> buyStock(StockTradeRequest tradeRequest, TransactionRequest transactionRequest, int currentStockPrice) {
        return userClient
                .doTransaction(transactionRequest)
                .filter(resp -> resp.getStatus() == COMPLETED)
                .flatMap(resp -> stockService.buyStock(tradeRequest))
                .map(resp -> mapper.toStockTradeResponse(tradeRequest, TradeStatus.COMPLETED, currentStockPrice));
    }

}
