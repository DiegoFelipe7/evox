package com.evox.evox.repository;

import com.evox.evox.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Integer> {

    @Query(value = "WITH RECURSIVE user_tree AS (\n" +
            " SELECT *\n" +
            " FROM users\n" +
            " WHERE email = :email\n" + // Modificado para buscar por email en lugar de username
            " UNION ALL\n" +
            " SELECT u.*\n" +
            " FROM users u\n" +
            " INNER JOIN user_tree ut ON u.parent_id = ut.id\n" +
            ")\n" +
            "SELECT * FROM user_tree")
    Flux<User> findUserAndDescendants(@Param("email") String email);

    Mono<User> findByEmail(String email);

}
