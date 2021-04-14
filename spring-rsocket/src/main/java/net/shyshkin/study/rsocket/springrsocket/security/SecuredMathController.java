package net.shyshkin.study.rsocket.springrsocket.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationRequestDto;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import net.shyshkin.study.rsocket.springrsocket.service.MathService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@MessageMapping("math.service.secured")
@RequiredArgsConstructor
public class SecuredMathController {

    private final MathService service;

    @PreAuthorize("hasRole('USER')")
    @MessageMapping("square")
    public Mono<ComputationResponseDto> findSquare(Mono<ComputationRequestDto> requestDtoMono,
                                                   @AuthenticationPrincipal Mono<UserDetails> userDetailsMono) {
        return userDetailsMono
                .doOnNext(userDetails -> log.debug("User Details: {}", userDetails))
                .then(service.findSquare(requestDtoMono));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MessageMapping("table")
    public Flux<ComputationResponseDto> tableStreamUsingMono(Mono<ComputationRequestDto> requestDtoMono) {
        return service.tableStreamUsingMono(requestDtoMono);
    }
}
