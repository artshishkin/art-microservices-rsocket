package net.shyshkin.study.rsocket.springrsocket;

import io.rsocket.transport.netty.client.TcpClientTransport;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;
import reactor.test.StepVerifier;

import java.nio.channels.ClosedChannelException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@ActiveProfiles("ssl_enabled")
@WithUserDetails("client01")
@TestPropertySource(properties = {
        "spring.rsocket.server.port=6262"
})
public class Lec12SslTest {

    @Autowired
    RSocketRequester.Builder builder;

    static {
        System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\Admin\\IdeaProjects\\Study\\VinothSelvaraj\\art-microservices-rsocket\\ssl-tls\\client.truststore");
        System.setProperty("javax.net.ssl.trustStorePassword", "password");
    }

    @Test
    void sslTlsTest_fail() {
        //given
        int input = 3;
        RSocketRequester requester = builder
                .tcp("localhost", 6262);

        //when
        Mono<ComputationResponseDto> mono = requester
                .route("math.service.find_square")
                .data(new ComputationRequestDto(input))
                .retrieveMono(ComputationResponseDto.class)
                .doOnNext(dto -> log.debug("response: {}", dto));

        //then
        Mono<Integer> outputMono = mono.map(ComputationResponseDto::getOutput);
        StepVerifier.create(outputMono)
                .expectErrorSatisfies(ex -> assertThat(ex).isInstanceOf(ClosedChannelException.class))
                .verify();
    }

    @Test
    void sslTlsTest_success() {
        //given
        int input = 3;
        RSocketRequester requester = builder
                .transport(TcpClientTransport.create(
                        TcpClient.create().host("localhost").port(6262).secure()
                ));

        //when
        Mono<ComputationResponseDto> mono = requester
                .route("math.service.find_square")
                .data(new ComputationRequestDto(input))
                .retrieveMono(ComputationResponseDto.class)
                .doOnNext(dto -> log.debug("response: {}", dto));

        //then
        Mono<Integer> outputMono = mono.map(ComputationResponseDto::getOutput);
        StepVerifier.create(outputMono)
                .expectNext(input * input)
                .verifyComplete();
    }
}

