package com.evox.evox.handler;

import com.evox.evox.model.Marketing;
import com.evox.evox.model.PackagesAccounts;
import com.evox.evox.model.Synthetics;
import com.evox.evox.model.enums.AccountState;
import com.evox.evox.services.MarketingService;
import com.evox.evox.utils.Response;
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
public class MarketingHandler {
    private final MarketingService marketingService;


    public Mono<ServerResponse> marketingListAccount(ServerRequest serverRequest){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(marketingService.findAllMarketingAccount(), PackagesAccounts.class);
    }
    public Mono<ServerResponse> saveMarketing(ServerRequest serverRequest){
        String token = serverRequest.headers().firstHeader("Authorization");
        return serverRequest.bodyToMono(Marketing.class)
                .flatMap(ele->ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(marketingService.saveTransaction(ele, token), Marketing.class));
    }

    public Mono<ServerResponse> marketingState(ServerRequest serverRequest){
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(marketingService.getStateMarketingUser(token), AccountState.class);
    }

    public Mono<ServerResponse> getMarketingId(ServerRequest serverRequest){
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(marketingService.getIdEvoxMarketingUser(token), AccountState.class);
    }


    public Mono<ServerResponse> activateAccount(ServerRequest serverRequest) {
        String transaction = serverRequest.pathVariable("transaction");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(marketingService.accountActivation(transaction), Synthetics.class);
    }


    public Mono<ServerResponse> invalidTransaction(ServerRequest serverRequest){
        String transaction = serverRequest.pathVariable("transaction");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(marketingService.invalidTransaction(transaction) , Response.class);
    }


    public Mono<ServerResponse> getAllMarketing(ServerRequest serverRequest){
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(marketingService.getAllMarketing(), Synthetics.class);
    }

}

