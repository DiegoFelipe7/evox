package com.evox.evox.router;

import com.evox.evox.handler.UserHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
@Configuration
@Slf4j
public class RouterUser {

    private static final String PATH = "api/users/";
    //:TODO ELIMINAR HANDLER
    @Bean
    RouterFunction<ServerResponse> userRouter(UserHandler handler) {
        return RouterFunctions.route()
                .GET(PATH+"list" , handler::getAllUsers)
                .GET(PATH +"referrals", handler::referrals)
                .GET(PATH +"referrals/team", handler::referralsTeam)
                .PUT(PATH+"edit",handler::updateUser)
                .GET(PATH+"level",handler::updateLevel)
                .build();
    }
}
