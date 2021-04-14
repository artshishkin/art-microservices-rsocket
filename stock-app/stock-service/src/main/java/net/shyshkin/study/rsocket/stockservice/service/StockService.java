package net.shyshkin.study.rsocket.stockservice.service;

import net.shyshkin.study.rsocket.stockservice.dto.StockTickDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class StockService {

    private final Stock AMZN = new Stock(1000, "AMZN", 20);
    private final Stock AAPL = new Stock(100, "AAPL", 3);
    private final Stock MSFT = new Stock(200, "MSFT", 5);

    public Flux<StockTickDto> getStockPrice() {
        return Flux.interval(Duration.ofSeconds(2))
                .flatMap(i -> Flux.just(AMZN, AAPL, MSFT))
                .map(stock -> StockTickDto.builder()
                        .code(stock.getCode())
                        .price(stock.getPrice())
                        .date(LocalDateTime.now())
                        .build());
    }

}
