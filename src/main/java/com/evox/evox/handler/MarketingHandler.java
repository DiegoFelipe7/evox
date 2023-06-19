package com.evox.evox.handler;

import com.evox.evox.model.Marketing;
import com.evox.evox.model.PackagesAccounts;
import com.evox.evox.services.MarketingService;
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
}

