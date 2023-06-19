package com.evox.evox.router;

import com.evox.evox.handler.MarketingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Slf4j
public class RouterMarketing {
    private final static String PATH="api/marketing/";
    @Bean
    RouterFunction<ServerResponse> marketingRouter(MarketingHandler handler){
        return RouterFunctions.route()
                .GET(PATH+"list" , handler::marketingListAccount)
                .POST(PATH+"saveTransaction" , handler::saveMarketing)
                .build();
    }
}
