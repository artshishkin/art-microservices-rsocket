package net.shyshkin.study.rsocket.tradingservice.service.trade;

import net.shyshkin.study.rsocket.tradingservice.client.StockClient;
import net.shyshkin.study.rsocket.tradingservice.client.UserClient;
import net.shyshkin.study.rsocket.tradingservice.dto.StockTradeRequest;
import net.shyshkin.study.rsocket.tradingservice.dto.StockTradeResponse;
import net.shyshkin.study.rsocket.tradingservice.dto.TradeStatus;
import net.shyshkin.study.rsocket.tradingservice.mapper.DtoMapper;
import net.shyshkin.study.rsocket.tradingservice.service.UserStockService;
import reactor.core.publisher.Mono;

import static net.shyshkin.study.rsocket.tradingservice.dto.TransactionStatus.COMPLETED;

public class SellOperation extends TradeOperation{

    public SellOperation(UserStockService stockService, UserClient userClient, StockClient stockClient, DtoMapper mapper, StockTradeRequest tradeRequest) {
        super(stockService, userClient, stockClient, mapper, tradeRequest);
    }

    @Override
    protected Mono<StockTradeResponse> tradeInternalSteps() {
        return stockService.sellStock(tradeRequest)
                .flatMap(us -> userClient.doTransaction(transactionRequest))
                .filter(resp -> resp.getStatus() == COMPLETED)
                .map(resp -> mapper.toStockTradeResponse(tradeRequest, TradeStatus.COMPLETED, currentStockPrice));
    }
}
