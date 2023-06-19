package com.evox.evox.handler;

import com.evox.evox.dto.*;
import com.evox.evox.model.User;
import com.evox.evox.services.UserServices;
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
public class UserHandler {

    private final UserServices userServices;

    public Mono<ServerResponse> referrals(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userServices.getAllReferrals(token), ReferralsDto.class);
    }

    public Mono<ServerResponse> referralsTeam(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userServices.getAllReferralsTeam(token), ReferralsDto.class);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userServices.getAllUses(), UserDto.class);

    }

    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(UserDto.class)
                .flatMap(ele -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(userServices.editUser(ele), Response.class));
    }


    public Mono<ServerResponse> getAccountSynthetic(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userServices.accountSynthetic(token), Boolean.class);

    }

    public Mono<ServerResponse> account(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userServices.account(token), AccountSyntheticsDto.class);

    }

    public Mono<ServerResponse> seedEmail(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginDto.class).flatMap(ele -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userServices.resendEmail(ele), Response.class));

    }

    public Mono<ServerResponse> saveWallet(ServerRequest serverRequest) {
        String token = serverRequest.headers().firstHeader("Authorization");
        return serverRequest.bodyToMono(User.class)
                .flatMap(ele -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(userServices.saveWallet(token, ele.getEvoxWallet()), Response.class));

    }
    public Mono<ServerResponse> getUserId(ServerRequest serverRequest){
        String token = serverRequest.headers().firstHeader("Authorization");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userServices.getUserId(token),UserDto.class);
    }

}
