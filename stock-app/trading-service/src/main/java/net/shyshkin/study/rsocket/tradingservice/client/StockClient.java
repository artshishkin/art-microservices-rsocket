package net.shyshkin.study.rsocket.tradingservice.client;

import net.shyshkin.study.rsocket.tradingservice.dto.StockTickDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class StockClient {

    private final RSocketRequester requester;
    private Flux<StockTickDto> hotPublisher;

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
                .retryWhen(Retry.fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2)))
                .publish()
                .autoConnect(0);
    }
}
