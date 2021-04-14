package net.shyshkin.study.rsocket.stockservice.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.rsocket.stockservice.dto.StockTickDto;
import net.shyshkin.study.rsocket.stockservice.service.StockService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @MessageMapping("stock.price")
    public Flux<StockTickDto> stockPrice() {
        return stockService.getStockPrice();
    }
}
