package com.evox.evox.services;

import com.evox.evox.dto.SupportDto;
import com.evox.evox.exception.CustomException;
import com.evox.evox.model.Support;
import com.evox.evox.model.enums.State;
import com.evox.evox.repository.SupportRepository;
import com.evox.evox.repository.UserRepository;
import com.evox.evox.security.jwt.JwtProvider;
import com.evox.evox.utils.Response;
import com.evox.evox.utils.Utils;
import com.evox.evox.utils.enums.TypeStateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SupportService {
    private final SupportRepository supportRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public Flux<SupportDto> getAllSupport() {
        return supportRepository.findAll()
                .flatMap(ele -> userRepository.findById(ele.getId())
                        .map(data -> new SupportDto(ele.getId(),
                                ele.getCategory(),
                                ele.getQuestion(),
                                ele.getAnswer(),
                                data.getUsername(),
                                data.getEmail(),
                                ele.getUrlPhoto(),
                                ele.getState(),
                                ele.getCreatedAt(),
                                ele.getUpdatedAt())));

    }

    public Flux<SupportDto> getAllSupportUser(String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "Error filtrando el usuario! ", TypeStateResponse.Error)))
                .flatMapMany(ele -> supportRepository.findAll()
                        .filter(data -> data.getUserId().equals(ele.getId()))
                        .map(support -> new SupportDto(support.getId(),
                                support.getCategory(),
                                support.getState(),
                                support.getCreatedAt(),
                                support.getUpdatedAt())));
    }

    public Mono<Support> getAllSupportId(Integer id) {
        return supportRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No existe un ticket de soporte con esta informacion!", TypeStateResponse.Warning)));
    }


    public Mono<Response> saveSupport(Support support, String token) {
        String username = jwtProvider.extractToken(token);
        return userRepository.findByUsername(username)
                .flatMap(ele -> {
                    support.setTicket(Utils.uid());
                    support.setUserId(ele.getId());
                    support.setState(State.Pending);
                    return supportRepository.save(support)
                            .thenReturn(new Response(TypeStateResponse.Success, "Solicitud enviada!"));
                });

    }

    public Mono<Response> editSupport(Support support, Integer id) {
        return supportRepository.findById(id)
                .switchIfEmpty(Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "No existe un ticket de soporte con esta informacion!", TypeStateResponse.Warning)))
                .flatMap(ele -> supportRepository.save(support)
                        .thenReturn(new Response(TypeStateResponse.Success, "Mensaje enviado")));
    }


}

