package com.evox.evox.security.router;

import com.evox.evox.security.handler.AuthHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Slf4j
public class RouterAuth {

    private static final String PATH = "api/auth/";

    @Bean
    RouterFunction<ServerResponse> authRouter(AuthHandler handler) {
        return RouterFunctions.route()
                .POST(PATH + "login", handler::login)
                .POST(PATH + "create", handler::create)
                .POST(PATH+ "recover" , handler::passwordRecovery)
                .GET(PATH+"validator/{token}", handler::validateToken)
                .GET(PATH+"validate" , handler::validateBearerToken)
                .GET(PATH+"activateAccount/{token}" , handler::activateAccount)
                .PATCH(PATH+"passwordChange/{token}" ,handler::passwordChange)
                .build();
    }
}
