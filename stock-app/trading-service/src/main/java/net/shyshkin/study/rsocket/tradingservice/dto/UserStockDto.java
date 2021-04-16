package net.shyshkin.study.rsocket.tradingservice.dto;

import lombok.Data;

@Data
public class UserStockDto {

    private String id;
    private String userId;
    private String stockCode;
    private int quantity;

}
