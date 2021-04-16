package net.shyshkin.study.rsocket.tradingservice.service.trade;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.rsocket.tradingservice.client.StockClient;
import net.shyshkin.study.rsocket.tradingservice.client.UserClient;
import net.shyshkin.study.rsocket.tradingservice.dto.StockTradeRequest;
import net.shyshkin.study.rsocket.tradingservice.dto.StockTradeResponse;
import net.shyshkin.study.rsocket.tradingservice.dto.TradeStatus;
import net.shyshkin.study.rsocket.tradingservice.dto.TransactionRequest;
import net.shyshkin.study.rsocket.tradingservice.mapper.DtoMapper;
import net.shyshkin.study.rsocket.tradingservice.service.UserStockService;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public abstract class TradeOperation {

    protected final UserStockService stockService;
    protected final UserClient userClient;
    protected final StockClient stockClient;
    protected final DtoMapper mapper;
    protected final StockTradeRequest tradeRequest;

    protected TransactionRequest transactionRequest;
    protected int currentStockPrice;

    public Mono<StockTradeResponse> trade() {

        currentStockPrice = stockClient.getCurrentStockPrice(tradeRequest.getStockCode());
        int amount = tradeRequest.getQuantity() * currentStockPrice;
        transactionRequest = mapper.toTransactionRequest(tradeRequest, amount);

        return tradeInternalSteps()
                .defaultIfEmpty(mapper.toStockTradeResponse(tradeRequest, TradeStatus.FAILED, currentStockPrice));
    }

    abstract protected Mono<StockTradeResponse> tradeInternalSteps();
}
