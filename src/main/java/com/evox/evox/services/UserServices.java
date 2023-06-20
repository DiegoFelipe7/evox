package com.evox.evox.services;


import com.evox.evox.dto.*;
import com.evox.evox.exception.CustomException;
import com.evox.evox.model.AccountSynthetics;
import com.evox.evox.model.User;
import com.evox.evox.repository.AccountSyntheticsRepository;
import com.evox.evox.repository.UserRepository;
import com.evox.evox.security.jwt.JwtProvider;
import com.evox.evox.security.service.EmailService;
import com.evox.evox.utils.Response;
import com.evox.evox.utils.enums.TypeStateResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;


@Service
@RequiredArgsConstructor
public class UserServices {
    private final UserRepository repository;
    private final AccountSyntheticsRepository accountSyntheticsRepository;
    private final ModelMapper modelMapper;

    private final JwtProvider jwtProvider;

    private final EmailService emailService;

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
                .filter(ele -> !ele.getUsername().equals(userName))
                .map(data -> new ReferralsDto(data.getFullName(), data.getLevel(), data.getUsername(), data.getCreatedAt()));
    }

    public Flux<UserDto> getAllUses() {
        return repository.findAll()
                .map(ele -> modelMapper.map(ele, UserDto.class));
    }


    public Mono<Response> editUser(UserDto userDto) {
        return repository.findByEmail(userDto.getEmail())
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Se ha producido un error, inténtelo de nuevo.", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    ele.setId(ele.getId());
                    ele.setPhoto(userDto.getPhoto());
                    ele.setPhone(userDto.getPhone());
                    ele.setCountry(userDto.getCountry());
                    ele.setCity(userDto.getCity());
                    return repository.save(ele)
                            .thenReturn(new Response(TypeStateResponse.Success, "Datos actualizados"));
                });
    }

    public Mono<Boolean> accountSynthetic(String token) {
        String userName = jwtProvider.extractToken(token);
        return repository.findByUsername(userName)
                .map(ele -> ele.getAccountSynthetics() != null)
                .defaultIfEmpty(false);
    }

    public Mono<AccountSyntheticsDto> account(String token) {
        String username = jwtProvider.extractToken(token);
        return repository.findByUsername(username)
                .flatMap(ele -> {
                    if (ele.getAccountSynthetics() != null) {
                        return accountSyntheticsRepository.findById(ele.getAccountSynthetics())
                                .map(data -> modelMapper.map(data, AccountSyntheticsDto.class));
                    } else {
                        return Mono.empty();
                    }
                });
    }

    public Mono<Response> resendEmail(LoginDto loginDto) {
        return repository.findByEmail(loginDto.getEmail())
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "El email no esta registrado en la bd", TypeStateResponse.Error)))
                .flatMap(ele -> emailService.sendEmailWelcome(ele.getFullName(), ele.getEmail(), ele.getToken())
                        .then(Mono.just(new Response(TypeStateResponse.Success, "Hemos enviado un correo electrónico para la activacion de tu cuenta!" + ele.getFullName()))));
    }

    public Mono<Response> saveWallet(String token, String evoxWallet) {
        String username = jwtProvider.extractToken(token);
        return repository.findByEvoxWallet(evoxWallet)
                .flatMap(err -> Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Esta billetera ya está registrada!", TypeStateResponse.Error)))
                .switchIfEmpty(repository.findByUsername(username)
                        .flatMap(ele -> {
                            ele.setId(ele.getId());
                            ele.setEvoxWallet(evoxWallet);
                            return repository.save(ele)
                                    .thenReturn(new Response(TypeStateResponse.Success, "Billetera registrada"))
                                    .onErrorResume(throwable -> Mono.just(new Response(TypeStateResponse.Error, "Error al guardar la billetera")));
                        })).cast(Response.class);
    }

    public Mono<UserDto> getUserId(String token) {
        String username = jwtProvider.extractToken(token);
        return repository.findByUsername(username)
                .map(ele -> modelMapper.map(ele, UserDto.class));
    }
}
