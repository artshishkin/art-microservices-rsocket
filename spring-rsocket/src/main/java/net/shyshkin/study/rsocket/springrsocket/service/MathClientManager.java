package net.shyshkin.study.rsocket.springrsocket.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.springrsocket.dto.ComputationResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class MathClientManager {

    private final Set<RSocketRequester> clients = Collections.synchronizedSet(new HashSet<>());

    public void add(RSocketRequester requester) {
        requester.rsocket()
                .onClose()
                .doFirst(() -> {
                    clients.add(requester);
                    log.debug("client CONNECTED {}", requester);
                })
                .doFinally(s -> {
                    clients.remove(requester);
                    log.debug("client disconnected {}", requester);
                })
                .subscribe();
    }

    @Value("${app.notification.route}")
    private String notificationRoute;

    //    @Scheduled(fixedRate = 1500)
    public void notificationSimulation() {
        if (StringUtils.hasText(notificationRoute)) {
            int i = ThreadLocalRandom.current().nextInt(1, 100);
            notifyAll(notificationRoute, new ComputationResponseDto(i, i * i));
        }
    }

    public void notifyAll(String route, Object newValue) {
        Flux.fromIterable(clients)
                .doOnNext(c -> log.debug("notifying {}", c))
                .flatMap(c -> c.route(route).data(newValue).send())
                .subscribe();
    }

}
