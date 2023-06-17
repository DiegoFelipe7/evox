package com.evox.evox.router;

import com.evox.evox.handler.PaymentHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Slf4j
public class RouterPayment {

    private static final String PATH = "api/payment/";
    @Bean
     RouterFunction<ServerResponse> paymentsRouter (PaymentHandler handler) {
        return RouterFunctions.route()
                .GET(PATH +"list", handler::listPayment)
                .build();
    }
}
