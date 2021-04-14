package net.shyshkin.study.rsocket.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.userservice.dto.OperationType;
import net.shyshkin.study.rsocket.userservice.dto.UserDto;
import net.shyshkin.study.rsocket.userservice.service.UserService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
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
    public Mono<Void> deleteById(@DestinationVariable String id) {
        return userService.deleteById(id);
    }

    @MessageMapping("operation.type")
    public Mono<Void> metadataOperationType(UserDto userDto, @Header("operation-type") OperationType operationType) {
        log.debug("Operation {} for user {}", operationType, userDto);
        return Mono.empty();
    }
}
