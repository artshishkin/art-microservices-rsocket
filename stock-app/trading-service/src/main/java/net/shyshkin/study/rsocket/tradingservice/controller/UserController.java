package net.shyshkin.study.rsocket.tradingservice.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.rsocket.tradingservice.client.UserClient;
import net.shyshkin.study.rsocket.tradingservice.dto.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public Flux<UserDto> getAllUsers() {
        return userClient.getAllUsers();
    }
}
