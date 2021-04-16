package net.shyshkin.study.rsocket.userservice.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.rsocket.userservice.entity.User;
import net.shyshkin.study.rsocket.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSetupService implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        userRepository
                .count()
                .filter(count -> count == 0)
                .flatMapMany(c -> Flux.range(1, 10))
                .map(this::createStubUser)
                .flatMap(userRepository::save)
                .doOnNext(u -> log.debug("user saved: {}", u))
                .subscribe();
    }

    private User createStubUser(int fakeIdx) {
        return User.builder().balance(10000 + 100 * fakeIdx).name("Name" + fakeIdx).build();
    }
}
