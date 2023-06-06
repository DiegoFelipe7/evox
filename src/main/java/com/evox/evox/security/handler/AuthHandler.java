package com.evox.evox.security.handler;

import com.evox.evox.dto.LoginDto;
import com.evox.evox.dto.TokenDto;
import com.evox.evox.exception.CustomException;
import com.evox.evox.model.User;
import com.evox.evox.security.service.AuthService;
import com.evox.evox.utils.Response;
import com.evox.evox.utils.enums.TypeStateResponse;
import com.evox.evox.validation.ObjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthHandler {

    private final AuthService authService;
    private final ObjectValidator objectValidator;

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginDto.class)
                .flatMap(ele -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(authService.login(ele), TokenDto.class));

    }

    public Mono<ServerResponse> create(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(User.class)
                .flatMap(ele -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(authService.accountRegistration(ele), Response.class));
    }

    public Mono<ServerResponse> passwordRecovery(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginDto.class)
                .flatMap(ele -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(authService.passwordRecovery(ele), Response.class));
    }

    public Mono<ServerResponse> validateToken(ServerRequest serverRequest) {
        String token = serverRequest.pathVariable("token");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.tokenValidation(token), Boolean.class);
    }

    public Mono<ServerResponse> passwordChange(ServerRequest serverRequest) {
        String token = serverRequest.pathVariable("token");
        return serverRequest.bodyToMono(LoginDto.class)
                .flatMap(ele -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(authService.passwordChange(token, ele), Response.class));
    }

    public Mono<ServerResponse> validateBearerToken(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(authService.validateToken(token), Boolean.class);
        } else {
            return Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Token invalid", TypeStateResponse.Error));
        }
    }
    public Mono<ServerResponse> activateAccount(ServerRequest serverRequest) {
        String token = serverRequest.pathVariable("token");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.activateAccount(token), Response.class);
    }

    public Mono<ServerResponse> send (ServerRequest serverRequest){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(authService.resendEmail(), Response.class);
    }
}
