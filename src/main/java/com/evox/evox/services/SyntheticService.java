package com.evox.evox.services;

import com.evox.evox.exception.CustomException;
import com.evox.evox.model.Synthetics;
import com.evox.evox.repository.SyntheticsRepository;
import com.evox.evox.repository.UserRepository;
import com.evox.evox.security.jwt.JwtProvider;
import com.evox.evox.utils.Response;
import com.evox.evox.utils.enums.TypeStateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SyntheticService {
    private final SyntheticsRepository syntheticsRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public Mono<Synthetics> accountActivation(String transaction) {
        return syntheticsRepository.findByTransaction(transaction)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Ocurrio un error con la activacion de la cuenta", TypeStateResponse.Warning)))
                .flatMap(ele -> {
                    ele.setId(ele.getId());
                    ele.setCreatedAt(LocalDateTime.now());
                    ele.setExpirationDate(ele.getCreatedAt().plusMonths(1));
                    ele.setState(true);
                    return syntheticsRepository.save(ele);
                });

    }


    public Mono<Synthetics> registrationTransaction(Synthetics synthetics, String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .flatMap(ele -> {
                    synthetics.setUserId(ele.getId());
                    return syntheticsRepository.save(synthetics);
                });
    }

    public Flux<Synthetics> getAllSynthetics() {
        return syntheticsRepository.findAll();
    }

    public Mono<Response> saveAccount(Synthetics synthetics, String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .flatMap(user -> syntheticsRepository.findAll()
                        .filter(ele -> ele.getUserId().equals(user.getId()))
                        .count()
                        .flatMap(ele -> {
                            if (ele >= 2) {
                                return Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Ya tienes dos cuentas registradas!", TypeStateResponse.Warning));
                            }
                            return syntheticsRepository.save(synthetics)
                                    .map(data -> new Response(TypeStateResponse.Success, "Cuenta vinculada exitosamente!"));
                        }));
    }


}
