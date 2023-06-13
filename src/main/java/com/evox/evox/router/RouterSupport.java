package com.evox.evox.router;

import com.evox.evox.handler.SupportHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Slf4j
public class RouterSupport {
    private static final String PATH = "api/support/";
    @Bean
    RouterFunction<ServerResponse> supportRouter(SupportHandler handler) {
        return RouterFunctions.route()
                .GET(PATH +"list", handler::getAllSupport)
                .GET(PATH +"user/{id}", handler::getAllSupportId)
                .GET(PATH +"list/users", handler::getAllSupportUsers)
                .POST(PATH+"save" , handler::registrationSupport)
                .PATCH(PATH+"edit/{id}" , handler::editSupport)
                .build();
    }
}
