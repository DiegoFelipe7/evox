package com.evox.evox.handler;

import com.evox.evox.dto.ListSyntheticUsersDto;
import com.evox.evox.model.AccountSynthetics;
import com.evox.evox.model.Synthetics;
import com.evox.evox.model.enums.AccountState;
import com.evox.evox.services.AccountSyntheticsService;
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
    private final AccountSyntheticsService accountSyntheticsService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<ServerResponse> getAllSyntheticsUsers(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(syntheticService.getAllSynthetics(), ListSyntheticUsersDto.class);
    }


    public Mono<ServerResponse> activateAccount(ServerRequest serverRequest) {
        String transaction = serverRequest.pathVariable("transaction");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(syntheticService.accountActivation(transaction), Synthetics.class);
    }

    public Mono<ServerResponse> registrationTransaction(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");
        return serverRequest
                .bodyToMono(Synthetics.class)
                .flatMap(ele -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(syntheticService.registrationTransaction(ele, token), Response.class));
    }

    public Mono<ServerResponse> accountStatusSynthetic(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(syntheticService.getStateUser(token), AccountState.class);

    }
    public Mono<ServerResponse> registerAccount(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");
        return serverRequest.bodyToMono(AccountSynthetics.class)
                .flatMap(ele -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(accountSyntheticsService.registerSyntheticAccount(token, ele), Response.class));
    }

    public Mono<ServerResponse> invalidTransaction(ServerRequest serverRequest){
        String transaction = serverRequest.pathVariable("transaction");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(syntheticService.invalidTransaction(transaction) , Response.class);
    }

    public Mono<ServerResponse> getSyntheticId(ServerRequest serverRequest){
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(syntheticService.getIdSyntheticUser(token), Synthetics.class);
    }

}
