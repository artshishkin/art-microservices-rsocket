package net.shyshkin.study.rsocket.tradingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTradeRequest {

    @Size(min = 4, max = 100)
    private String userId;
    @Size(min = 3, max = 5)
    private String stockCode;
    @Positive
    private int quantity;
    private TradeType tradeType;

}
