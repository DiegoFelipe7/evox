package com.evox.evox.services;


import com.evox.evox.dto.ReferralsDto;
import com.evox.evox.dto.TokenDto;
import com.evox.evox.dto.UserDto;
import com.evox.evox.exception.CustomException;
import com.evox.evox.repository.UserRepository;
import com.evox.evox.security.jwt.JwtProvider;
import com.evox.evox.utils.enums.TypeStateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;


@Service
@RequiredArgsConstructor
public class UserServices {
    private final UserRepository repository;
    private final JwtProvider jwtProvider;

    public Flux<ReferralsDto> getAllReferrals(String token) {
        String userName = jwtProvider.extractToken(token);
        return repository.findByUsername(userName)
                .flatMapMany(user -> repository.findAll()
                        .filter(ele -> Objects.equals(ele.getParentId(), user.getId()))
                        .filter(ele -> !ele.getUsername().equals(userName))
                        .map(data -> new ReferralsDto(data.getFullName(), data.getPhone(), data.getUsername(), data.getCreatedAt())));
    }

    public Flux<ReferralsDto> getAllReferralsTeam(String token) {
        String userName = jwtProvider.extractToken(token);
        return repository.findUserAndDescendantsTeam(userName)
                .filter(ele->!ele.getUsername().equals(userName))
                .map(data -> new ReferralsDto(data.getFullName(), data.getLevel(), data.getUsername(), data.getCreatedAt()));
    }


    public Mono<TokenDto> editUser(UserDto userDto) {
       return repository.findByEmail(userDto.getEmail())
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Se ha producido un error, inténtelo de nuevo.", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    ele.setId(ele.getId());
                    ele.setPhoto(userDto.getPhoto());
                    ele.setPhone(userDto.getPhone());
                    ele.setCountry(userDto.getCountry());
                    ele.setCity(userDto.getCity());
                     return repository.save(ele).map(data-> new TokenDto(jwtProvider.generateToken(data)));
                });
    }

}
