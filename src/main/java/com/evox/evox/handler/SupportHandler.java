package com.evox.evox.handler;

import com.evox.evox.dto.ListSyntheticUsersDto;
import com.evox.evox.dto.SupportDto;
import com.evox.evox.dto.SyntheticAccessDto;
import com.evox.evox.model.Support;
import com.evox.evox.model.Synthetics;
import com.evox.evox.services.SupportService;
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
public class SupportHandler {
    private final SupportService supportService;

    public Mono<ServerResponse> getAllSupport(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(supportService.getAllSupport(), SupportDto.class);
    }

    public Mono<ServerResponse> getAllSupportUsers(ServerRequest serverRequest){
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(supportService.getAllSupportUser(token), SyntheticAccessDto.class);
    }


    public Mono<ServerResponse> registrationSupport(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");
        return serverRequest
                .bodyToMono(Support.class)
                .flatMap(ele -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(supportService.saveSupport(ele, token), Response.class));
    }


    public Mono<ServerResponse> registrationResponse(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");
        return serverRequest
                .bodyToMono(Support.class)
                .flatMap(ele -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(supportService.saveSupport(ele, token), Response.class));
    }

}
