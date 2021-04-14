package net.shyshkin.study.rsocket.springrsocket;

import io.rsocket.exceptions.ApplicationErrorException;
import io.rsocket.exceptions.RejectedSetupException;
import io.rsocket.metadata.WellKnownMimeType;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.UsernamePasswordMetadata;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class Lec13SecurityTest {

    public static final MimeType MIME_TYPE = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_AUTHENTICATION.getString());

    @Autowired
    RSocketRequester.Builder builder;

    @Test
    @WithUserDetails("client01")
    @Disabled("We have different roles for setup and request")
    void requestResponse_withUserDetailsTest() {
        //given
        int input = 12;
        RSocketRequester requester = builder.tcp("localhost", 6565);

        //when
        Mono<ComputationResponseDto> mono = requester.route("math.service.secured.square")
                .data(new ComputationRequestDto(input))
                .retrieveMono(ComputationResponseDto.class)
                .doOnNext(dto -> log.debug("{}", dto));

        //then
        StepVerifier.create(mono)
                .assertNext(dto -> assertThat(dto)
                        .hasFieldOrPropertyWithValue("input", input)
                        .hasFieldOrPropertyWithValue("output", input * input)
                )
                .verifyComplete();
    }

    @Test
    void requestResponse_withMetadataAuthentication() {
        //given
        int input = 12;
        UsernamePasswordMetadata metadataSetup = new UsernamePasswordMetadata("client01", "pass04c");
        UsernamePasswordMetadata metadataRequest = new UsernamePasswordMetadata("user01", "pass01u");

        //when
        RSocketRequester requester = builder
                .setupMetadata(metadataSetup, MIME_TYPE)
                .tcp("localhost", 6565);


        Mono<ComputationResponseDto> mono = requester.route("math.service.secured.square")
                .metadata(metadataRequest, MIME_TYPE)
                .data(new ComputationRequestDto(input))
                .retrieveMono(ComputationResponseDto.class)
                .doOnNext(dto -> log.debug("{}", dto));

        //then
        StepVerifier.create(mono)
                .assertNext(dto -> assertThat(dto)
                        .hasFieldOrPropertyWithValue("input", input)
                        .hasFieldOrPropertyWithValue("output", input * input)
                )
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({
            "admin01,pass03a,Access Denied",
            "user02,pass02u,Access Denied",
            "client01,wrong_pass,Invalid Credentials",
            "admin01,wrong_pass,Invalid Credentials",
            "foo,buzz,Invalid Credentials"
    })
    void requestResponse_invalidSetup(String username, String password, String expectedErrorMessage) {
        //given
        int input = 12;
        UsernamePasswordMetadata metadata = new UsernamePasswordMetadata(username, password);

        //when
        RSocketRequester requester = builder
                .setupMetadata(metadata, MIME_TYPE)
                .tcp("localhost", 6565);

        Mono<ComputationResponseDto> mono = requester.route("math.service.secured.square")
                .data(new ComputationRequestDto(input))
                .retrieveMono(ComputationResponseDto.class)
                .doOnNext(dto -> log.debug("{}", dto));

        //then
        StepVerifier.create(mono)
                .verifyErrorSatisfies(ex -> assertThat(ex)
                        .isInstanceOf(RejectedSetupException.class)
                        .hasMessage(expectedErrorMessage));
    }

    @ParameterizedTest
    @CsvSource({
            "admin01,pass03a,Access Denied",
            "client01,pass04c,Access Denied",
            "client01,wrong_pass,Invalid Credentials",
            "admin01,wrong_pass,Invalid Credentials",
            "foo,buzz,Invalid Credentials"
    })
    void requestResponse_invalidRequest(String username, String password, String expectedErrorMessage) {
        //given
        int input = 12;
        UsernamePasswordMetadata metadataSetup = new UsernamePasswordMetadata("client01", "pass04c");
        UsernamePasswordMetadata metadataRequest = new UsernamePasswordMetadata(username, password);

        RSocketRequester requester = builder
                .setupMetadata(metadataSetup, MIME_TYPE)
                .tcp("localhost", 6565);

        //when
        Mono<ComputationResponseDto> mono = requester.route("math.service.secured.square")
                .metadata(metadataRequest, MIME_TYPE)
                .data(new ComputationRequestDto(input))
                .retrieveMono(ComputationResponseDto.class)
                .doOnNext(dto -> log.debug("{}", dto));

        //then
        StepVerifier.create(mono)
                .verifyErrorSatisfies(ex -> assertThat(ex)
                        .isInstanceOf(ApplicationErrorException.class)
                        .hasMessage(expectedErrorMessage));
    }
}





