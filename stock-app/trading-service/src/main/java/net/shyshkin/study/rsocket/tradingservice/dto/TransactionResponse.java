package net.shyshkin.study.rsocket.tradingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {

    private String userId;
    private int amount;
    private TransactionType type;
    private TransactionStatus status;

}