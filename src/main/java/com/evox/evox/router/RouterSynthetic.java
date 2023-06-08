package com.evox.evox.router;

import com.evox.evox.handler.SyntheticHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Slf4j
public class RouterSynthetic {

    private static final String PATH = "api/synthetic/";

    @Bean
    RouterFunction<ServerResponse> syntheticRouter(SyntheticHandler handler) {
        return RouterFunctions.route()
                .GET(PATH +"list", handler::getAllSyntheticsUsers)
                .GET(PATH+"accountStatus" , handler::accountStatusSynthetic)
                .GET(PATH+"transaction" , handler::getSyntheticId)
                .PATCH(PATH+"active/{transaction}" , handler::activateAccount)
                .PATCH(PATH+"invalid/{transaction}" , handler::invalidTransaction)
                .POST(PATH+"transaction" , handler::registrationTransaction)
                .POST(PATH+"registerAccount" , handler::registerAccount)
                .GET(PATH+"access" , handler::getAllSyntheticAccess)
                .PUT(PATH+"stateAccount/{id}" , handler::stateAccount)
                .build();
    }
}
