package net.shyshkin.study.rsocket.springrsocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpringRsocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringRsocketApplication.class, args);
    }

}
