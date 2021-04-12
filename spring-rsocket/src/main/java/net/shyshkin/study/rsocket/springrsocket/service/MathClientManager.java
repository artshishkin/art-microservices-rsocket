package net.shyshkin.study.rsocket.springrsocket.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class MathClientManager {

    private final Set<RSocketRequester> clients = Collections.synchronizedSet(new HashSet<>());

    public void add(RSocketRequester requester) {
        requester.rsocket()
                .onClose()
                .doFirst(() -> clients.add(requester))
                .doFinally(s -> {
                    log.debug("finally");
                    clients.remove(requester);
                })
                .subscribe();
    }

    @Scheduled(fixedRate = 1000)
    public void print() {
        log.debug("Connected clients: {}", clients);
    }

}
