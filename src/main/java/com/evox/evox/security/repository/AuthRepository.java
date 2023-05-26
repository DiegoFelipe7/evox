package com.evox.evox.security.repository;
import com.evox.evox.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AuthRepository extends ReactiveCrudRepository<User, Integer> {

    Mono<User> findByUsername(String username);
    Mono<User> findByEmailIgnoreCase(String email);

    Mono<User> findByToken(String token);
}
