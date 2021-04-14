package net.shyshkin.study.rsocket.userservice.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.rsocket.userservice.dto.UserDto;
import net.shyshkin.study.rsocket.userservice.mapper.UserMapper;
import net.shyshkin.study.rsocket.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public Flux<UserDto> getAllUsers() {
        return repository
                .findAll()
                .map(mapper::toDto);
    }

    public Mono<UserDto> getById(String id) {
        return repository
                .findById(id)
                .map(mapper::toDto);
    }

    public Mono<UserDto> createUser(Mono<UserDto> userDtoMono) {
        return userDtoMono
                .map(mapper::toUser)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    public Mono<UserDto> updateUser(String id, Mono<UserDto> userDtoMono) {
        return repository.findById(id)
                .flatMap(u -> userDtoMono)
                .map(mapper::toUser)
                .doOnNext(u -> u.setId(id))
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

}
