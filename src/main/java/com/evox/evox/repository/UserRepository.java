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

    Mono<User> findByUsername(String username);

    Mono<User> findByEmail(String email);

    @Query(value = "WITH RECURSIVE user_team AS (\n" +
            "  SELECT u.id, u.full_name, u.phone, u.username, u.created_at, 0 AS level\n" +
            "  FROM users u\n" +
            "  WHERE u.username = :username\n" +
            "  UNION ALL\n" +
            "  SELECT u.id, u.full_name, u.phone, u.username, u.created_at, ut.level + 1 AS level\n" +
            "  FROM users u\n" +
            "  INNER JOIN user_team ut ON u.parent_id = ut.id\n" +
            ")\n" +
            "SELECT * FROM user_team;")
    Flux<User> findUserAndDescendantsTeam(@Param("username") String username);

    //:TODO METODO PARA CAMBIO DE NIVELES ELIMINAR
   /* @Query(value = "WITH RECURSIVE user_team AS (\n" +
            "  SELECT u.id, u.username, u.email, u.password, u.full_name, u.phone, u.country, u.city, u.email_verified, u.token, u.photo, u.ref_link, u.invitation_link, u.roles, u.parent_id, u.status, u.level AS user_level, u.created_at, u.updated_at, 0 AS level\n" +
            "  FROM users u\n" +
            "   WHERE u.username = :username\n" +
            "  UNION ALL\n" +
            "  SELECT u.id, u.username, u.email, u.password, u.full_name, u.phone, u.country, u.city, u.email_verified, u.token, u.photo, u.ref_link, u.invitation_link, u.roles, u.parent_id, u.status, u.level AS user_level, u.created_at, u.updated_at, \n" +
            "    CASE \n" +
            "      WHEN ut.level + 1 > 10 THEN 10 \n" +
            "      ELSE ut.level + 1 \n" +
            "    END AS level\n" +
            "  FROM users u\n" +
            "  INNER JOIN user_team ut ON u.parent_id = ut.id\n" +
            ")\n" +
            "SELECT id, username, email, password, full_name, phone, country, city, email_verified, token, photo, ref_link, invitation_link, roles, parent_id, status, level AS level, created_at, updated_at\n" +
            "FROM user_team;")*/

    @Query(value = "WITH RECURSIVE user_team AS (\n" +
            "  SELECT u.id, u.username, u.email, u.password, u.full_name, u.phone, u.country, u.city, u.email_verified, u.token, u.photo, u.ref_link, u.invitation_link, u.roles, u.parent_id, u.status, u.level AS user_level, u.created_at, u.updated_at, 0 AS level\n" +
            "  FROM users u\n" +
            "  WHERE u.username = :username\n" +
            "  UNION ALL\n" +
            "  SELECT u.id, u.username, u.email, u.password, u.full_name, u.phone, u.country, u.city, u.email_verified, u.token, u.photo, u.ref_link, u.invitation_link, u.roles, u.parent_id, u.status, u.level AS user_level, u.created_at, u.updated_at, ut.level + 1 AS level\n" +
            "  FROM users u\n" +
            "  INNER JOIN user_team ut ON u.parent_id = ut.id\n" +
            ")\n" +
            "SELECT id, username, email, password, full_name, phone, country, city, email_verified, token, photo, ref_link, invitation_link, roles, parent_id, status, level AS level, created_at, updated_at\n" +
            "FROM user_team;\n")
    Flux<User> findUserAndDescendantsLevel(@Param("username") String username);
}
