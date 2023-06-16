package com.evox.evox.handler;

import com.evox.evox.dto.ListBridgeFundUsersDto;
import com.evox.evox.model.*;
import com.evox.evox.model.enums.AccountState;
import com.evox.evox.services.BridgeFundService;
import com.evox.evox.utils.Response;
import com.evox.evox.validation.ObjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BridgeFundsHandler {
    private final BridgeFundService bridgeFundService;
    private final ObjectValidator objectValidator;

    public Mono<ServerResponse> getAllBridgeAccountType(ServerRequest serverRequest){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bridgeFundService.getBridgeAccountType(), BridgeAccountType.class);
    }


    public Mono<ServerResponse> getAllBridgeFundsUsers(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bridgeFundService.getAllBridgeFunds(), ListBridgeFundUsersDto.class);
    }


    public Mono<ServerResponse> activateAccount(ServerRequest serverRequest) {
        String transaction = serverRequest.pathVariable("transaction");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bridgeFundService.accountActivation(transaction), BridgeFunds.class);
    }

    public Mono<ServerResponse> registrationTransaction(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");
        return serverRequest
                .bodyToMono(BridgeFunds.class).doOnNext(objectValidator::validate)
                .flatMap(ele -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(bridgeFundService.registrationTransaction(ele, token), Response.class));
    }


    public Mono<ServerResponse> accountStatusBridgeFunds(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bridgeFundService.getStateUser(token), AccountState.class);

    }
    public Mono<ServerResponse> invalidTransaction(ServerRequest serverRequest){
        String transaction = serverRequest.pathVariable("transaction");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bridgeFundService.invalidTransaction(transaction) , Response.class);
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<ServerResponse> saveAccounts(ServerRequest serverRequest){
        Integer id = Integer.valueOf(serverRequest.pathVariable("id"));
        return serverRequest.bodyToMono(new ParameterizedTypeReference<List<BridgeFundsAccount>>() {})
                .doOnNext(objectValidator::validateList)
                .flatMap(ele->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(bridgeFundService.saveAccountBridgeFunds(ele , id) , Response.class));
    }

    public Mono<ServerResponse> validateRegistration(ServerRequest serverRequest){
        Integer id = Integer.valueOf(serverRequest.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bridgeFundService.getValidateRegistration(id) , Boolean.class);
    }

    public Mono<ServerResponse> getAccounts(ServerRequest serverRequest){
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bridgeFundService.getAccountsBridgeFunds(token) , BridgeFundsAccount.class);
    }

    public Mono<ServerResponse> getTransaction(ServerRequest serverRequest){
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(bridgeFundService.getTransaction(token), BridgeFunds.class);
    }
    //TODO:eliminar esto
//    public Mono<ServerResponse> getTransactionaaaaaaa(ServerRequest serverRequest){
//        return ServerResponse.ok()
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(bridgeFundService.registerPayment(), Payments.class);
//    }

}
