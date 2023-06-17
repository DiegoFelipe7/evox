package com.evox.evox.handler;

import com.evox.evox.model.Payments;
import com.evox.evox.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentHandler {
    private final PaymentService paymentService;

    public Mono<ServerResponse> listPayment(ServerRequest serverRequest){
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentService.listPaymentsUser(token), Payments.class);
    }
}
