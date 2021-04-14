package net.shyshkin.study.rsocket.springrsocket.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

@Configuration
@EnableRSocketSecurity
public class RSocketSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PayloadSocketAcceptorInterceptor interceptor(RSocketSecurity security) {
        return security
                .simpleAuthentication(Customizer.withDefaults())
                .authorizePayload(
                        authorize -> authorize
                                .setup().permitAll()
                                .anyRequest().permitAll()
                ).build();
    }
}
