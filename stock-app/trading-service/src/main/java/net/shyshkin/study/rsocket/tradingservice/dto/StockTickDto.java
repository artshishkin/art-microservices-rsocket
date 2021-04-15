package net.shyshkin.study.rsocket.tradingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTickDto {

    private String code;
    private int price;
    private LocalDateTime date;

}
