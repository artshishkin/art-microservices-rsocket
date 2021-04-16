package net.shyshkin.study.rsocket.tradingservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.tradingservice.client.StockClient;
import net.shyshkin.study.rsocket.tradingservice.dto.StockTickDto;
import net.shyshkin.study.rsocket.tradingservice.dto.StockTradeRequest;
import net.shyshkin.study.rsocket.tradingservice.dto.StockTradeResponse;
import net.shyshkin.study.rsocket.tradingservice.service.TradeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("stock")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;
    private final StockClient stockClient;

    @PostMapping("trade")
    public Mono<ResponseEntity<StockTradeResponse>> trade(@RequestBody Mono<StockTradeRequest> tradeRequestMono) {
        return tradeRequestMono
                .doOnNext(req -> log.debug("{}", req))
                .filter(req -> req.getQuantity() > 0)
                .flatMap(tradeService::trade)
                .doOnNext(resp -> log.debug("{}", resp))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @GetMapping(value = "tick/stream", produces = {MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_NDJSON_VALUE})
    public Flux<StockTickDto> stockTickStream() {
        return stockClient.getStockStream();
    }
}
