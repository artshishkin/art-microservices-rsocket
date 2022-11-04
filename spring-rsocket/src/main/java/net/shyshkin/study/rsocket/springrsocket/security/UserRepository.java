package net.shyshkin.study.rsocket.springrsocket.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Map;

@Profile("secure")
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final PasswordEncoder encoder;

    private Map<String, UserDetails> db;

    @PostConstruct
    private void init() {

        db = Map.of(
                "user01", User.withUsername("user01").password(encoder.encode("pass01u")).roles("USER").build(),
                "user02", User.withUsername("user02").password(encoder.encode("pass02u")).roles("USER").build(),
                "admin01", User.withUsername("admin01").password(encoder.encode("pass03a")).roles("ADMIN").build(),
                "client01", User.withUsername("client01").password(encoder.encode("pass04c")).roles("TRUSTED_CLIENT").build()
        );
    }

    public UserDetails findByUsername(String username) {
        return db.get(username);
    }

}
