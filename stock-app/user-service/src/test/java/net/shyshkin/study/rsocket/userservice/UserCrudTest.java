package net.shyshkin.study.rsocket.userservice;

import io.rsocket.metadata.WellKnownMimeType;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.userservice.dto.OperationType;
import net.shyshkin.study.rsocket.userservice.dto.UserDto;
import net.shyshkin.study.rsocket.userservice.entity.User;
import net.shyshkin.study.rsocket.userservice.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties ={
        "spring.rsocket.server.port=7071"
})
class UserCrudTest {

    RSocketRequester requester;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RSocketRequester.Builder builder;

    @Autowired
    RSocketProperties rSocketProperties;

    @BeforeAll
    void beforeAll() {
        Integer port = rSocketProperties.getServer().getPort();
        requester = builder.tcp("localhost", port);
    }

    @Order(0)
    @Test
    void getAllUsers() {

        //when
        Flux<UserDto> flux = requester.route("user.get.all")
                .retrieveFlux(UserDto.class)
                .doOnNext(dto -> log.debug("Retrieve {}", dto));

        //then
        StepVerifier.create(flux)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void getUserById() {
        //given
        UserDto userDto = randomUser();
        String userId = userDto.getId();

        //when
        Mono<UserDto> mono = requester.route("user.get.{id}", userId)
                .retrieveMono(UserDto.class)
                .doOnNext(dto -> log.debug("Retrieve {}", dto));

        //then
        StepVerifier.create(mono)
                .assertNext(user -> assertAll(
                        () -> assertThat(user.getId()).isNotEmpty(),
                        () -> assertThat(user.getName()).startsWith("Name"),
                        () -> assertThat(user.getBalance()).isGreaterThan(99)
                        )
                )
                .verifyComplete();
    }

    private UserDto randomUser() {
        return requester.route("user.get.all")
                .retrieveFlux(UserDto.class)
                .next()
                .block();
    }

    @Test
    void createNewUser() {
        //given
        UserDto art = UserDto.builder().balance(12345).name("NameArt").build();

        //when
        Mono<UserDto> mono = requester.route("user.create")
                .data(art)
                .retrieveMono(UserDto.class)
                .doOnNext(dto -> log.debug("Created {}", dto));

        //then
        StepVerifier.create(mono)
                .assertNext(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("balance", 12345)
                        .hasFieldOrPropertyWithValue("name", "NameArt")
                        .satisfies(dto -> assertThat(dto.getId()).isNotEmpty())
                )
                .verifyComplete();
    }


    @Test
    void updateUser() {
        //given
        UserDto userDto = randomUser();
        log.debug("User before update: {}", userDto);
        String userId = userDto.getId();
        String newName = userDto.getName() + "Kate";

        //when
        userDto.setBalance(54321);
        userDto.setName(newName);
        Mono<UserDto> mono = requester.route("user.update.{id}", userId)
                .data(userDto)
                .retrieveMono(UserDto.class)
                .doOnNext(dto -> log.debug("Updated {}", dto));

        //then
        StepVerifier.create(mono)
                .assertNext(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("balance", 54321)
                        .hasFieldOrPropertyWithValue("name", newName)
                        .satisfies(dto -> assertThat(dto.getId()).isNotEmpty())
                )
                .verifyComplete();
    }

    @Test
    void deleteUser() throws InterruptedException {
        //given
        UserDto userDto = randomUser();
        log.debug("User to delete: {}", userDto);
        String userId = userDto.getId();

        //when
        Mono<Void> mono = requester.route("user.delete.{id}", userId)
                .send();

        //then
        StepVerifier.create(mono)
                .verifyComplete();

        Thread.sleep(200);
        Mono<User> deletedUserMono = userRepository.findById(userId);
        StepVerifier.create(deletedUserMono)
                .verifyComplete();
    }

    @Test
    void metadataTest() {
        //given
        MimeType mimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.APPLICATION_CBOR.getString());
        UserDto newUser = UserDto.builder().balance(777).name("Name777").build();

        //when
        Mono<Void> mono = requester.route("user.operation.type")
                .metadata(OperationType.POST, mimeType)
                .data(newUser)
                .send();

        //then
        StepVerifier.create(mono)
                .verifyComplete();
    }
}


