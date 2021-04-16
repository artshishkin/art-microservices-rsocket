package net.shyshkin.study.rsocket.tradingservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.tradingservice.dto.UserDto;
import net.shyshkin.study.rsocket.tradingservice.dto.UserStockDto;
import net.shyshkin.study.rsocket.tradingservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Flux<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{userId}/stocks")
    public Flux<UserStockDto> getAllStocksOfUser(@PathVariable String userId) {
        return userService
                .getAllUserStocks(userId)
                .doFirst(() -> log.debug("User `{}` has stocks:", userId))
                .doOnNext(stock -> log.debug("{}", stock));
    }
}
