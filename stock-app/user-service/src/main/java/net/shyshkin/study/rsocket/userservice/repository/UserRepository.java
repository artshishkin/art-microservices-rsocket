package net.shyshkin.study.rsocket.userservice.repository;

import net.shyshkin.study.rsocket.userservice.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
