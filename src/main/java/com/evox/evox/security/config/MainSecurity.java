package com.evox.evox.security.config;


import com.evox.evox.security.jwt.JwtFilter;
import com.evox.evox.security.repository.SecurityContextRepository;
import io.netty.handler.codec.http.HttpMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class MainSecurity {

    private final SecurityContextRepository securityContextRepository;
   @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http, JwtFilter jwtFilter) {
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/auth/**").permitAll()
                .pathMatchers("/api/**").hasAnyAuthority("ROLE_USER")
                .anyExchange().authenticated()
                .and()
                .addFilterAfter(jwtFilter, SecurityWebFiltersOrder.FIRST)
                .securityContextRepository(securityContextRepository)
                .formLogin().disable()
                .logout().disable()
                .httpBasic().disable()
                .build();
    }



}
