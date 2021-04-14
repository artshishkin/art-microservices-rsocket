package net.shyshkin.study.rsocket.userservice.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.rsocket.userservice.dto.UserDto;
import net.shyshkin.study.rsocket.userservice.service.UserService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@MessageMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @MessageMapping("get.all")
    public Flux<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @MessageMapping("get.{id}")
    public Mono<UserDto> getById(@DestinationVariable String id) {
        return userService.getById(id);
    }

    @MessageMapping("create")
    public Mono<UserDto> createUser(Mono<UserDto> userDtoMono) {
        return userService.createUser(userDtoMono);
    }

    @MessageMapping("update.{id}")
    public Mono<UserDto> updateUser(@DestinationVariable String id, Mono<UserDto> userDtoMono) {
        return userService.updateUser(id, userDtoMono);
    }

    @MessageMapping("delete.{id}")
    public Mono<Void> deleteById(String id) {
        return userService.deleteById(id);
    }
}
