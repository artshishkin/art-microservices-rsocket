package net.shyshkin.study.rsocket.userservice.config;

import io.rsocket.metadata.WellKnownMimeType;
import net.shyshkin.study.rsocket.userservice.dto.OperationType;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

@Configuration
public class RSocketServerConfig {

    @Bean
    public RSocketStrategiesCustomizer strategiesCustomizer() {
        MimeType mimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.APPLICATION_CBOR.getString());
        return strategies -> strategies
                .metadataExtractorRegistry(
                        registry -> registry
                                .metadataToExtract(mimeType, OperationType.class, "operation-type")
                );
    }
}
