package net.shyshkin.study.rsocket.tradingservice.mapper;

import net.shyshkin.study.rsocket.tradingservice.dto.*;
import net.shyshkin.study.rsocket.tradingservice.entity.UserStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = {TradeType.class, TransactionType.class})
public interface DtoMapper {

    UserStock toUserStock(StockTradeRequest request);

    @Mapping(target = "type", expression = "java(request.getTradeType() == TradeType.BUY ? TransactionType.DEBIT : TransactionType.CREDIT)")
    TransactionRequest toTransactionRequest(StockTradeRequest request, int amount);

    @Mapping(source = "status", target = "tradeStatus")
    StockTradeResponse toStockTradeResponse(StockTradeRequest request, TradeStatus status, int price);
}