package com.evox.evox.handler;

import com.evox.evox.model.Synthetics;
import com.evox.evox.services.SyntheticService;
import com.evox.evox.utils.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class SyntheticHandler {
    private final SyntheticService syntheticService;
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Mono<ServerResponse> getAllSyntheticsUsers(ServerRequest serverRequest){
        return  ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(syntheticService.getAllSynthetics(), Synthetics.class);
    }

    public Mono<ServerResponse> activateAccount(ServerRequest serverRequest){
        String transaction = serverRequest.pathVariable("transaction");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(syntheticService.accountActivation(transaction) , Synthetics.class);
    }
    public Mono<ServerResponse> saveSynthetic(ServerRequest serverRequest){
        String token = serverRequest.headers().firstHeader("Authorization");
        return serverRequest
                .bodyToMono(Synthetics.class)
                .flatMap(ele->ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(syntheticService.saveAccount(ele, token) , Response.class));
    }

    public Mono<ServerResponse> registrationTransaction(ServerRequest serverRequest){
        String token = serverRequest.headers().firstHeader("Authorization");
        return serverRequest
                .bodyToMono(Synthetics.class)
                .flatMap(ele->ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(syntheticService.registrationTransaction(ele,token) , Response.class));
    }
}
