package com.evox.evox.handler;

import com.evox.evox.dto.ReferralsDto;
import com.evox.evox.dto.TokenDto;
import com.evox.evox.dto.UserDto;
import com.evox.evox.model.User;
import com.evox.evox.services.UserServices;
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

    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userServices.getAllUses(), UserDto.class);

    }

    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(UserDto.class)
                .flatMap(ele -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(userServices.editUser(ele), TokenDto.class));
    }
    //:TODO METODO PARA ENVIAR
    public Mono<ServerResponse> updateLevel(ServerRequest serverRequest) {
       return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userServices.updateLevel(), User.class);
    }


}
