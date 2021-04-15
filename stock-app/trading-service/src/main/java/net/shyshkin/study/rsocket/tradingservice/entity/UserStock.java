package net.shyshkin.study.rsocket.tradingservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserStock {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String userId;
    private String stockCode;
    private int quantity;

}
