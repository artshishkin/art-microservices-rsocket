package net.shyshkin.study.rsocket.tradingservice.client;

import net.shyshkin.study.rsocket.tradingservice.dto.StockTickDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class StockClient {

    private final RSocketRequester requester;
    private Flux<StockTickDto> hotPublisher;
    private final Map<String, Integer> lastPriceMap = new HashMap<>();

    public StockClient(RSocketRequester.Builder builder,
                       RSocketConnectorConfigurer connectorConfigurer,
                       @Value("${app.clients.stock-service.host}") String host,
                       @Value("${app.clients.stock-service.port}") int port) {

        this.requester = builder
                .rsocketConnector(connectorConfigurer)
                .tcp(host, port);
        initialize();
    }

    public Flux<StockTickDto> getStockStream() {
        return hotPublisher;
    }

    private void initialize() {
        hotPublisher = requester.route("stock.price")
                .retrieveFlux(StockTickDto.class)
                .doOnNext(this::saveLastPrice)
                .retryWhen(Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2)))
                .publish()
                .autoConnect(0);
    }

    public int getCurrentStockPrice(String stockCode) {
        return lastPriceMap.getOrDefault(stockCode, 0);
    }

    private void saveLastPrice(StockTickDto tick) {
        lastPriceMap.put(tick.getCode(), tick.getPrice());
    }
}
