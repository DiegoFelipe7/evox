package com.evox.evox.router;

import com.evox.evox.handler.BridgeFundsHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Slf4j
public class RouterBridgeFunds {
    private static final String PATH = "api/bridgeFunds/";

    @Bean
    RouterFunction<ServerResponse> bridgeFundsRouter(BridgeFundsHandler handler) {
        return RouterFunctions.route()
                .GET(PATH +"list", handler::getAllBridgeAccountType)
                .GET(PATH+"accountStatus" , handler::accountStatusBridgeFunds)
                .GET(PATH+"list/users" , handler::getAllBridgeFundsUsers)
                .POST(PATH+"transaction" , handler::registrationTransaction)
                .PATCH(PATH+"active/{transaction}" , handler::activateAccount)
                .PATCH(PATH+"invalid/{transaction}" , handler::invalidTransaction)
                .build();
    }
}
