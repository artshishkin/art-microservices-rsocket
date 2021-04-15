package net.shyshkin.study.rsocket.tradingservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
@Configuration
public class RSocketClientConfig {

    private static final Retry RETRY_STRATEGY = Retry
            .fixedDelay(Long.MAX_VALUE, Duration.ofSeconds(2))
            .doBeforeRetry(rs -> log.debug("Lost connection. Retry: {}", rs.totalRetriesInARow()));

    @Bean
    public RSocketConnectorConfigurer connectorConfigurer() {
        return connector -> connector.reconnect(RETRY_STRATEGY);
    }
}
