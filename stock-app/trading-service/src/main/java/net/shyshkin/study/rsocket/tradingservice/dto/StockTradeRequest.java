package net.shyshkin.study.rsocket.tradingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTradeRequest {

    private String userId;
    private String stockCode;
    private int quantity;
    private TradeType tradeType;

}
